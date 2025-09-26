package services;

import models.Account;
import models.Transaction;
import models.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public class TransactionService {
    private final List<Transaction> transactions;
    private final AccountService accountService;


    public TransactionService(AccountService accountService) {
        this.transactions = new ArrayList<>();
        this.accountService = accountService;
    }

    public Transaction MakeDeposit(String accountId, double amount, String description) {

        if (validateAmount(amount)) {
            throw new IllegalArgumentException("The minimum amount to deposit is 100 dh");
        }

        Account account = accountService.findAccountById(accountId);

        Transaction transaction = new Transaction(TransactionType.DEPOSIT, amount, description, account);
        account.deposit(amount);
        account.addTransaction(transaction);
        transactions.add(transaction);
        return transaction;
    }

    public Transaction MakeWithdrawal(String accountId, double amount, String description) {
        Account account = accountService.findAccountById(accountId);
        if (!accountService.canWithdraw(account, amount)) {
            throw new ArithmeticException("Insufficient balance for withdrawal");
        }
        Transaction transaction = new Transaction(TransactionType.WITHDRAWAL, amount, description, account);
        account.setBalance(amount);
        account.addTransaction(transaction);
        transactions.add(transaction);
        return transaction;
    }

    public Transaction makeTransfer(String sourceAccountId, String destinationAccountId, double amount, String description) {
        Account sourceAccount = accountService.findAccountById(sourceAccountId);
        Account destinationAccount = accountService.findAccountById(destinationAccountId);

        if (!accountService.canWithdraw(sourceAccount, amount)) {
            throw new ArithmeticException("Insufficient balance for withdrawal");
        }

        Transaction transaction = new Transaction(TransactionType.TRANSFER, amount, description, sourceAccount, destinationAccount);

        sourceAccount.withdraw(amount);
        sourceAccount.addTransaction(transaction);

        destinationAccount.deposit(amount);
        destinationAccount.addTransaction(transaction);
        transactions.add(transaction);

        return transaction;
    }

    public void updateTransaction(String transactionId, String newDescription) {
        Transaction transaction = findTransactionById(transactionId);
        transaction.setDescription(newDescription);
    }

    public void deleteTransaction(String transactionId) {
        Transaction transaction = findTransactionById(transactionId);
        transactions.remove(transaction);
    }

    public Transaction findTransactionById(String transactionId) {
        return transactions
                .stream()
                .filter(transaction -> transaction.getTransactionId().equals(transactionId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No Transaction Found with ID: " + transactionId));
    }

    public List<Transaction> getTransactionsByAccount(String accountId) {
        Account account = accountService.findAccountById(accountId);
        return transactions
                .stream()
                .filter(transaction -> transaction.getSourceAccount().equals(account) || (transaction.getDestinationAccount() != null && transaction.getDestinationAccount().equals(account)))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByType(TransactionType type) {
        return transactions
                .stream()
                .filter(transaction -> transaction.getTransactionType().equals(type))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactions
                .stream()
                .filter(transaction -> !transaction.getDate().isBefore(startDate) && !transaction.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsSortedByDate(boolean ascending) {
        return transactions
                .stream()
                .sorted(ascending ? Comparator.comparing(Transaction::getDate) : Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsSortedByAmount(boolean ascending) {
        return transactions
                .stream()
                .sorted(ascending ? Comparator.comparing(Transaction::getAmount) : Comparator.comparing(Transaction::getAmount).reversed())
                .collect(Collectors.toList());
    }

    public double calculateTotalByType(TransactionType type) {
        return transactions
                .stream()
                .filter(transaction -> transaction.getTransactionType().equals(type))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public List<Transaction> getSuspiciousTransactions() {
        return transactions
                .stream()
                .filter(transaction -> transaction.getAmount() > 10000.9)
                .collect(Collectors.toList());
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    private boolean validateAmount(double amount) {
        return amount <= 0;
    }
}
