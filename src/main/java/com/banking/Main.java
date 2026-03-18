package com.banking;

import java.util.Scanner;

import com.banking.exceptions.AccountNotFoundException;
import com.banking.services.BankService;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static BankService bankService;

    static {
        bankService = new BankService();
    }

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("Welcome to the Smart Banking System");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    try {
                        login();
                    } catch (AccountNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    System.out.println("Thank you for using the Smart Banking System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);
    }

    private static void register() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter a password: ");
        String password = scanner.nextLine();
        bankService.registerCustomer(name, password, password);
    }

    private static void login() throws AccountNotFoundException {
        System.out.print("Enter your account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        bankService.login(accountNumber, password);
    }
}