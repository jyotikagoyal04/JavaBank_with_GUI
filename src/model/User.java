package model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String username;
    private final String password;
    private final String fullName;
    private final List<Account> accounts = new ArrayList<>();

    public User(String username, String password, String fullName) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    public String getUsername()        { return username; }
    public String getPassword()        { return password; }
    public String getFullName()        { return fullName; }
    public List<Account> getAccounts() { return accounts; }

    public void addAccount(Account account) { accounts.add(account); }
}
