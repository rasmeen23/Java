package com.Day1;

public class hw1 {
    public static void main(String[]args){
        if (args.length != 2){
            System.out.println("Please enter two command line arguments.");
            return;
        }
        String company = args[0];
        String location = args[1];
        System.out.print.ln(company + " Technologies " + location);
    }
}
