import gui.BankingGUI;
import model.Account;
import model.Transaction;
import model.User;
import service.BankService;
import util.ConsoleUI;

import javax.swing.*;
import java.util.List;

/**
 * JavaBank — entry point.
 * Launches the Swing GUI by default.
 * Pass --cli as argument to use the original terminal interface.
 */
public class BankingApp {

    private static final BankService bank = new BankService();
    private static User currentUser = null;

    public static void main(String[] args) {
        boolean useCLI = args.length > 0 && args[0].equalsIgnoreCase("--cli");

        if (useCLI) {
            runCLI();
        } else {
            // Launch GUI
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            SwingUtilities.invokeLater(BankingGUI::new);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // CLI MODE  (pass --cli to use)
    // ════════════════════════════════════════════════════════════════════════

    private static void runCLI() {
        ConsoleUI.header("  🏦  Welcome to JavaBank CLI  ");
        seedDemoData();

        boolean running = true;
        while (running) {
            showMainMenu();
            int choice = ConsoleUI.promptInt("Choose option");
            switch (choice) {
                case 1 -> handleLogin();
                case 2 -> handleRegister();
                case 0 -> { ConsoleUI.info("Thank you for banking with JavaBank. Goodbye!"); running = false; }
                default -> ConsoleUI.error("Invalid option. Try again.");
            }
        }
    }

    private static void showMainMenu() {
        ConsoleUI.section("Main Menu");
        ConsoleUI.menuItem(1, "Login");
        ConsoleUI.menuItem(2, "Register");
        ConsoleUI.menuItem(0, "Exit");
        ConsoleUI.blank();
    }

    private static void showDashboard() {
        boolean loggedIn = true;
        while (loggedIn) {
            ConsoleUI.header("Dashboard  |  " + currentUser.getFullName());
            ConsoleUI.menuItem(1, "My Accounts");
            ConsoleUI.menuItem(2, "Open New Account");
            ConsoleUI.menuItem(3, "Deposit");
            ConsoleUI.menuItem(4, "Withdraw");
            ConsoleUI.menuItem(5, "Transfer Funds");
            ConsoleUI.menuItem(6, "Transaction History");
            ConsoleUI.menuItem(0, "Logout");
            ConsoleUI.blank();

            int choice = ConsoleUI.promptInt("Choose option");
            switch (choice) {
                case 1 -> listAccounts();
                case 2 -> openAccount();
                case 3 -> handleDeposit();
                case 4 -> handleWithdraw();
                case 5 -> handleTransfer();
                case 6 -> handleHistory();
                case 0 -> { ConsoleUI.success("Logged out."); currentUser = null; loggedIn = false; }
                default -> ConsoleUI.error("Invalid option.");
            }
        }
    }

    private static void handleLogin() {
        ConsoleUI.section("Login");
        String username = ConsoleUI.prompt("Username");
        String password = ConsoleUI.promptPassword("Password");
        currentUser = bank.login(username, password);
        if (currentUser == null) ConsoleUI.error("Invalid username or password.");
        else { ConsoleUI.success("Welcome, " + currentUser.getFullName() + "!"); showDashboard(); }
    }

    private static void handleRegister() {
        ConsoleUI.section("Register");
        String fullName = ConsoleUI.prompt("Full Name");
        String username = ConsoleUI.prompt("Username");
        String password = ConsoleUI.promptPassword("Password");
        if (bank.register(username, password, fullName)) ConsoleUI.success("Registered! Please login.");
        else ConsoleUI.error("Username already taken.");
    }

    private static void listAccounts() {
        ConsoleUI.section("Your Accounts");
        List<Account> accounts = currentUser.getAccounts();
        if (accounts.isEmpty()) { ConsoleUI.info("No accounts. Open one first."); return; }
        accounts.forEach(a -> System.out.println("  " + a));
    }

    private static void openAccount() {
        ConsoleUI.section("Open New Account");
        double initial = ConsoleUI.promptDouble("Initial Deposit (₹)");
        Account acc = bank.openAccount(currentUser, initial);
        ConsoleUI.success("Account opened: " + acc.getAccountNumber());
    }

    private static void handleDeposit() {
        ConsoleUI.section("Deposit");
        Account acc = pickAccount();
        if (acc == null) return;
        double amount = ConsoleUI.promptDouble("Amount (₹)");
        if (bank.deposit(acc, amount)) ConsoleUI.success(String.format("₹%.2f deposited. Balance: ₹%.2f", amount, acc.getBalance()));
        else ConsoleUI.error("Deposit failed.");
    }

    private static void handleWithdraw() {
        ConsoleUI.section("Withdraw");
        Account acc = pickAccount();
        if (acc == null) return;
        double amount = ConsoleUI.promptDouble("Amount (₹)");
        if (bank.withdraw(acc, amount)) ConsoleUI.success(String.format("₹%.2f withdrawn. Balance: ₹%.2f", amount, acc.getBalance()));
        else ConsoleUI.error("Insufficient funds.");
    }

    private static void handleTransfer() {
        ConsoleUI.section("Transfer");
        Account from = pickAccount();
        if (from == null) return;
        String toAccNo = ConsoleUI.prompt("Destination Account Number");
        double amount  = ConsoleUI.promptDouble("Amount (₹)");
        String result  = bank.transfer(from, toAccNo, amount);
        if ("SUCCESS".equals(result)) ConsoleUI.success(String.format("₹%.2f sent to %s. Balance: ₹%.2f", amount, toAccNo, from.getBalance()));
        else ConsoleUI.error(result);
    }

    private static void handleHistory() {
        ConsoleUI.section("Transaction History");
        Account acc = pickAccount();
        if (acc == null) return;
        List<Transaction> txns = acc.getTransactions();
        if (txns.isEmpty()) { ConsoleUI.info("No transactions yet."); return; }
        System.out.printf("%n  Account: %s%n", acc.getAccountNumber());
        ConsoleUI.line('─');
        txns.forEach(System.out::println);
        ConsoleUI.line('─');
        System.out.printf("  Balance: ₹%.2f%n", acc.getBalance());
    }

    private static Account pickAccount() {
        List<Account> accounts = currentUser.getAccounts();
        if (accounts.isEmpty()) { ConsoleUI.error("No accounts found."); return null; }
        if (accounts.size() == 1) return accounts.get(0);
        ConsoleUI.info("Your accounts:");
        for (int i = 0; i < accounts.size(); i++) System.out.printf("  [%d] %s%n", i+1, accounts.get(i));
        int idx = ConsoleUI.promptInt("Select account") - 1;
        if (idx < 0 || idx >= accounts.size()) { ConsoleUI.error("Invalid selection."); return null; }
        return accounts.get(idx);
    }

    private static void seedDemoData() {
        bank.register("alice", "alice123", "Alice Sharma");
        bank.register("bob",   "bob123",   "Bob Verma");
        User alice = bank.login("alice", "alice123");
        User bob   = bank.login("bob",   "bob123");
        bank.openAccount(alice, 5000);
        bank.openAccount(bob,   3000);
        ConsoleUI.info("Demo accounts: alice/alice123  |  bob/bob123");
        ConsoleUI.line('─');
    }
}
