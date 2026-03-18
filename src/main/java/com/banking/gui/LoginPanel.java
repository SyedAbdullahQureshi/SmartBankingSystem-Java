package com.banking.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import com.banking.exceptions.AccountNotFoundException;
import com.banking.services.BankService;

public class LoginPanel extends JPanel {
    private BankingGUI mainFrame;
    private BankService bankService;
    private JTextField accountField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    public LoginPanel(BankingGUI mainFrame, BankService bankService) {
        this.mainFrame = mainFrame;
        this.bankService = bankService;
        setLayout(new BorderLayout());
        setBackground(new Color(30, 58, 138));
        initializeComponents();
    }

    private void initializeComponents() {
        // Top panel with title
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(30, 58, 138));
        topPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));

        JLabel titleLabel = new JLabel("Smart Banking System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);

        topPanel.add(titleLabel);

        // Center panel with login form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(30, 58, 138));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Form container
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createLineBorder(new Color(59, 130, 246), 2));
        formPanel.setPreferredSize(new Dimension(400, 300));

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(15, 20, 15, 20);
        formGbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel formTitle = new JLabel("Login");
        formTitle.setFont(new Font("Arial", Font.BOLD, 24));
        formTitle.setForeground(new Color(30, 58, 138));
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formGbc.gridwidth = 2;
        formPanel.add(formTitle, formGbc);

        // Account number
        JLabel accountLabel = new JLabel("Account Number:");
        accountLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formGbc.gridx = 0;
        formGbc.gridy = 1;
        formGbc.gridwidth = 1;
        formPanel.add(accountLabel, formGbc);

        accountField = new JTextField(15);
        accountField.setFont(new Font("Arial", Font.PLAIN, 12));
        accountField.setBackground(new Color(219, 234, 254));
        formGbc.gridx = 1;
        formGbc.gridy = 1;
        formPanel.add(accountField, formGbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formGbc.gridx = 0;
        formGbc.gridy = 2;
        formPanel.add(passwordLabel, formGbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordField.setBackground(new Color(219, 234, 254));
        formGbc.gridx = 1;
        formGbc.gridy = 2;
        formPanel.add(passwordField, formGbc);

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        errorLabel.setForeground(new Color(220, 38, 38));
        formGbc.gridx = 0;
        formGbc.gridy = 3;
        formGbc.gridwidth = 2;
        formPanel.add(errorLabel, formGbc);

        // Login button
        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(59, 130, 246));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleLogin());
        formGbc.gridx = 0;
        formGbc.gridy = 4;
        formGbc.gridwidth = 2;
        formGbc.insets = new Insets(20, 20, 15, 20);
        formPanel.add(loginButton, formGbc);

        // Register link
        JPanel registerPanel = new JPanel();
        registerPanel.setBackground(Color.WHITE);
        JLabel registerLabel = new JLabel("Don't have an account? ");
        registerLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        JButton registerLink = new JButton("Sign up");
        registerLink.setContentAreaFilled(false);
        registerLink.setBorderPainted(false);
        registerLink.setForeground(new Color(59, 130, 246));
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.setFont(new Font("Arial", Font.PLAIN, 11));
        registerLink.addActionListener(e -> mainFrame.showRegisterPanel());

        registerPanel.add(registerLabel);
        registerPanel.add(registerLink);

        formGbc.gridx = 0;
        formGbc.gridy = 5;
        formGbc.insets = new Insets(0, 20, 15, 20);
        formPanel.add(registerPanel, formGbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(formPanel, gbc);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void handleLogin() {
        String account = accountField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (account.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Account number and password cannot be empty.");
            return;
        }

        try {
            bankService.login(account, password);
            mainFrame.showDashboardPanel(account);
        } catch (AccountNotFoundException ex) {
            errorLabel.setText("Invalid account number or password.");
        }
    }
}
