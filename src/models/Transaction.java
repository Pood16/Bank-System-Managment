package models;

import models.enums.TransactionType;

import java.time.LocalDate;
import java.util.UUID;

public class Transaction {
    private String id;
    private TransactionType type;
    private double amount;
    private LocalDate date;
    private String description;
    private Account sourceAccount;
    private Account destinationAccount;

    public Transaction(TransactionType type, double amount, String description,Account sourceAccount) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = LocalDate.now();
        this.sourceAccount = sourceAccount;
    }

    public Transaction(TransactionType type, double amount, String description, Account sourceAccount, Account destinationAccount) {
       this(type, amount, description, sourceAccount);
       this.destinationAccount = destinationAccount;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Account getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", amount=" + amount +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", sourceAccount=" + sourceAccount +
                ", destinationAccount=" + destinationAccount +
                '}';
    }
}
