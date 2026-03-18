package com.banking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all file I/O for the Smart Banking System.
 *
 * <p>Accounts are stored in a single flat file ({@code data/accounts.txt}).
 * Each account's transactions are stored in a separate file under
 * {@code data/transactions/<accountNumber>.txt}.
 *
 * <p>All data is written as human-readable, pipe-delimited text so that files
 * can be inspected and backed up easily.
 */
public class FileManager {

    /** Directory where all data files are stored. */
    private static final String DATA_DIR = "data";

    /** Path to the accounts registry file. */
    private static final String ACCOUNTS_FILE = DATA_DIR + File.separator + "accounts.txt";

    /** Sub-directory that holds per-account transaction files. */
    private static final String TRANSACTIONS_DIR = DATA_DIR + File.separator + "transactions";

    /**
     * Ensures that the required data directories exist.
     * Call this once during application start-up.
     */
    public void initialise() {
        new File(DATA_DIR).mkdirs();
        new File(TRANSACTIONS_DIR).mkdirs();
    }

    // -------------------------------------------------------------------------
    // Account persistence
    // -------------------------------------------------------------------------

    /**
     * Loads all accounts from the accounts file.
     * Transaction histories are <em>not</em> loaded here; call
     * {@link #loadTransactions(Account)} after login if history is needed.
     *
     * @return a list of accounts; empty list if the file does not exist
     */
    public List<Account> loadAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        File file = new File(ACCOUNTS_FILE);
        if (!file.exists()) {
            return accounts;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        accounts.add(Account.fromFileString(line));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Warning: skipping malformed account record — " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading accounts file: " + e.getMessage());
        }
        return accounts;
    }

    /**
     * Persists a single account to the accounts file.
     * If an entry for this account number already exists it is replaced;
     * otherwise the entry is appended.
     *
     * @param account the account to save
     */
    public void saveAccount(Account account) {
        List<Account> accounts = loadAllAccounts();

        // Replace existing entry or add new one
        boolean found = false;
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getAccountNumber().equals(account.getAccountNumber())) {
                accounts.set(i, account);
                found = true;
                break;
            }
        }
        if (!found) {
            accounts.add(account);
        }

        writeAllAccounts(accounts);
    }

    /** Overwrites the accounts file with the supplied list using an atomic rename. */
    private void writeAllAccounts(List<Account> accounts) {
        File target = new File(ACCOUNTS_FILE);
        File tempFile = new File(ACCOUNTS_FILE + ".tmp");
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(
                new FileWriter(tempFile, false)))) {
            for (Account a : accounts) {
                writer.println(a.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Error writing accounts file: " + e.getMessage());
            return;
        }
        // Atomically replace the accounts file with the newly written temp file
        try {
            Files.move(tempFile.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            // ATOMIC_MOVE may not be supported on all platforms; fall back to a non-atomic move
            try {
                Files.move(tempFile.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                System.err.println("Error replacing accounts file: " + ex.getMessage());
            }
        }
    }

    // -------------------------------------------------------------------------
    // Transaction persistence
    // -------------------------------------------------------------------------

    /**
     * Loads the full transaction history for the given account from its
     * dedicated transaction file and attaches it to the account object.
     *
     * @param account the account whose history should be loaded
     */
    public void loadTransactions(Account account) {
        File file = transactionFile(account.getAccountNumber());
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        account.addTransactionFromHistory(Transaction.fromFileString(line));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Warning: skipping malformed transaction record — "
                                + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading transactions for account "
                    + account.getAccountNumber() + ": " + e.getMessage());
        }
    }

    /**
     * Appends a single transaction to the account's transaction file.
     * Creates the file if it does not yet exist.
     *
     * @param accountNumber the account to which the transaction belongs
     * @param transaction   the transaction to persist
     */
    public void appendTransaction(String accountNumber, Transaction transaction) {
        File file = transactionFile(accountNumber);
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(
                new FileWriter(file, true)))) {  // true = append mode
            writer.println(transaction.toFileString());
        } catch (IOException e) {
            System.err.println("Error writing transaction for account "
                    + accountNumber + ": " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Returns the {@link File} handle for the transaction file of the given account.
     *
     * @param accountNumber the account identifier
     * @return the transaction file (may not exist yet)
     */
    private File transactionFile(String accountNumber) {
        return new File(TRANSACTIONS_DIR + File.separator + accountNumber + ".txt");
    }
}
