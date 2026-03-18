package com.banking.interfaces;

import com.banking.exceptions.InsufficientFundsException;
import com.banking.models.Account;
import java.util.List;

public interface TransactionOperations {
    void deposit(Account account, double amount);
    void withdraw(Account account, double amount) throws InsufficientFundsException;
    List<String> viewTransactionHistory();
}