package com.banking.utils;

import java.util.regex.Pattern;

public class InputValidator {

    private static final Pattern NUMERIC_PATTERN = Pattern.compile("\\d+");

    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && NUMERIC_PATTERN.matcher(accountNumber).matches();
    }

    public static boolean isValidAmount(String amount) {
        return amount != null && NUMERIC_PATTERN.matcher(amount).matches() && Double.parseDouble(amount) > 0;
    }

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}