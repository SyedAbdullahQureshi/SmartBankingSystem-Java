package com.banking.models;

public class Customer {
    private String name;
    private String accountNumber;
    private String password;

    public Customer(String name, String accountNumber, String password) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }
}