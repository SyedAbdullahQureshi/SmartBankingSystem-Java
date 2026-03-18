package com.banking.gui;

import javax.swing.*;
import java.awt.*;
import com.banking.services.BankService;

public class BankingGUI extends JFrame {
    private BankService bankService;
    private static final int WIDTH = 900;
    private static final int HEIGHT = 650;

    public BankingGUI() {
        this.bankService = new BankService();
        initializeFrame();
        showLoginPanel();
    }

    private void initializeFrame() {
        setTitle("Smart Banking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(700, 500));
    }

    public void showLoginPanel() {
        LoginPanel loginPanel = new LoginPanel(this, bankService);
        setContentPane(loginPanel);
        setSize(WIDTH, HEIGHT);
        revalidate();
        repaint();
    }

    public void showRegisterPanel() {
        RegisterPanel registerPanel = new RegisterPanel(this, bankService);
        setContentPane(registerPanel);
        setSize(WIDTH, HEIGHT);
        revalidate();
        repaint();
    }

    public void showDashboardPanel(String accountNumber) {
        DashboardPanel dashboardPanel = new DashboardPanel(this, bankService, accountNumber);
        setContentPane(dashboardPanel);
        setSize(900, 650);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankingGUI frame = new BankingGUI();
            frame.setVisible(true);
        });
    }
}
