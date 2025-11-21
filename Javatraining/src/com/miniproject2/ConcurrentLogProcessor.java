package com.miniproject2;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.stream.Collectors;

    public class ConcurrentLogProcessor {

        private static final String TOKEN_REGEX = "[^A-Za-z0-9']+";

        public static void main(String[] args) throws Exception {
            if (args.length < 2) {
                System.out.println("Usage: java ConcurrentLogProcessor <folderPath> <numThreads> [resultFile]");
                System.exit(1);
            }

            Path folder = Paths.get(args[0]);
            int numThreads = Integer.parseInt(args[1]);
            String resultFile = args.length >= 3 ? args[2] : "results.txt";

            if (!Files.isDirectory(folder)) {
                System.err.println("Not a directory: " + folder);
                System.exit(2);
            }

            // Collect log files
            List<Path> files;
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder, "*")) {
                files = new ArrayList<>();
                for (Path p : ds) {
                    String name = p.getFileName().toString().toLowerCase();
                    if (Files.isRegularFile(p) &&
                            (name.endsWith(".txt") || name.endsWith(".log") || name.endsWith(".out"))) {
                        files.add(p);
                    }
                }
            }

            if (files.isEmpty()) {
                System.out.println("No log files found in: " + folder);
                System.exit(0);
            }

            System.out.println("Found " + files.size() + " files. Starting benchmark...");

            // Sequential run
            Map<String, Long> seq = runSequential(files);

            // Concurrent run
            Map<String, Long> conc = runConcurrent(files, numThreads);

            // Compare
            boolean equal = seq.equals(conc);

            // Write results
            writeResults(resultFile, seq, conc, equal);
            System.out.println("Done. Results saved to: " + resultFile);
        }

        private static Map<String, Long> runSequential(List<Path> files) {
            System.out.println("\n--- SEQUENTIAL RUN ---");
            long start = System.nanoTime();
            Map<String, Long> aggregate = new HashMap<>();

            for (Path p : files) {
                try {
                    Map<String, Long> result = processFile(p);
                    result.forEach((k, v) -> aggregate.merge(k, v, Long::sum));
                } catch (IOException e) {
                    System.err.println("Error: " + p + ": " + e.getMessage());
                }
            }

            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            System.out.println("Sequential time: " + elapsed + " ms");
            aggregate.put("__SEQUENTIAL_MS__", elapsed);

            printTop(aggregate, 10);
            return aggregate;
        }

        private static Map<String, Long> runConcurrent(List<Path> files, int numThreads) throws Exception {
            System.out.println("\n--- CONCURRENT RUN (" + numThreads + " threads) ---");

            long start = System.nanoTime();
            ConcurrentHashMap<String, LongAdder> aggregate = new ConcurrentHashMap<>();

            ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);

            // Monitor thread pool
            ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor();
            monitor.scheduleAtFixedRate(() -> {
                System.out.println("[POOL MONITOR] active=" + pool.getActiveCount()
                        + " poolSize=" + pool.getPoolSize()
                        + " queued=" + pool.getQueue().size()
                        + " completed=" + pool.getCompletedTaskCount());
            }, 0, 500, TimeUnit.MILLISECONDS);

            List<Callable<Void>> tasks = new ArrayList<>();
            for (Path p : files) {
                tasks.add(() -> {
                    Map<String, Long> local = processFile(p);
                    local.forEach((k, v) ->
                            aggregate.computeIfAbsent(k, x -> new LongAdder()).add(v));
                    return null;
                });
            }

            // Submit and wait
            List<Future<Void>> futures = new ArrayList<>();
            for (Callable<Void> c : tasks) futures.add(pool.submit(c));
            for (Future<Void> f : futures) f.get();

            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.MINUTES);
            monitor.shutdownNow();

            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            System.out.println("Concurrent time: " + elapsed + " ms");

            Map<String, Long> result = aggregate.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().sum()));

            result.put("__CONCURRENT_MS__", elapsed);
            printTop(result, 10);

            return result;
        }

        private static Map<String, Long> processFile(Path p) throws IOException {
            try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
                return br.lines()
                        .flatMap(line -> Arrays.stream(line.split(TOKEN_REGEX)))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(String::toLowerCase)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            }
        }

        private static void printTop(Map<String, Long> map, int top) {
            System.out.println("Top " + top + " words:");
            map.entrySet().stream()
                    .filter(e -> !e.getKey().startsWith("__"))
                    .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                    .limit(top)
                    .forEach(e -> System.out.println("  " + e.getKey() + " = " + e.getValue()));
        }

        private static void writeResults(
                String filename, Map<String, Long> seq, Map<String, Long> conc, boolean equal) {
            try (BufferedWriter w = Files.newBufferedWriter(Paths.get(filename))) {

                w.write("--- SEQUENTIAL ---\n");
                long seqMs = seq.get("__SEQUENTIAL_MS__");
                seq.entrySet().stream()
                        .filter(e -> !e.getKey().startsWith("__"))
                        .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                        .forEach(e -> {
                            try { w.write(e.getKey() + "=" + e.getValue() + "\n"); } catch (Exception ignored) {}
                        });
                w.write("SEQUENTIAL_MS=" + seqMs + "\n\n");

                w.write("--- CONCURRENT ---\n");
                long concMs = conc.get("__CONCURRENT_MS__");
                conc.entrySet().stream()
                        .filter(e -> !e.getKey().startsWith("__"))
                        .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                        .forEach(e -> {
                            try { w.write(e.getKey() + "=" + e.getValue() + "\n"); } catch (Exception ignored) {}
                        });
                w.write("CONCURRENT_MS=" + concMs + "\n\n");

                w.write("EQUAL_RESULTS=" + equal + "\n");

            } catch (IOException e) {
                System.err.println("Error writing results: " + e.getMessage());
            }
        }
    }


