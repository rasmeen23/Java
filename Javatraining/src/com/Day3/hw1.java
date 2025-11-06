package com.Day3;

public class hw1 {
        public static void main(String[] args) {
            // Check if no command line arguments are passed
            if (args.length == 0) {
                System.out.println("No values");
            } else {
                // Print all arguments separated by commas
                for (int i = 0; i < args.length; i++) {
                    System.out.print(args[i]);
                    // Add comma except after the last value
                    if (i < args.length - 1) {
                        System.out.print(",");
                    }
                }
            }
        }
    }

}
