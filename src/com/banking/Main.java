package com.banking;

import java.io.Console;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the Smart Banking System.
 *
 * <p>Provides a menu-driven console interface allowing users to:
 * <ul>
 *   <li>Register a new account</li>
 *   <li>Log in to an existing account</li>
 *   <li>Deposit funds</li>
 *   <li>Withdraw funds</li>
 *   <li>Check their balance</li>
 *   <li>View transaction history</li>
 *   <li>Log out</li>
 * </ul>
 */
public class Main {

    // -------------------------------------------------------------------------
    // Menu constants
    // -------------------------------------------------------------------------

    // Main menu options
    private static final int MAIN_REGISTER = 1;
    private static final int MAIN_LOGIN    = 2;
    private static final int MAIN_EXIT     = 3;

    // Account menu options
    private static final int ACCT_DEPOSIT  = 1;
    private static final int ACCT_WITHDRAW = 2;
    private static final int ACCT_BALANCE  = 3;
    private static final int ACCT_HISTORY  = 4;
    private static final int ACCT_LOGOUT   = 5;

    private final Bank bank;
    private final Scanner scanner;

    /**
     * Creates a new Main application shell backed by the given Bank.
     *
     * @param bank the bank service to use
     */
    public Main(Bank bank) {
        this.bank = bank;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Application entry point.
     * Initialises the file system, creates the Bank, and starts the main loop.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        FileManager fileManager = new FileManager();
        fileManager.initialise();

        Bank bank = new Bank(fileManager);
        Main app = new Main(bank);
        app.run();
    }

    /**
     * Runs the main application loop, displaying the top-level menu until the
     * user chooses to exit.
     */
    public void run() {
        printBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case MAIN_REGISTER:
                    handleRegister();
                    break;
                case MAIN_LOGIN:
                    handleLogin();
                    break;
                case MAIN_EXIT:
                    System.out.println("\nThank you for using Smart Banking System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-3.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Registration
    // -------------------------------------------------------------------------

    /**
     * Prompts the user for registration details and creates a new account.
     */
    private void handleRegister() {
        System.out.println("\n--- Create New Account ---");

        String name = readNonBlankString("Enter your full name: ");
        String password = readPassword("Create a password (min 4 characters): ");

        try {
            Account account = bank.register(name, password);
            System.out.println("\nAccount created successfully!");
            System.out.println("Your Account Number: " + account.getAccountNumber());
            System.out.println("Please keep your account number safe.");
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Login
    // -------------------------------------------------------------------------

    /**
     * Prompts for credentials, authenticates the user, and starts the account menu.
     */
    private void handleLogin() {
        System.out.println("\n--- Login ---");

        String accountNumber = readNonBlankString("Enter account number: ");
        String password = readPassword("Enter password: ");

        try {
            Account account = bank.login(accountNumber, password);
            System.out.println("\nLogin successful! Welcome, " + account.getHolderName() + ".");
            runAccountMenu(account);
        } catch (IllegalArgumentException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Account menu
    // -------------------------------------------------------------------------

    /**
     * Displays the account-level menu and dispatches user actions until logout.
     *
     * @param account the currently logged-in account
     */
    private void runAccountMenu(Account account) {
        boolean loggedIn = true;
        while (loggedIn) {
            printAccountMenu(account);
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case ACCT_DEPOSIT:
                    handleDeposit(account);
                    break;
                case ACCT_WITHDRAW:
                    handleWithdraw(account);
                    break;
                case ACCT_BALANCE:
                    printBalance(account);
                    break;
                case ACCT_HISTORY:
                    printTransactionHistory(account);
                    break;
                case ACCT_LOGOUT:
                    System.out.println("\nLogged out successfully.");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-5.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Deposit
    // -------------------------------------------------------------------------

    /**
     * Prompts for a deposit amount and description, then performs the deposit.
     *
     * @param account the account to credit
     */
    private void handleDeposit(Account account) {
        System.out.println("\n--- Deposit ---");
        double amount = readPositiveDouble("Enter deposit amount: $");
        String description = readOptionalString("Enter description (or press Enter to skip): ", "Deposit");

        try {
            bank.deposit(account, amount, description);
            System.out.printf("Deposit successful. New balance: $%.2f%n", account.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Withdrawal
    // -------------------------------------------------------------------------

    /**
     * Prompts for a withdrawal amount and description, then performs the withdrawal.
     *
     * @param account the account to debit
     */
    private void handleWithdraw(Account account) {
        System.out.println("\n--- Withdraw ---");
        System.out.printf("Current balance: $%.2f%n", account.getBalance());
        double amount = readPositiveDouble("Enter withdrawal amount: $");
        String description = readOptionalString("Enter description (or press Enter to skip): ", "Withdrawal");

        try {
            bank.withdraw(account, amount, description);
            System.out.printf("Withdrawal successful. New balance: $%.2f%n", account.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Balance
    // -------------------------------------------------------------------------

    /**
     * Displays the current balance for the given account.
     *
     * @param account the account to query
     */
    private void printBalance(Account account) {
        System.out.println("\n--- Account Balance ---");
        System.out.printf("Account: %s%n", account.getAccountNumber());
        System.out.printf("Holder : %s%n", account.getHolderName());
        System.out.printf("Balance: $%.2f%n", account.getBalance());
    }

    // -------------------------------------------------------------------------
    // Transaction history
    // -------------------------------------------------------------------------

    /**
     * Prints the full transaction history for the given account.
     *
     * @param account the account whose history to display
     */
    private void printTransactionHistory(Account account) {
        System.out.println("\n--- Transaction History ---");
        List<Transaction> history = account.getTransactions();

        if (history.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            System.out.println("Account: " + account.getAccountNumber()
                    + "  |  Holder: " + account.getHolderName());
            System.out.println("-".repeat(90));
            for (Transaction tx : history) {
                System.out.println(tx);
            }
            System.out.println("-".repeat(90));
            System.out.printf("Current balance: $%.2f%n", account.getBalance());
        }
    }

    // -------------------------------------------------------------------------
    // Display helpers
    // -------------------------------------------------------------------------

    /** Prints the application welcome banner. */
    private void printBanner() {
        System.out.println("=".repeat(60));
        System.out.println("        SMART BANKING SYSTEM");
        System.out.println("        Secure · Reliable · Simple");
        System.out.println("=".repeat(60));
    }

    /** Prints the main (unauthenticated) menu. */
    private void printMainMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println(" 1. Register New Account");
        System.out.println(" 2. Login");
        System.out.println(" 3. Exit");
        System.out.println("================================");
    }

    /**
     * Prints the account (authenticated) menu, including the current balance.
     *
     * @param account the logged-in account
     */
    private void printAccountMenu(Account account) {
        System.out.println("\n========== ACCOUNT MENU ==========");
        System.out.printf(" Logged in as: %s (%s)%n",
                account.getHolderName(), account.getAccountNumber());
        System.out.printf(" Balance: $%.2f%n", account.getBalance());
        System.out.println("-----------------------------------");
        System.out.println(" 1. Deposit");
        System.out.println(" 2. Withdraw");
        System.out.println(" 3. Check Balance");
        System.out.println(" 4. Transaction History");
        System.out.println(" 5. Logout");
        System.out.println("===================================");
    }

    // -------------------------------------------------------------------------
    // Input helpers
    // -------------------------------------------------------------------------

    /**
     * Reads an integer from the console, re-prompting on invalid input.
     *
     * @param prompt the message to display before reading
     * @return the integer entered by the user
     */
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = scanner.nextInt();
                scanner.nextLine(); // consume trailing newline
                return value;
            } catch (InputMismatchException e) {
                scanner.nextLine(); // discard invalid input
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Reads a strictly positive double from the console, re-prompting on invalid input.
     *
     * @param prompt the message to display before reading
     * @return a positive double entered by the user
     */
    private double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = scanner.nextDouble();
                scanner.nextLine(); // consume trailing newline
                if (value <= 0) {
                    System.out.println("Amount must be greater than zero.");
                } else {
                    return value;
                }
            } catch (InputMismatchException e) {
                scanner.nextLine(); // discard invalid input
                System.out.println("Please enter a valid amount.");
            }
        }
    }

    /**
     * Reads a non-blank string from the console, re-prompting if the user enters nothing.
     *
     * @param prompt the message to display before reading
     * @return a non-empty trimmed string
     */
    private String readNonBlankString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    /**
     * Reads a password string (minimum 4 characters) without echoing characters to
     * the console when a {@link Console} is available, re-prompting if too short.
     *
     * @param prompt the message to display before reading
     * @return the entered password (plain text)
     */
    private String readPassword(String prompt) {
        Console console = System.console();
        while (true) {
            if (console != null) {
                // Console.readPassword() masks input — characters are not echoed
                char[] chars = console.readPassword(prompt);
                if (chars != null && chars.length >= 4) {
                    return new String(chars);
                }
            } else {
                // Fallback for environments without a TTY (e.g., IDE run configurations)
                System.out.print(prompt);
                String input = scanner.nextLine();
                if (input.length() >= 4) {
                    return input;
                }
            }
            System.out.println("Password must be at least 4 characters.");
        }
    }

    /**
     * Reads an optional string; returns the default value if the user presses Enter.
     *
     * @param prompt       the message to display before reading
     * @param defaultValue the value to use if the user provides no input
     * @return the trimmed user input, or {@code defaultValue} if blank
     */
    private String readOptionalString(String prompt, String defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }
}
