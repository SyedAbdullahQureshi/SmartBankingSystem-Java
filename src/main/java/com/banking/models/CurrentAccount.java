package com.banking.models;

public class CurrentAccount extends Account {
    private double overdraftLimit;

    public CurrentAccount(String accountNumber, double balance, double overdraftLimit) {
        super(accountNumber, "Current");
        if (balance > 0) {
            deposit(balance);
        }
        this.overdraftLimit = overdraftLimit;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= (getBalance() + overdraftLimit)) {
            setBalance(getBalance() - amount);
            return;
        }
        throw new IllegalArgumentException("Insufficient funds considering overdraft limit.");
    }

    @Override
    public String toString() {
        return "CurrentAccount{" +
                "accountNumber='" + getAccountNumber() + '\'' +
                ", balance=" + getBalance() +
                ", overdraftLimit=" + overdraftLimit +
                '}';
    }
}