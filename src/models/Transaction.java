package models;

import models.enums.TransactionType;


import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private String transactionId;
    private TransactionType transactionType;
    private double amount;
    private LocalDateTime date;
    private String description;
    private Account sourceAccount;
    private Account destinationAccount;

    public Transaction(TransactionType transactionType, double amount, String description, Account sourceAccount) {
        this(transactionType, amount, description, sourceAccount, null);
    }

    public Transaction(TransactionType transactionType, double amount, String description, Account sourceAccount, Account destinationAccount) {
        this.transactionId = UUID.randomUUID().toString();
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.date = LocalDateTime.now();
    }

    // Getters and setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
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
        return "Transaction ID: " + transactionId + " | Transaction Type: " + transactionType + " | Amount: " + amount + " MAD| Date: " + date + " | Description: " + description + " | Source Account ID: " + sourceAccount.getAccountId() + (destinationAccount != null ? " | Destination Account ID: " + destinationAccount.getAccountId() : "");
    }
}
