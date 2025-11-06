package com.Day3;

public class hw3 {
        public static void main(String[] args) {
            // Initialize a character variable
            char ch = 'A';  // You can change this to test other characters

            // Check and print the result
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                System.out.println("Alphabet");
            } else if (ch >= '0' && ch <= '9') {
                System.out.println("Digit");
            } else {
                System.out.println("Special Character");
            }
        }
    }

}
