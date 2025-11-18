package com.Miniproject1;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;

    public class Library {

        private HashMap<String, Book> books = new HashMap<>();
        private HashMap<String, Member> members = new HashMap<>();
        private HashMap<String, LocalDate> issuedDates = new HashMap<>();

        private static final int MAX_DAYS = 7;
        private static final double LATE_FEE_PER_DAY = 5.0;

        private FileWriter writer;

        public Library() {
            try {
                writer = new FileWriter("library_log.txt", true);
            } catch (IOException e) {
                System.out.println("Could not create log file.");
            }
        }

        private void log(String msg) {
            try {
                writer.write(LocalDate.now() + ": " + msg + "\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println("Error logging.");
            }
        }

        public void addBook(Book book) {
            books.put(book.getId(), book);
            log("Added book: " + book.getId());
        }

        public void addMember(Member member) {
            members.put(member.getId(), member);
            log("Added member: " + member.getId());
        }

        public void issueBook(String bookId, String memberId) {
            Book book = books.get(bookId);

            if (book == null) {
                System.out.println("Book not found.");
                return;
            }
            if (book.isIssued()) {
                System.out.println("Book already issued.");
                return;
            }

            book.issue();
            issuedDates.put(bookId, LocalDate.now());
            log("Issued book " + bookId + " to " + memberId);

            System.out.println("Book issued successfully.");
        }

        public void returnBook(String bookId) throws LateReturnException {
            Book book = books.get(bookId);

            if (book == null) {
                throw new LateReturnException("Attempted to return non-existent book.");
            }
            if (!book.isIssued()) {
                throw new LateReturnException("Book was not issued.");
            }

            LocalDate issuedDate = issuedDates.get(bookId);
            long days = java.time.temporal.ChronoUnit.DAYS.between(issuedDate, LocalDate.now());

            double lateFee = 0;
            if (days > MAX_DAYS) {
                lateFee = (days - MAX_DAYS) * LATE_FEE_PER_DAY;
            }

            book.returned();
            issuedDates.remove(bookId);
            log("Returned book " + bookId + ", late fee: " + lateFee);

            System.out.println("Book returned. Late fee: " + lateFee);
        }

        public void showBooks() {
            books.values().forEach(System.out::println);
        }
    }


