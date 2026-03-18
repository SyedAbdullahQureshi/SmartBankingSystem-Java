package com.banking.services;

import com.banking.models.Account;
import com.banking.exceptions.InsufficientFundsException;

public class AccountService {

    public void deposit(Account account, double amount) {
        if (amount > 0) {
            account.setBalance(account.getBalance() + amount);
            System.out.println("Deposited: " + amount + ". New balance: " + account.getBalance());
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    }

    public void withdraw(Account account, double amount) throws InsufficientFundsException {
        if (amount > account.getBalance()) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal.");
        } else {
            account.setBalance(account.getBalance() - amount);
            System.out.println("Withdrew: " + amount + ". New balance: " + account.getBalance());
        }
    }

    public double checkBalance(Account account) {
        return account.getBalance();
    }
}