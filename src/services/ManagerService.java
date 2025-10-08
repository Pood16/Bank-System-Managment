package services;

import models.Manager;
import models.Client;
import models.Account;
import models.Transaction;
import models.enums.DepartmentType;
import models.enums.AccountType;
import models.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ManagerService {
    private final List<Manager> managers;
    private final ClientService clientService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public ManagerService(ClientService clientService, AccountService accountService, TransactionService transactionService) {
        this.managers = new ArrayList<>();
        this.clientService = clientService;
        this.accountService = accountService;
        this.transactionService = transactionService;
        Manager defaultManager = new Manager("Admin", "Manager", "admin@bank.ma", "admin123", DepartmentType.IT);
        managers.add(defaultManager);
    }

    public Manager createManager(String firstName, String lastName, String email, String password, DepartmentType department) {
        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            department == null) {
            throw new IllegalArgumentException("All manager information must be provided");
        }

        Optional<Manager> existingManager = findManagerByEmail(email);
        if (existingManager.isPresent()) {
            throw new IllegalArgumentException("Manager with this email already exists");
        }

        Manager manager = new Manager(firstName, lastName, email, password, department);
        managers.add(manager);
        return manager;
    }

    public Optional<Manager> findManagerByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        return managers.stream()
                .filter(manager -> manager.getEmail().equals(email))
                .findFirst();
    }

    public List<Manager> getAllManagers() {
        return new ArrayList<>(managers);
    }

    public Client createClientAsManager(Manager manager, String firstName, String lastName, String email, String password) {
        return clientService.createClient(manager, firstName, lastName, email, password);
    }

    public Client updateClientAsManager(String clientId, String firstName, String lastName, String email) {
        return clientService.updateClient(clientId, firstName, lastName, email);
    }

    public void deleteClientAsManager(Manager manager, String clientId) {
        clientService.deleteClient(manager, clientId);
    }

    public List<Client> getManagerClients(Manager manager) {
        return new ArrayList<>(manager.getClientList());
    }

    public Account createAccountForClient(String clientId, AccountType accountType, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        return accountService.createAccount(accountType, clientId, initialBalance);
    }

    public Account updateAccountType(String accountId, AccountType newAccountType) {
        return accountService.updateAccountType(accountId, newAccountType);
    }

    public void deleteAccount(String accountId) {
        accountService.deleteAccount(accountId);
    }

    public Transaction addDepositTransaction(String accountId, double amount, String description) {
        return transactionService.MakeDeposit(accountId, amount, description);
    }

    public Transaction addWithdrawalTransaction(String accountId, double amount, String description) {
        return transactionService.MakeWithdrawal(accountId, amount, description);
    }

    public List<Transaction> getClientTransactions(String clientId) {
        return clientService.getClientTransactionHistory(clientId);
    }

    public List<Transaction> getAllTransactions() {
        return clientService.getAllClients()
                .stream()
                .flatMap(client -> client.getAccounts().stream())
                .flatMap(account -> account.getTransactions().stream())
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Transaction> filterTransactionsByType(TransactionType type) {
        return getAllTransactions().stream()
                .filter(transaction -> transaction.getTransactionType() == type)
                .collect(Collectors.toList());
    }

    public List<Transaction> filterTransactionsByAmount(double minAmount, double maxAmount) {
        return getAllTransactions().stream()
                .filter(transaction -> transaction.getAmount() >= minAmount && transaction.getAmount() <= maxAmount)
                .collect(Collectors.toList());
    }

    public List<Transaction> filterTransactionsByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return getAllTransactions().stream()
                .filter(transaction -> {
                    LocalDateTime transactionDate = transaction.getDate();
                    return transactionDate.isAfter(startDate) && transactionDate.isBefore(endDate);
                })
                .collect(Collectors.toList());
    }

    public List<Transaction> filterTransactions(Predicate<Transaction> filter) {
        return getAllTransactions().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public List<Transaction> detectSuspiciousTransactions() {
        List<Transaction> suspiciousTransactions = new ArrayList<>();
        List<Transaction> allTransactions = getAllTransactions();

        double highAmountThreshold = 10000.0;

        List<Transaction> highAmountTransactions = allTransactions
                .stream()
                .filter(transaction -> transaction.getAmount() > highAmountThreshold)
                .collect(Collectors.toList());
        suspiciousTransactions.addAll(highAmountTransactions);

        return suspiciousTransactions.stream()
                .distinct()
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());
    }

    public double calculateTotalBalanceAllClients() {
        return clientService.getAllClients().stream()
                .mapToDouble(client -> clientService.calculateTotalBalance(client.getClientId()))
                .sum();
    }

    public double calculateTotalDepositsByClient(String clientId) {
        return clientService.calculateTotalDeposits(clientId);
    }

    public double calculateTotalWithdrawalsByClient(String clientId) {
        return clientService.calculateTotalWithdrawals(clientId);
    }

    public double calculateTotalTransfersByClient(String clientId) {
        return clientService.calculateTotalTransfers(clientId);
    }

    public Map<TransactionType, Long> getTransactionStatistics() {
        return getAllTransactions().stream()
                .collect(Collectors.groupingBy(Transaction::getTransactionType, Collectors.counting()));
    }

    public Map<AccountType, Long> getAccountTypeStatistics() {
        return clientService.getAllClients().stream()
                .flatMap(client -> client.getAccounts().stream())
                .collect(Collectors.groupingBy(Account::getAccountType, Collectors.counting()));
    }

    public List<Client> getTopClientsByBalance(int limit) {
        return clientService.getAllClients().stream()
                .sorted((c1, c2) -> Double.compare(
                    clientService.calculateTotalBalance(c2.getClientId()),
                    clientService.calculateTotalBalance(c1.getClientId())
                ))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public boolean validateManagerCredentials(String email, String password) {
        return managers.stream()
                .anyMatch(manager -> manager.getEmail().equals(email) && manager.getPassword().equals(password));
    }

    public boolean validateSufficientBalance(String accountId, double amount) {
        try {
            Account account = accountService.findAccountById(accountId);
            return account.getBalance() >= amount;
        } catch (NoSuchElementException e) {
            return false;
        }
    }



    public void addManager(Manager manager) {
        managers.add(manager);
    }

    public void removeManager(Manager manager) {
        managers.remove(manager);
    }
}
