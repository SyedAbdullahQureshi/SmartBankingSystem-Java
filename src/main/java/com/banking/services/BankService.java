package com.banking.services;

import com.banking.models.Customer;
import com.banking.exceptions.AccountNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class BankService {
    private Map<String, Customer> customers;

    public BankService() {
        customers = new HashMap<>();
    }

    public void registerCustomer(String name, String accountNumber, String password) {
        Customer customer = new Customer(name, accountNumber, password);
        customers.put(accountNumber, customer);
    }

    public Customer login(String accountNumber, String password) throws AccountNotFoundException {
        Customer customer = customers.get(accountNumber);
        if (customer == null || !customer.getPassword().equals(password)) {
            throw new AccountNotFoundException("Account not found or password is incorrect.");
        }
        return customer;
    }

    public boolean customerExists(String accountNumber) {
        return customers.containsKey(accountNumber);
    }
}