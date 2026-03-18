package com.banking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single banking transaction (deposit or withdrawal).
 * Stores the transaction type, amount, resulting balance, and timestamp.
 */
public class Transaction {

    /** Formatter used when converting transactions to/from file storage. */
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Possible transaction types. */
    public enum Type {
        DEPOSIT, WITHDRAWAL
    }

    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp;
    private final String description;

    /**
     * Creates a new Transaction with the current timestamp.
     *
     * @param type         the type of transaction (DEPOSIT or WITHDRAWAL)
     * @param amount       the amount involved in the transaction
     * @param balanceAfter the account balance after this transaction
     * @param description  a short description of the transaction
     */
    public Transaction(Type type, double amount, double balanceAfter, String description) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Reconstructs a Transaction from a saved string (used when loading from file).
     *
     * @param type         the type of transaction
     * @param amount       the amount involved
     * @param balanceAfter the balance after this transaction
     * @param description  a short description
     * @param timestamp    the original timestamp of the transaction
     */
    public Transaction(Type type, double amount, double balanceAfter,
                       String description, LocalDateTime timestamp) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.timestamp = timestamp;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Converts this transaction to a pipe-delimited string suitable for file storage.
     * Format: TYPE|AMOUNT|BALANCE_AFTER|DESCRIPTION|TIMESTAMP
     *
     * @return the serialised string representation
     */
    public String toFileString() {
        return type.name() + "|"
                + String.format("%.2f", amount) + "|"
                + String.format("%.2f", balanceAfter) + "|"
                + description + "|"
                + timestamp.format(FORMATTER);
    }

    /**
     * Reconstructs a Transaction object from a pipe-delimited file string.
     *
     * @param line a single line from the transaction file
     * @return the corresponding Transaction object
     * @throws IllegalArgumentException if the line format is invalid
     */
    public static Transaction fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid transaction record: " + line);
        }
        Type type = Type.valueOf(parts[0].trim());
        double amount = Double.parseDouble(parts[1].trim());
        double balanceAfter = Double.parseDouble(parts[2].trim());
        String description = parts[3].trim();
        LocalDateTime timestamp = LocalDateTime.parse(parts[4].trim(), FORMATTER);
        return new Transaction(type, amount, balanceAfter, description, timestamp);
    }

    /**
     * Returns a human-readable representation of this transaction.
     */
    @Override
    public String toString() {
        return String.format("[%s] %-12s Amount: $%10.2f  |  Balance After: $%10.2f  |  %s",
                timestamp.format(FORMATTER),
                type.name(),
                amount,
                balanceAfter,
                description);
    }
}
