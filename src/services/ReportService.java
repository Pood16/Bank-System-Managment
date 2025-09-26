package services;

import models.Account;
import models.Client;
import models.Transaction;
import models.enums.AccountType;
import models.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {
    private final ClientService clientService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public ReportService(ClientService clientService, AccountService accountService, TransactionService transactionService) {
        this.clientService = clientService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    public String generateClientReport(String clientId) {
        Client client = clientService.findClientById(clientId);

        StringBuilder report = new StringBuilder();
        report.append("=== CLIENT REPORT ===\n");
        report.append("Name: ").append(client.getFullName()).append("\n");
        report.append("Email: ").append(client.getEmail()).append("\n");
        report.append("Client ID: ").append(client.getClientId()).append("\n");
        report.append("Number of Accounts: ").append(client.getAccounts().size()).append("\n");
        report.append("Total Balance: $").append(clientService.calculateTotalBalance(clientId)).append("\n\n");

        report.append("ACCOUNTS:\n");
        client.getAccounts().forEach(account -> {
            report.append("- ").append(account.getAccountType())
                  .append(" (").append(account.getAccountId()).append("): $")
                  .append(account.getBalance()).append("\n");
        });

        List<Transaction> transactions = clientService.getClientTransactionHistory(clientId);
        report.append("\nTRANSACTION SUMMARY:\n");
        report.append("Total Transactions: ").append(transactions.size()).append("\n");

        double totalDeposits = transactions.stream()
            .filter(t -> t.getTransactionType() == TransactionType.DEPOSIT)
            .mapToDouble(Transaction::getAmount)
            .sum();

        double totalWithdrawals = transactions.stream()
            .filter(t -> t.getTransactionType() == TransactionType.WITHDRAWAL)
            .mapToDouble(Transaction::getAmount)
            .sum();

        report.append("Total Deposits: $").append(totalDeposits).append("\n");
        report.append("Total Withdrawals: $").append(totalWithdrawals).append("\n");

        return report.toString();
    }

    public String generateAccountReport(String accountId) {
        Account account = accountService.findAccountById(accountId);

        StringBuilder report = new StringBuilder();
        report.append("=== ACCOUNT REPORT ===\n");
        report.append("Account ID: ").append(account.getAccountId()).append("\n");
        report.append("Account Type: ").append(account.getAccountType()).append("\n");
        report.append("Current Balance: $").append(account.getBalance()).append("\n");
        report.append("Owner: ").append(account.getClient().getFullName()).append("\n");
        report.append("Number of Transactions: ").append(account.getTransactions().size()).append("\n\n");

        report.append("RECENT TRANSACTIONS:\n");
        account.getTransactions().stream()
            .sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
            .limit(10)
            .forEach(transaction -> {
                report.append("- ").append(transaction.getDate().toLocalDate())
                      .append(" | ").append(transaction.getTransactionType())
                      .append(" | $").append(transaction.getAmount())
                      .append(" | ").append(transaction.getDescription()).append("\n");
            });

        return report.toString();
    }

    public String generateSystemReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== SYSTEM REPORT ===\n");

        List<Client> allClients = clientService.getAllClients();
        List<Account> allAccounts = accountService.getAllAccounts();
        List<Transaction> allTransactions = transactionService.getAllTransactions();

        report.append("Total Clients: ").append(allClients.size()).append("\n");
        report.append("Total Accounts: ").append(allAccounts.size()).append("\n");
        report.append("Total Transactions: ").append(allTransactions.size()).append("\n\n");

        // Account type distribution
        Map<AccountType, Long> accountTypeCounts = allAccounts.stream()
            .collect(Collectors.groupingBy(Account::getAccountType, Collectors.counting()));

        report.append("ACCOUNT DISTRIBUTION:\n");
        accountTypeCounts.forEach((type, count) ->
            report.append("- ").append(type).append(": ").append(count).append("\n"));

        // Transaction type distribution
        Map<TransactionType, Long> transactionTypeCounts = allTransactions.stream()
            .collect(Collectors.groupingBy(Transaction::getTransactionType, Collectors.counting()));

        report.append("\nTRANSACTION DISTRIBUTION:\n");
        transactionTypeCounts.forEach((type, count) ->
            report.append("- ").append(type).append(": ").append(count).append("\n"));

        // Total system balance
        double totalSystemBalance = allAccounts.stream()
            .mapToDouble(Account::getBalance)
            .sum();

        report.append("\nTotal System Balance: $").append(totalSystemBalance).append("\n");

        return report.toString();
    }

    public String generateSuspiciousActivityReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== SUSPICIOUS ACTIVITY REPORT ===\n");

        List<Transaction> suspiciousTransactions = transactionService.getSuspiciousTransactions();


        report.append("High-Value Transactions (>$10,000):\n");
        if (suspiciousTransactions.isEmpty()) {
            report.append("No suspicious high-value transactions found.\n");
        } else {
            suspiciousTransactions.forEach(transaction -> {
                report.append("- ").append(transaction.getDate().toLocalDate())
                      .append(" | ").append(transaction.getTransactionType())
                      .append(" | $").append(transaction.getAmount())
                      .append(" | Account: ").append(transaction.getSourceAccount().getAccountId())
                      .append("\n");
            });
        }

        return report.toString();
    }

    public String generateTransactionReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);

        StringBuilder report = new StringBuilder();
        report.append("=== TRANSACTION REPORT ===\n");
        report.append("Period: ").append(startDate.toLocalDate()).append(" to ").append(endDate.toLocalDate()).append("\n");
        report.append("Total Transactions: ").append(transactions.size()).append("\n\n");

        Map<TransactionType, Double> totalsByType = transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getTransactionType,
                Collectors.summingDouble(Transaction::getAmount)
            ));

        report.append("TOTALS BY TYPE:\n");
        totalsByType.forEach((type, total) ->
            report.append("- ").append(type).append(": $").append(total).append("\n"));

        return report.toString();
    }
}
