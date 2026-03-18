package com.banking.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.banking.models.SavingsAccount;
import com.banking.models.Account;
import com.banking.services.AccountService;
import com.banking.services.BankService;

public class DashboardPanel extends JPanel {
    private BankingGUI mainFrame;
    private BankService bankService;
    private AccountService accountService;
    private String accountNumber;
    private Account currentAccount;
    private JLabel balanceLabel;
    private DefaultTableModel transactionModel;

    public DashboardPanel(BankingGUI mainFrame, BankService bankService, String accountNumber) {
        this.mainFrame = mainFrame;
        this.bankService = bankService;
        this.accountNumber = accountNumber;
        this.accountService = new AccountService();
        this.currentAccount = new SavingsAccount(accountNumber, 5000, 3.5);
        setLayout(new BorderLayout());
        initializeComponents();
    }

    private void initializeComponents() {
        // Top panel - Header
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Main content with tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 12));

        // Dashboard tab
        JPanel dashboardTab = createDashboardTab();
        tabbedPane.addTab("Dashboard", dashboardTab);

        // Transactions tab
        JPanel transactionsTab = createTransactionsTab();
        tabbedPane.addTab("Transactions", transactionsTab);

        // Settings tab
        JPanel settingsTab = createSettingsTab();
        tabbedPane.addTab("Settings", settingsTab);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(31, 41, 55));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        JLabel titleLabel = new JLabel("Smart Banking System - Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(31, 41, 55));

        JLabel userLabel = new JLabel("Account: " + accountNumber);
        userLabel.setForeground(new Color(229, 231, 235));
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton logoutButton = new JButton("LOGOUT");
        logoutButton.setBackground(new Color(239, 68, 68));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 11));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> mainFrame.showLoginPanel());

        rightPanel.add(userLabel);
        rightPanel.add(logoutButton);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Left sidebar - Account info
        JPanel sidebarPanel = new JPanel(new GridLayout(1, 1));
        JPanel accountInfoPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        accountInfoPanel.setBackground(Color.WHITE);
        accountInfoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(59, 130, 246), 1), "Account Information"));

        JLabel typeLabel = new JLabel("Account Type: " + currentAccount.getAccountType());
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        typeLabel.setForeground(new Color(107, 114, 128));

        balanceLabel = new JLabel(String.format("Balance: $%.2f", currentAccount.getBalance()));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 13));
        balanceLabel.setForeground(new Color(5, 150, 105));

        JLabel accountNumLabel = new JLabel("Account #: " + accountNumber);
        accountNumLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        accountNumLabel.setForeground(new Color(107, 114, 128));

        accountInfoPanel.add(typeLabel);
        accountInfoPanel.add(balanceLabel);
        accountInfoPanel.add(accountNumLabel);

        sidebarPanel.add(accountInfoPanel);

        // Right side - Quick actions
        JPanel actionsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        actionsPanel.setBackground(Color.WHITE);

        JPanel depositCard = createActionCard("Deposit", "Add funds", new Color(59, 130, 246), e -> showDepositDialog());
        JPanel withdrawCard = createActionCard("Withdraw", "Withdraw funds", new Color(239, 68, 68), e -> showWithdrawDialog());
        JPanel balanceCard = createActionCard("Balance", "Check balance", new Color(16, 185, 129), e -> showBalanceAlert());

        actionsPanel.add(depositCard);
        actionsPanel.add(withdrawCard);
        actionsPanel.add(balanceCard);

        JPanel mainActionPanel = new JPanel(new BorderLayout(0, 15));
        mainActionPanel.setBackground(Color.WHITE);
        mainActionPanel.setBorder(BorderFactory.createTitledBorder("Quick Actions"));
        mainActionPanel.add(actionsPanel, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, mainActionPanel);
        splitPane.setDividerLocation(250);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActionCard(String title, String description, Color color, java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(color);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        descLabel.setForeground(new Color(107, 114, 128));

        card.add(titleLabel);
        card.add(descLabel);

        JButton cardButton = new JButton();
        cardButton.setOpaque(false);
        cardButton.setContentAreaFilled(false);
        cardButton.setBorderPainted(false);
        cardButton.addActionListener(action);

        card.add(cardButton);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                action.actionPerformed(new java.awt.event.ActionEvent(card, 0, ""));
            }
        });

        return card;
    }

    private JPanel createTransactionsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Transaction History");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(31, 41, 55));

        transactionModel = new DefaultTableModel(
                new String[]{"Date", "Description", "Amount"},
                0
        );

        transactionModel.addRow(new Object[]{"2026-03-18", "Initial Deposit", "$5000.00"});
        transactionModel.addRow(new Object[]{"2026-03-17", "Interest Credited", "$5.50"});

        JTable transactionTable = new JTable(transactionModel);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 11));
        transactionTable.setRowHeight(25);
        transactionTable.getTableHeader().setBackground(new Color(243, 244, 246));
        transactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));

        JScrollPane scrollPane = new JScrollPane(transactionTable);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSettingsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Account Settings");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(31, 41, 55));

        JPanel passwordPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBorder(BorderFactory.createTitledBorder("Change Password"));

        JLabel oldPassLabel = new JLabel("Old Password:");
        JPasswordField oldPassField = new JPasswordField(15);
        oldPassField.setBackground(new Color(243, 244, 246));

        JLabel newPassLabel = new JLabel("New Password:");
        JPasswordField newPassField = new JPasswordField(15);
        newPassField.setBackground(new Color(243, 244, 246));

        JButton changeButton = new JButton("CHANGE PASSWORD");
        changeButton.setBackground(new Color(124, 58, 237));
        changeButton.setForeground(Color.WHITE);
        changeButton.setFont(new Font("Arial", Font.BOLD, 12));
        changeButton.setFocusPainted(false);
        changeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changeButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        passwordPanel.add(oldPassLabel);
        passwordPanel.add(oldPassField);
        passwordPanel.add(newPassLabel);
        passwordPanel.add(newPassField);
        passwordPanel.add(new JLabel());
        passwordPanel.add(changeButton);

        panel.add(title, BorderLayout.NORTH);
        panel.add(passwordPanel, BorderLayout.CENTER);

        return panel;
    }

    private void showDepositDialog() {
        String amountStr = JOptionPane.showInputDialog(this, "Enter deposit amount:", "Deposit", JOptionPane.PLAIN_MESSAGE);
        if (amountStr != null && !amountStr.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount > 0) {
                    currentAccount.deposit(amount);
                    updateBalance();
                    transactionModel.insertRow(0, new Object[]{"2026-03-18", "Deposit", String.format("$%.2f", amount)});
                    JOptionPane.showMessageDialog(this, "Deposited $" + String.format("%.2f", amount) + " successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Amount must be positive!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showWithdrawDialog() {
        String amountStr = JOptionPane.showInputDialog(this, "Enter withdrawal amount:", "Withdraw", JOptionPane.PLAIN_MESSAGE);
        if (amountStr != null && !amountStr.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount > currentAccount.getBalance()) {
                    JOptionPane.showMessageDialog(this, "Insufficient balance!", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (amount > 0) {
                    currentAccount.withdraw(amount);
                    updateBalance();
                    transactionModel.insertRow(0, new Object[]{"2026-03-18", "Withdrawal", String.format("$%.2f", amount)});
                    JOptionPane.showMessageDialog(this, "Withdrew $" + String.format("%.2f", amount) + " successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Amount must be positive!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showBalanceAlert() {
        JOptionPane.showMessageDialog(this, String.format("Your current balance is: $%.2f", currentAccount.getBalance()), "Account Balance", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateBalance() {
        balanceLabel.setText(String.format("Balance: $%.2f", currentAccount.getBalance()));
    }
}
