package services;

import models.Client;
import models.Account;
import models.Manager;
import models.Transaction;
import models.enums.TransactionType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClientService {
    private final List<Client> clients;

    public ClientService() {
        this.clients = new ArrayList<>();
    }

    public Client createClient(Manager manager, String firstName, String lastName, String email, String password) {
        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("All client information must be provided");
        }

        if (findClientByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Client with this email already exists");
        }

        Client client = new Client(firstName, lastName, email, password);
        clients.add(client);

        if (manager != null) {
            manager.addClient(client);
        }

        return client;
    }

    public Client updateClient(String clientId, String firstName, String lastName, String email) {
        Client client = findClientById(clientId);

        if (firstName != null && !firstName.trim().isEmpty()) {
            client.setFirstName(firstName);
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            client.setLastName(lastName);
        }
        if (email != null && !email.trim().isEmpty()) {
            Optional<Client> existingClient = findClientByEmail(email);
            if (existingClient.isPresent() && !existingClient.get().getClientId().equals(clientId)) {
                throw new IllegalArgumentException("Email already exists for another client");
            }
            client.setEmail(email);
        }

        return client;
    }

    public void deleteClient(Manager manager, String clientId) {
        Client client = findClientById(clientId);

        boolean hasBalance = client.getAccounts().stream()
            .anyMatch(account -> account.getBalance() != 0);

        if (hasBalance) {
            throw new IllegalStateException("Cannot delete client with accounts having non-zero balance");
        }

        if (manager != null) {
            manager.removeClient(client);
        }

        clients.remove(client);
    }

    public Client findClientById(String clientId) {
        return clients.stream()
                .filter(client -> client.getClientId().equals(clientId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Client not found with ID: " + clientId));
    }

    public Optional<Client> findClientByEmail(String email) {
        return clients.stream()
                .filter(client -> client.getEmail().equals(email))
                .findFirst();
    }

    public List<Client> getAllClients() {
        return new ArrayList<>(clients);
    }

    public List<Transaction> getClientTransactionHistory(String clientId) {
        Client client = findClientById(clientId);
        return client.getAccounts()
                .stream()
                .flatMap(account -> account.getTransactions().stream())
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Transaction> filterTransactionsByType(String clientId, TransactionType type) {
        return getClientTransactionHistory(clientId)
                .stream()
                .filter(transaction -> transaction.getTransactionType() == type)
                .collect(Collectors.toList());
    }

    public List<Transaction> filterTransactionsByAmount(String clientId, double minAmount, double maxAmount) {
        return getClientTransactionHistory(clientId)
                .stream()
                .filter(transaction -> transaction.getAmount() >= minAmount && transaction.getAmount() <= maxAmount)
                .collect(Collectors.toList());
    }

    public List<Transaction> filterTransactionsByDate(String clientId, LocalDateTime startDate, LocalDateTime endDate) {
        return getClientTransactionHistory(clientId)
                .stream()
                .filter(transaction -> {
                    LocalDateTime transactionDate = transaction.getDate();
                    return transactionDate.isAfter(startDate) && transactionDate.isBefore(endDate);
                })
                .collect(Collectors.toList());
    }

    public List<Transaction> filterTransactions(String clientId, Predicate<Transaction> filter) {
        return getClientTransactionHistory(clientId)
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public List<Transaction> sortTransactionsByAmount(String clientId, boolean ascending) {
        Comparator<Transaction> comparator = Comparator.comparing(Transaction::getAmount);
        if (!ascending) {
            comparator = comparator.reversed();
        }

        return getClientTransactionHistory(clientId).stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public List<Transaction> sortTransactionsByDate(String clientId, boolean ascending) {
        Comparator<Transaction> comparator = Comparator.comparing(Transaction::getDate);
        if (!ascending) {
            comparator = comparator.reversed();
        }

        return getClientTransactionHistory(clientId)
                .stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public double calculateTotalBalance(String clientId) {
        Client client = findClientById(clientId);
        return client.getAccounts()
                .stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }

    public double calculateTotalDeposits(String clientId) {
        return getClientTransactionHistory(clientId)
                .stream()
                .filter(transaction -> transaction.getTransactionType() == TransactionType.DEPOSIT)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double calculateTotalWithdrawals(String clientId) {
        return getClientTransactionHistory(clientId)
                .stream()
                .filter(transaction -> transaction.getTransactionType() == TransactionType.WITHDRAWAL)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double calculateTotalTransfers(String clientId) {
        return getClientTransactionHistory(clientId)
                .stream()
                .filter(transaction -> transaction.getTransactionType() == TransactionType.TRANSFER)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public boolean validateClientCredentials(String email, String password) {
        return clients.stream()
                .anyMatch(client -> client.getEmail().equals(email) && client.getPassword().equals(password));
    }

    public void addClient(Client client) {
        clients.add(client);
    }

    public void removeClient(Client client) {
        clients.remove(client);
    }

}
