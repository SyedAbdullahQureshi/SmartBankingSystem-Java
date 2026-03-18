package com.banking.services;

import com.banking.exceptions.InsufficientFundsException;
import com.banking.interfaces.TransactionOperations;
import com.banking.models.Account;
import java.util.ArrayList;
import java.util.List;

public class TransactionService implements TransactionOperations {
    private List<String> transactionHistory;

    public TransactionService() {
        this.transactionHistory = new ArrayList<>();
    }

    @Override
    public void deposit(Account account, double amount) {
        account.deposit(amount);
        recordTransaction("Deposited " + amount + " to account " + account.getAccountNumber());
    }

    @Override
    public void withdraw(Account account, double amount) throws InsufficientFundsException {
        if (account.getBalance() >= amount) {
            account.withdraw(amount);
            recordTransaction("Withdrew " + amount + " from account " + account.getAccountNumber());
        } else {
            throw new InsufficientFundsException("Insufficient funds for withdrawal from account " + account.getAccountNumber());
        }
    }

    @Override
    public List<String> viewTransactionHistory() {
        return transactionHistory;
    }

    private void recordTransaction(String transaction) {
        transactionHistory.add(transaction);
    }
}