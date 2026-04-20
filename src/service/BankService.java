package service;

import model.Account;
import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BankService {
    private final Map<String, User>    users    = new HashMap<>();
    private final Map<String, Account> accounts = new HashMap<>();

    public boolean register(String username, String password, String fullName) {
        if (users.containsKey(username.toLowerCase())) return false;
        users.put(username.toLowerCase(), new User(username, password, fullName));
        return true;
    }

    public User login(String username, String password) {
        User u = users.get(username.toLowerCase());
        return (u != null && u.getPassword().equals(password)) ? u : null;
    }

    public Account openAccount(User user, double initialDeposit) {
        Account acc = new Account(initialDeposit);
        user.addAccount(acc);
        accounts.put(acc.getAccountNumber(), acc);
        return acc;
    }

    public boolean deposit(Account acc, double amount) {
        return acc.deposit(amount, "Deposit");
    }

    public boolean withdraw(Account acc, double amount) {
        return acc.withdraw(amount, "Withdrawal");
    }

    public String transfer(Account from, String toAccNo, double amount) {
        Account to = accounts.get(toAccNo);
        if (to == null)           return "Destination account not found.";
        if (to == from)           return "Cannot transfer to the same account.";
        if (amount <= 0)          return "Amount must be positive.";
        if (from.getBalance() < amount) return "Insufficient funds.";

        from.addTransferOut(amount, toAccNo);
        to.addTransferIn(amount, from.getAccountNumber());
        return "SUCCESS";
    }

    public Account findAccount(String accNo) {
        return accounts.get(accNo);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }
}
