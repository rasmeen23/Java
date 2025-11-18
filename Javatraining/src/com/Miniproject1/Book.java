package com.Miniproject1;

public class Book {
        private String id;
        private String title;
        private String author;
        private boolean issued;

        public Book(String id, String title, String author) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.issued = false;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public boolean isIssued() { return issued; }

        public void issue() { this.issued = true; }
        public void returned() { this.issued = false; }

        @Override
        public String toString() {
            return id + " | " + title + " | " + author + " | Issued: " + issued;
        }
    }


