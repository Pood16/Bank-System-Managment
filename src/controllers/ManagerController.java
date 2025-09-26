package controllers;

import models.Manager;
import models.Client;
import models.Account;
import models.Transaction;
import models.enums.AccountType;
import models.enums.TransactionType;
import models.enums.DepartmentType;
import services.ManagerService;
import services.ClientService;
import services.AccountService;
import services.AuthService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ManagerController {
    private final ManagerService managerService;
    private final ClientService clientService;
    private final AccountService accountService;
    private final AuthService authService;

    public ManagerController(ManagerService managerService, ClientService clientService,
                           AccountService accountService, AuthService authService) {
        this.managerService = managerService;
        this.clientService = clientService;
        this.accountService = accountService;
        this.authService = authService;
    }

    // Client Management
    public Client createClient(String firstName, String lastName, String email, String password) {
        Manager currentManager = authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.createClientAsManager(currentManager, firstName, lastName, email, password);
    }

    public Client updateClient(String clientId, String firstName, String lastName, String email) {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.updateClientAsManager(clientId, firstName, lastName, email);
    }

    public void deleteClient(String clientId) {
        Manager currentManager = authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        managerService.deleteClientAsManager(currentManager, clientId);
    }

    public List<Client> viewAllClients() {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return clientService.getAllClients();
    }

    public List<Client> viewMyClients() {
        Manager currentManager = authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.getManagerClients(currentManager);
    }

    // Account Management
    public Account createAccountForClient(String clientId, AccountType accountType, double initialBalance) {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.createAccountForClient(clientId, accountType, initialBalance);
    }

    public Account updateAccountType(String accountId, AccountType newAccountType) {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.updateAccountType(accountId, newAccountType);
    }

    public void deleteAccount(String accountId) {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        managerService.deleteAccount(accountId);
    }

    // Transaction Management
    public Transaction addDepositTransaction(String accountId, double amount, String description) {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.addDepositTransaction(accountId, amount, description);
    }

    public Transaction addWithdrawalTransaction(String accountId, double amount, String description) {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.addWithdrawalTransaction(accountId, amount, description);
    }

    // Transaction Viewing and Filtering
    public List<Transaction> viewClientTransactions(String clientId) {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.getClientTransactions(clientId);
    }

    public List<Transaction> viewAllTransactions() {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.getAllTransactions();
    }

    public List<Transaction> filterTransactionsByType(TransactionType type) {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.filterTransactionsByType(type);
    }

    public List<Transaction> filterTransactionsByAmount(double minAmount, double maxAmount) {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.filterTransactionsByAmount(minAmount, maxAmount);
    }

    public List<Transaction> filterTransactionsByDate(LocalDateTime startDate, LocalDateTime endDate) {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.filterTransactionsByDate(startDate, endDate);
    }

    // Suspicious Transaction Detection
    public List<Transaction> detectSuspiciousTransactions() {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.detectSuspiciousTransactions();
    }

    // Balance Calculations and Reporting
    public double calculateTotalBalanceAllClients() {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.calculateTotalBalanceAllClients();
    }

    public double calculateClientTotalBalance(String clientId) {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return clientService.calculateTotalBalance(clientId);
    }

    public double calculateClientTotalDeposits(String clientId) {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.calculateTotalDepositsByClient(clientId);
    }

    public double calculateClientTotalWithdrawals(String clientId) {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.calculateTotalWithdrawalsByClient(clientId);
    }

    // Statistics and Analytics
    public Map<TransactionType, Long> getTransactionStatistics() {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.getTransactionStatistics();
    }

    public Map<AccountType, Long> getAccountTypeStatistics() {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.getAccountTypeStatistics();
    }

    public List<Client> getTopClientsByBalance(int limit) {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.getTopClientsByBalance(limit);
    }

    // Validation
    public boolean validateSufficientBalance(String accountId, double amount) {
        authService.getCurrentManager()
                .orElseThrow(() -> new IllegalStateException("No manager logged in"));
        return managerService.validateSufficientBalance(accountId, amount);
    }
}
