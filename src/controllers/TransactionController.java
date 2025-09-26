package controllers;

import models.Transaction;
import models.Account;
import models.enums.TransactionType;
import services.AccountService;
import services.AuthService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TransactionController {
    private final AccountService accountService;
    private final AuthService authService;

    public TransactionController(AccountService accountService, AuthService authService) {
        this.accountService = accountService;
        this.authService = authService;
    }

    public Transaction makeDeposit(String accountId, double amount, String description) {
        if (!authService.isLoggedIn()) {
            throw new IllegalStateException("User must be logged in");
        }

        Account account = accountService.findAccountById(accountId);

        // Validate ownership for clients
        if (authService.isClient()) {
            boolean ownsAccount = authService.getCurrentClient()
                    .map(client -> client.getAccounts().contains(account))
                    .orElse(false);
            if (!ownsAccount) {
                throw new IllegalStateException("Client can only access their own accounts");
            }
        }

        // Create deposit transaction
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, amount, description, account);
        account.deposit(amount);
        account.addTransaction(transaction);

        return transaction;
    }

    public Transaction makeWithdrawal(String accountId, double amount, String description) {
        if (!authService.isLoggedIn()) {
            throw new IllegalStateException("User must be logged in");
        }

        Account account = accountService.findAccountById(accountId);

        // Validate ownership for clients
        if (authService.isClient()) {
            boolean ownsAccount = authService.getCurrentClient()
                    .map(client -> client.getAccounts().contains(account))
                    .orElse(false);
            if (!ownsAccount) {
                throw new IllegalStateException("Client can only access their own accounts");
            }
        }

        // Validate sufficient balance
        if (account.getBalance() < amount) {
            throw new ArithmeticException("Insufficient balance for withdrawal");
        }

        // Create withdrawal transaction
        Transaction transaction = new Transaction(TransactionType.WITHDRAWAL, amount, description, account);
        account.withdraw(amount);
        account.addTransaction(transaction);

        return transaction;
    }

    public Transaction makeTransfer(String fromAccountId, String toAccountId, double amount, String description) {
        if (!authService.isLoggedIn()) {
            throw new IllegalStateException("User must be logged in");
        }

        Account fromAccount = accountService.findAccountById(fromAccountId);
        Account toAccount = accountService.findAccountById(toAccountId);

        // Validate ownership for clients
        if (authService.isClient()) {
            boolean ownsFromAccount = authService.getCurrentClient()
                    .map(client -> client.getAccounts().contains(fromAccount))
                    .orElse(false);
            if (!ownsFromAccount) {
                throw new IllegalStateException("Client can only transfer from their own accounts");
            }
        }

        // Validate sufficient balance
        if (fromAccount.getBalance() < amount) {
            throw new ArithmeticException("Insufficient balance for transfer");
        }

        // Create transfer transaction
        Transaction transaction = new Transaction(TransactionType.TRANSFER, amount, description, fromAccount, toAccount);

        // Update balances
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        // Add transaction to both accounts
        fromAccount.addTransaction(transaction);

        return transaction;
    }

    public List<Transaction> getTransactionsByAccount(String accountId) {
        if (!authService.isLoggedIn()) {
            throw new IllegalStateException("User must be logged in");
        }

        Account account = accountService.findAccountById(accountId);

        // Validate ownership for clients
        if (authService.isClient()) {
            boolean ownsAccount = authService.getCurrentClient()
                    .map(client -> client.getAccounts().contains(account))
                    .orElse(false);
            if (!ownsAccount) {
                throw new IllegalStateException("Client can only access their own accounts");
            }
        }

        return account.getTransactions();
    }

    public List<Transaction> filterTransactions(String accountId, Predicate<Transaction> filter) {
        return getTransactionsByAccount(accountId).stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByDateRange(String accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return filterTransactions(accountId, transaction -> {
            LocalDateTime transactionDate = transaction.getDate();
            return transactionDate.isAfter(startDate) && transactionDate.isBefore(endDate);
        });
    }

    public List<Transaction> getTransactionsByType(String accountId, TransactionType type) {
        return filterTransactions(accountId, transaction -> transaction.getTransactionType() == type);
    }

    public List<Transaction> getTransactionsByAmountRange(String accountId, double minAmount, double maxAmount) {
        return filterTransactions(accountId, transaction ->
            transaction.getAmount() >= minAmount && transaction.getAmount() <= maxAmount);
    }
}
