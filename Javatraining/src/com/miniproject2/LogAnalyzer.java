package com.miniproject2;
import java.util.*;
import java.util.concurrent.*;

public class LogAnalyzer {

        private ExecutorService executor;
        private ConcurrentHashMap<String, Integer> finalResult;

        public LogAnalyzer(int threadCount) {
            executor = Executors.newFixedThreadPool(threadCount);
            finalResult = new ConcurrentHashMap<>();
        }

        public void analyze(List<List<String>> logFiles) throws Exception {

            long start = System.currentTimeMillis();

            List<Future<Map<String, Integer>>> futureList = new ArrayList<>();

            // Submit each log file to worker thread
            for (List<String> file : logFiles) {
                LogProcessor task = new LogProcessor(file);
                futureList.add(executor.submit(task));
            }

            // Collect results from each thread
            for (Future<Map<String, Integer>> f : futureList) {
                Map<String, Integer> result = f.get();
                result.forEach((word, count) ->
                        finalResult.merge(word, count, Integer::sum));
            }

            long end = System.currentTimeMillis();

            System.out.println("\n=== FINAL RESULT ===");
            System.out.println("Total Execution Time: " + (end - start) + " ms");
            System.out.println("Keyword Counts: " + finalResult);

            executor.shutdown();
        }
    }


