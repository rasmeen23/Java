package com.Miniproject1;

    import java.util.Scanner;

    public class Main {
        public static void main(String[] args) {
            Library lib = new Library();
            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println("\n===== Library Menu =====");
                System.out.println("1. Add Book");
                System.out.println("2. Add Member");
                System.out.println("3. Issue Book");
                System.out.println("4. Return Book");
                System.out.println("5. Show All Books");
                System.out.println("0. Exit");
                System.out.print("Choose: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Book ID: ");
                        String bid = sc.nextLine();
                        System.out.print("Title: ");
                        String title = sc.nextLine();
                        System.out.print("Author: ");
                        String author = sc.nextLine();
                        lib.addBook(new Book(bid, title, author));
                        break;

                    case 2:
                        System.out.print("Member ID: ");
                        String mid = sc.nextLine();
                        System.out.print("Name: ");
                        String name = sc.nextLine();
                        lib.addMember(new Member(mid, name));
                        break;

                    case 3:
                        System.out.print("Book ID: ");
                        String ibid = sc.nextLine();
                        System.out.print("Member ID: ");
                        String imid = sc.nextLine();
                        lib.issueBook(ibid, imid);
                        break;

                    case 4:
                        System.out.print("Book ID: ");
                        String rbid = sc.nextLine();
                        try {
                            lib.returnBook(rbid);
                        } catch (LateReturnException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;

                    case 5:
                        lib.showBooks();
                        break;

                    case 0:
                        System.out.println("Goodbye!");
                        sc.close();
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }
            }
        }
    }

}
