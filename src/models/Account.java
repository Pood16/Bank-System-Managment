package models;


import models.enums.AccountType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Account {
    private String accountId;
    private AccountType accountType;
    private double balance;
    private List<Transaction> transactions;
    private Client client;

    public Account(AccountType accountType, Client client, double initialBalance) {
        this.accountId = UUID.randomUUID().toString();
        this.accountType = accountType;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        this.client = client;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        if (balance <= 0) {
            throw new IllegalArgumentException("The balance can't be negative");
        }
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("The minimum amount to deposit is 100 DH.");
        }
        balance += amount;
    }
    public void withdraw(double amount) {
        balance -= amount;
    }



    @Override
    public String toString() {
        return "Account ID# " + accountId + " | Type: " + accountType + " | Balance: " + balance + "$| Transactions: " + transactions.size() + " | Client ID# " + client.getClientId();
    }
}
