package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    public enum Type { DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT }

    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp;
    private final String note;

    public Transaction(Type type, double amount, double balanceAfter, String note) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
        this.note = note;
    }

    public Type getType()          { return type; }
    public double getAmount()      { return amount; }
    public double getBalanceAfter(){ return balanceAfter; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getNote()        { return note; }

    @Override
    public String toString() {
        String sign = (type == Type.DEPOSIT || type == Type.TRANSFER_IN) ? "+" : "-";
        String ts = timestamp.format(DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm"));
        return String.format("  %-22s  %s₹%-10.2f  Balance: ₹%.2f   %s",
                ts, sign, amount, balanceAfter, note);
    }
}
