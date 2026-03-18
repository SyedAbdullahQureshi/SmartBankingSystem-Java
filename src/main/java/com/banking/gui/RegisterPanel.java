package com.banking.gui;

import javax.swing.*;
import java.awt.*;
import com.banking.services.BankService;

public class RegisterPanel extends JPanel {
    private BankingGUI mainFrame;
    private BankService bankService;
    private JTextField nameField, accountField;
    private JPasswordField passwordField, confirmField;
    private JComboBox<String> accountTypeBox;
    private JLabel errorLabel, successLabel;

    public RegisterPanel(BankingGUI mainFrame, BankService bankService) {
        this.mainFrame = mainFrame;
        this.bankService = bankService;
        setLayout(new BorderLayout());
        setBackground(new Color(5, 150, 105));
        initializeComponents();
    }

    private void initializeComponents() {
        // Top panel with title
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(5, 150, 105));
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        topPanel.add(titleLabel);

        // Center panel with registration form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(5, 150, 105));
        GridBagConstraints gbc = new GridBagConstraints();

        // Form container
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createLineBorder(new Color(16, 185, 129), 2));
        formPanel.setPreferredSize(new Dimension(420, 450));

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(12, 20, 12, 20);
        formGbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel formTitle = new JLabel("Create New Account");
        formTitle.setFont(new Font("Arial", Font.BOLD, 20));
        formTitle.setForeground(new Color(5, 150, 105));
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formGbc.gridwidth = 2;
        formPanel.add(formTitle, formGbc);

        int row = 1;

        // Full name
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        formGbc.gridx = 0;
        formGbc.gridy = row;
        formGbc.gridwidth = 1;
        formPanel.add(nameLabel, formGbc);

        nameField = new JTextField(15);
        nameField.setFont(new Font("Arial", Font.PLAIN, 11));
        nameField.setBackground(new Color(209, 250, 229));
        formGbc.gridx = 1;
        formGbc.gridy = row;
        formPanel.add(nameField, formGbc);
        row++;

        // Account number
        JLabel accountLabel = new JLabel("Account Number:");
        accountLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        formGbc.gridx = 0;
        formGbc.gridy = row;
        formPanel.add(accountLabel, formGbc);

        accountField = new JTextField(15);
        accountField.setFont(new Font("Arial", Font.PLAIN, 11));
        accountField.setBackground(new Color(209, 250, 229));
        formGbc.gridx = 1;
        formGbc.gridy = row;
        formPanel.add(accountField, formGbc);
        row++;

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        formGbc.gridx = 0;
        formGbc.gridy = row;
        formPanel.add(passwordLabel, formGbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 11));
        passwordField.setBackground(new Color(209, 250, 229));
        formGbc.gridx = 1;
        formGbc.gridy = row;
        formPanel.add(passwordField, formGbc);
        row++;

        // Confirm password
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        formGbc.gridx = 0;
        formGbc.gridy = row;
        formPanel.add(confirmLabel, formGbc);

        confirmField = new JPasswordField(15);
        confirmField.setFont(new Font("Arial", Font.PLAIN, 11));
        confirmField.setBackground(new Color(209, 250, 229));
        formGbc.gridx = 1;
        formGbc.gridy = row;
        formPanel.add(confirmField, formGbc);
        row++;

        // Account type
        JLabel typeLabel = new JLabel("Account Type:");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        formGbc.gridx = 0;
        formGbc.gridy = row;
        formPanel.add(typeLabel, formGbc);

        accountTypeBox = new JComboBox<>(new String[]{"Savings Account", "Current Account"});
        accountTypeBox.setFont(new Font("Arial", Font.PLAIN, 11));
        formGbc.gridx = 1;
        formGbc.gridy = row;
        formPanel.add(accountTypeBox, formGbc);
        row++;

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        errorLabel.setForeground(new Color(220, 38, 38));
        formGbc.gridx = 0;
        formGbc.gridy = row;
        formGbc.gridwidth = 2;
        formPanel.add(errorLabel, formGbc);
        row++;

        // Success label
        successLabel = new JLabel(" ");
        successLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        successLabel.setForeground(new Color(5, 150, 105));
        formGbc.gridx = 0;
        formGbc.gridy = row;
        formGbc.gridwidth = 2;
        formPanel.add(successLabel, formGbc);
        row++;

        // Register button
        JButton registerButton = new JButton("CREATE ACCOUNT");
        registerButton.setFont(new Font("Arial", Font.BOLD, 12));
        registerButton.setBackground(new Color(16, 185, 129));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> handleRegister());
        formGbc.gridx = 0;
        formGbc.gridy = row;
        formGbc.gridwidth = 2;
        formGbc.insets = new Insets(15, 20, 12, 20);
        formPanel.add(registerButton, formGbc);
        row++;

        // Login link
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(Color.WHITE);
        JLabel loginLabel = new JLabel("Already have an account? ");
        loginLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        JButton loginLink = new JButton("Sign in");
        loginLink.setContentAreaFilled(false);
        loginLink.setBorderPainted(false);
        loginLink.setForeground(new Color(16, 185, 129));
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.setFont(new Font("Arial", Font.PLAIN, 10));
        loginLink.addActionListener(e -> mainFrame.showLoginPanel());

        loginPanel.add(loginLabel);
        loginPanel.add(loginLink);

        formGbc.gridx = 0;
        formGbc.gridy = row;
        formGbc.gridwidth = 2;
        formGbc.insets = new Insets(0, 20, 12, 20);
        formPanel.add(loginPanel, formGbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(formPanel, gbc);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(centerPanel), BorderLayout.CENTER);
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String account = accountField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        errorLabel.setText(" ");
        successLabel.setText(" ");

        if (name.isEmpty() || account.isEmpty() || password.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirm)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        if (password.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters.");
            return;
        }

        try {
            bankService.registerCustomer(name, account, password);
            successLabel.setText("Account created successfully! Redirecting to login...");
            nameField.setText("");
            accountField.setText("");
            passwordField.setText("");
            confirmField.setText("");

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    SwingUtilities.invokeLater(() -> mainFrame.showLoginPanel());
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } catch (Exception ex) {
            errorLabel.setText("Error creating account.");
        }
    }
}
