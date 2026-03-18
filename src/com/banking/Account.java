package com.banking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a bank account belonging to a single user.
 * Stores personal details, credentials, current balance, and a local transaction history.
 *
 * <p>Instances are created during registration and are persisted by {@link FileManager}.
 */
public class Account {

    /** Minimum allowed balance — accounts may not be overdrawn below this value. */
    public static final double MINIMUM_BALANCE = 0.0;

    private final String accountNumber;
    private final String holderName;
    private String passwordHash;   // SHA-256 hash stored as hex string
    private double balance;
    private final List<Transaction> transactions;

    /**
     * Constructs a new Account with zero balance and an empty transaction list.
     *
     * @param accountNumber unique account identifier
     * @param holderName    full name of the account holder
     * @param passwordHash  SHA-256 hash of the account password
     */
    public Account(String accountNumber, String holderName, String passwordHash) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.passwordHash = passwordHash;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
    }

    /**
     * Constructs an Account with an existing balance (used when loading from file).
     *
     * @param accountNumber unique account identifier
     * @param holderName    full name of the account holder
     * @param passwordHash  SHA-256 hash of the account password
     * @param balance       current account balance
     */
    public Account(String accountNumber, String holderName,
                   String passwordHash, double balance) {
        this(accountNumber, holderName, passwordHash);
        this.balance = balance;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public double getBalance() {
        return balance;
    }

    /** Returns an unmodifiable view of the transaction history. */
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    // -------------------------------------------------------------------------
    // Business operations
    // -------------------------------------------------------------------------

    /**
     * Deposits the given amount into the account and records the transaction.
     *
     * @param amount      the amount to deposit (must be positive)
     * @param description a short note about this deposit
     * @throws IllegalArgumentException if the amount is not positive
     */
    public void deposit(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        balance += amount;
        Transaction tx = new Transaction(Transaction.Type.DEPOSIT, amount, balance, description);
        transactions.add(tx);
    }

    /**
     * Withdraws the given amount from the account and records the transaction.
     *
     * @param amount      the amount to withdraw (must be positive and not exceed balance)
     * @param description a short note about this withdrawal
     * @throws IllegalArgumentException if the amount is not positive or exceeds the balance
     */
    public void withdraw(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException(
                    String.format("Insufficient funds. Available balance: $%.2f", balance));
        }
        balance -= amount;
        Transaction tx = new Transaction(Transaction.Type.WITHDRAWAL, amount, balance, description);
        transactions.add(tx);
    }

    /**
     * Appends a transaction loaded from persistent storage to the in-memory history.
     * Does <em>not</em> modify the balance — use the balance stored in the account file.
     *
     * @param transaction the transaction to add
     */
    public void addTransactionFromHistory(Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * Updates the password hash (e.g. after a password-change operation).
     *
     * @param newPasswordHash the new SHA-256 hash
     */
    public void setPasswordHash(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    /**
     * Converts this account to a pipe-delimited string for file storage.
     * Format: ACCOUNT_NUMBER|HOLDER_NAME|PASSWORD_HASH|BALANCE
     *
     * @return the serialised string representation
     */
    public String toFileString() {
        return accountNumber + "|"
                + holderName + "|"
                + passwordHash + "|"
                + String.format("%.2f", balance);
    }

    /**
     * Reconstructs an Account from a pipe-delimited file string.
     * The returned account has an empty transaction list; transactions must be
     * loaded separately via {@link FileManager#loadTransactions(Account)}.
     *
     * @param line a single line from the accounts file
     * @return the corresponding Account object
     * @throws IllegalArgumentException if the line format is invalid
     */
    public static Account fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid account record: " + line);
        }
        String accountNumber = parts[0].trim();
        String holderName = parts[1].trim();
        String passwordHash = parts[2].trim();
        double balance = Double.parseDouble(parts[3].trim());
        return new Account(accountNumber, holderName, passwordHash, balance);
    }

    /**
     * Returns a formatted summary of this account for display purposes.
     */
    @Override
    public String toString() {
        return String.format("Account No: %s  |  Holder: %-20s  |  Balance: $%.2f",
                accountNumber, holderName, balance);
    }
}
