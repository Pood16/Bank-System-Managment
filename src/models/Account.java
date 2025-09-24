package models;


import models.enums.AccountType;
import models.enums.TransactionType;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class Account {
    private String id;
    private Client client;
    private AccountType type;
    private double balance;
    private List<Transaction> transactions;

    public Account(Client client, AccountType type, double balance) {
        this.id = UUID.randomUUID().toString();
        this.client = client;
        this.type = type;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
    }

    public void deposit(double amount) {
        if (amount <= 0 ) {
            throw new IllegalArgumentException("The minimum amount to deposit is 1$");
        }
        balance += amount;
        transactions.add(new Transaction(TransactionType.DEPOSIT,amount, "Deposit an amount", this));
    }

    public void withdraw(double amount) {
        if (amount <= 0 ) {
            throw new IllegalArgumentException("The minimum amount to withdraw is 1$");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        balance -= amount;
        transactions.add(new Transaction(TransactionType.WITHDRAWAL,amount, "Withdraw an amount", this));
    }

    public void transfer(Account destAccount, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("The minimal amount To Transfer is 100DH");
        }
        if (destAccount == null) {
            throw new NoSuchElementException("The destination account is required");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient balance for transfer");
        }
        balance -= amount;
        destAccount.balance += amount;
        transactions.add(new Transaction(TransactionType.TRANSFER, amount, "Transfer an amount", this, destAccount));
        destAccount.transactions.add(new Transaction(TransactionType.DEPOSIT, amount, "Received transfer", destAccount));
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", client=" + client.getFirstName() + " " + client.getLastName() +
                ", type=" + type +
                ", balance=" + balance +
                ", transactions number=" + transactions.size() +
                '}';
    }
}
