package com.Day3;

public class hw4 {
    // Base class Person
    class Person {
        String name;
        String dateOfBirth;

        // Constructor
        Person(String name, String dateOfBirth) {
            this.name = name;
            this.dateOfBirth = dateOfBirth;
        }

        // Display method
        void displayPersonDetails() {
            System.out.println("Name: " + name);
            System.out.println("Date of Birth: " + dateOfBirth);
        }
    }

    // Derived class Teacher
    class Teacher extends Person {
        double salary;
        String subject;

        // Constructor
        Teacher(String name, String dateOfBirth, double salary, String subject) {
            super(name, dateOfBirth);
            this.salary = salary;
            this.subject = subject;
        }

        // Display method
        void displayTeacherDetails() {
            displayPersonDetails();
            System.out.println("Subject: " + subject);
            System.out.println("Salary: " + salary);
        }
    }

    // Derived class Student
    class Student extends Person {
        int studentId;

        // Constructor
        Student(String name, String dateOfBirth, int studentId) {
            super(name, dateOfBirth);
            this.studentId = studentId;
        }

        // Display method
        void displayStudentDetails() {
            displayPersonDetails();
            System.out.println("Student ID: " + studentId);
        }
    }

    // Derived class CollegeStudent
    class CollegeStudent extends Student {
        String collegeName;
        String year; // first, second, third, etc.

        // Constructor
        CollegeStudent(String name, String dateOfBirth, int studentId, String collegeName, String year) {
            super(name, dateOfBirth, studentId);
            this.collegeName = collegeName;
            this.year = year;
        }

        // Display method
        void displayCollegeStudentDetails() {
            displayStudentDetails();
            System.out.println("College Name: " + collegeName);
            System.out.println("Year: " + year);
        }
    }

    // Main class to test
    public class SchoolApplication {
        public static void main(String[] args) {
            // Create Person object
            Person p = new Person("John Doe", "12/03/1990");
            System.out.println("--- Person Details ---");
            p.displayPersonDetails();

            // Create Teacher object
            Teacher t = new Teacher("Ms. Smith", "05/07/1980", 55000, "Mathematics");
            System.out.println("\n--- Teacher Details ---");
            t.displayTeacherDetails();

            // Create Student object
            Student s = new Student("Alice", "15/09/2005", 101);
            System.out.println("\n--- Student Details ---");
            s.displayStudentDetails();

            // Create CollegeStudent object
            CollegeStudent cs = new CollegeStudent("Bob", "20/01/2003", 202, "ABC College", "Second Year");
            System.out.println("\n--- College Student Details ---");
            cs.displayCollegeStudentDetails();
        }
    }

}
