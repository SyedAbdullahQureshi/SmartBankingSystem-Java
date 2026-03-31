package com.banking;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Core banking service that manages all accounts and delegates persistence
 * to {@link FileManager}.
 *
 * <p>Accounts are cached in memory (keyed by account number) for fast look-up
 * during a session. Any mutation is immediately flushed to disk via
 * {@link FileManager#saveAccount(Account)}.
 *
 * <p>Account numbers are generated automatically using an ever-increasing
 * 8-digit counter seeded from the number of existing accounts so that
 * numbers remain unique across application restarts.
 */
public class Bank {

    /** Prefix applied to every generated account number (e.g. "ACC00000001"). */
    private static final String ACCOUNT_PREFIX = "ACC";

    /** Delimiter used to separate the salt from the hash in the stored credential. */
    private static final String SALT_HASH_SEPARATOR = "$";

    /** Number of random salt bytes generated per account. */
    private static final int SALT_BYTES = 16;

    private final SecureRandom secureRandom = new SecureRandom();

    private final FileManager fileManager;

    /** In-memory cache of all accounts, keyed by account number. */
    private final Map<String, Account> accounts;

    /** Atomic counter used to derive the next account number. */
    private final AtomicLong accountCounter;

    /**
     * Creates a Bank instance and loads all persisted accounts from disk.
     *
     * @param fileManager the file manager to use for persistence
     */
    public Bank(FileManager fileManager) {
        this.fileManager = fileManager;
        this.accounts = new HashMap<>();

        // Load persisted accounts
        List<Account> saved = fileManager.loadAllAccounts();
        for (Account a : saved) {
            accounts.put(a.getAccountNumber(), a);
        }

        // Seed the counter so new account numbers don't collide with existing ones
        accountCounter = new AtomicLong(saved.size());
    }

    // -------------------------------------------------------------------------
    // Account registration
    // -------------------------------------------------------------------------

    /**
     * Registers a new account for the given holder.
     *
     * @param holderName the full name of the new account holder
     * @param password   the plain-text password chosen by the user
     * @return the newly created {@link Account}
     * @throws IllegalArgumentException if the holder name or password is blank
     */
    public Account register(String holderName, String password) {
        if (holderName == null || holderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Holder name must not be empty.");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }

        String accountNumber = generateAccountNumber();
        String passwordHash = createPasswordHash(password);
        Account account = new Account(accountNumber, sanitise(holderName), passwordHash);

        accounts.put(accountNumber, account);
        fileManager.saveAccount(account);

        return account;
    }

    // -------------------------------------------------------------------------
    // Authentication
    // -------------------------------------------------------------------------

    /**
     * Authenticates a user and returns the matching account.
     *
     * @param accountNumber the account number entered by the user
     * @param password      the plain-text password entered by the user
     * @return the authenticated {@link Account}
     * @throws IllegalArgumentException if the credentials are invalid
     */
    public Account login(String accountNumber, String password) {
        Account account = accounts.get(accountNumber.trim());
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }

        if (!verifyPassword(password, account.getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect password.");
        }

        // Eagerly load transaction history for this session
        fileManager.loadTransactions(account);

        return account;
    }

    // -------------------------------------------------------------------------
    // Deposit
    // -------------------------------------------------------------------------

    /**
     * Deposits money into the specified account.
     *
     * @param account     the target account
     * @param amount      the amount to deposit (must be &gt; 0)
     * @param description an optional note describing the deposit
     * @throws IllegalArgumentException if the amount is invalid
     */
    public void deposit(Account account, double amount, String description) {
        account.deposit(amount, sanitise(description));
        persistTransaction(account);
    }

    // -------------------------------------------------------------------------
    // Withdrawal
    // -------------------------------------------------------------------------

    /**
     * Withdraws money from the specified account.
     *
     * @param account     the source account
     * @param amount      the amount to withdraw (must be &gt; 0 and ≤ balance)
     * @param description an optional note describing the withdrawal
     * @throws IllegalArgumentException if the amount is invalid or exceeds the balance
     */
    public void withdraw(Account account, double amount, String description) {
        account.withdraw(amount, sanitise(description));
        persistTransaction(account);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Saves the updated account and appends the most recent transaction to disk.
     *
     * @param account the account that was just modified
     */
    private void persistTransaction(Account account) {
        // Save updated balance
        fileManager.saveAccount(account);

        // Append the latest transaction to the account's transaction file
        List<Transaction> txList = account.getTransactions();
        if (!txList.isEmpty()) {
            Transaction latest = txList.get(txList.size() - 1);
            fileManager.appendTransaction(account.getAccountNumber(), latest);
        }
    }

    /**
     * Generates the next unique account number.
     *
     * @return an 8-digit zero-padded account number prefixed with {@code ACC}
     */
    private String generateAccountNumber() {
        long num = accountCounter.incrementAndGet();
        String candidate = String.format("%s%08d", ACCOUNT_PREFIX, num);
        // Advance further only if there is a pre-existing collision (e.g. after gaps)
        int maxAttempts = 10_000;
        while (accounts.containsKey(candidate) && maxAttempts-- > 0) {
            num = accountCounter.incrementAndGet();
            candidate = String.format("%s%08d", ACCOUNT_PREFIX, num);
        }
        return candidate;
    }

    /**
     * Creates a salted SHA-256 credential string for the given plain-text password.
     * The returned string has the format {@code hexSalt$hexHash}.
     *
     * @param password the plain-text password
     * @return the salted credential string
     */
    private String createPasswordHash(String password) {
        byte[] salt = new byte[SALT_BYTES];
        secureRandom.nextBytes(salt);
        String hexSalt = bytesToHex(salt);
        String hexHash = hashWithSalt(password, salt);
        return hexSalt + SALT_HASH_SEPARATOR + hexHash;
    }

    /**
     * Verifies a plain-text password against a stored salted credential string.
     *
     * @param password      the plain-text password to verify
     * @param storedCredential the stored {@code hexSalt$hexHash} credential
     * @return {@code true} if the password matches, {@code false} otherwise
     */
    private boolean verifyPassword(String password, String storedCredential) {
        String[] parts = storedCredential.split("\\" + SALT_HASH_SEPARATOR, 2);
        if (parts.length != 2) {
            return false;
        }
        byte[] salt = hexToBytes(parts[0]);
        String expectedHash = parts[1];
        String actualHash = hashWithSalt(password, salt);
        return actualHash.equals(expectedHash);
    }

    /**
     * Computes SHA-256(salt || password) and returns the hex-encoded result.
     *
     * @param password the plain-text password
     * @param salt     the random salt bytes
     * @return the hex-encoded SHA-256 hash of the salted password
     */
    private static String hashWithSalt(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is mandated by the Java SE specification and will always be present
            throw new RuntimeException("SHA-256 algorithm not available.", e);
        }
    }

    /** Encodes a byte array as a lowercase hex string. */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    /** Decodes a lowercase hex string to a byte array. */
    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Removes pipe characters from user-supplied strings to protect the
     * pipe-delimited file format from corruption.
     *
     * @param input the raw user input
     * @return the sanitised string with pipe characters removed
     */
    private static String sanitise(String input) {
        return input == null ? "" : input.replace("|", "").trim();
    }
}
