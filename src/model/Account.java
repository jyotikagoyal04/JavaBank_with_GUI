package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Account {
    private final String accountNumber;
    private double balance;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(double initialDeposit) {
        this.accountNumber = "JB" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.balance = 0;
        if (initialDeposit > 0) {
            balance = initialDeposit;
            transactions.add(new Transaction(Transaction.Type.DEPOSIT, initialDeposit, balance, "Initial deposit"));
        }
    }

    public String getAccountNumber()       { return accountNumber; }
    public double getBalance()             { return balance; }
    public List<Transaction> getTransactions() { return transactions; }

    public boolean deposit(double amount, String note) {
        if (amount <= 0) return false;
        balance += amount;
        transactions.add(new Transaction(Transaction.Type.DEPOSIT, amount, balance, note));
        return true;
    }

    public boolean withdraw(double amount, String note) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        transactions.add(new Transaction(Transaction.Type.WITHDRAWAL, amount, balance, note));
        return true;
    }

    public void addTransferIn(double amount, String fromAcc) {
        balance += amount;
        transactions.add(new Transaction(Transaction.Type.TRANSFER_IN, amount, balance, "From " + fromAcc));
    }

    public void addTransferOut(double amount, String toAcc) {
        balance -= amount;
        transactions.add(new Transaction(Transaction.Type.TRANSFER_OUT, amount, balance, "To " + toAcc));
    }

    @Override
    public String toString() {
        return String.format("[%s]  Balance: ₹%.2f", accountNumber, balance);
    }
}
