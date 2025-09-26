package controllers;

import models.Client;
import models.Account;
import models.Transaction;
import models.enums.TransactionType;
import services.ClientService;
import services.AuthService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ClientController {
    private final ClientService clientService;
    private final AuthService authService;

    public ClientController(ClientService clientService, AuthService authService) {
        this.clientService = clientService;
        this.authService = authService;
    }

    public Optional<Client> viewPersonalInformation() {
        return authService.getCurrentClient();
    }

    public List<Account> viewMyAccounts() {
        return authService.getCurrentClient()
                .map(client -> client.getAccounts())
                .orElseThrow(() -> new IllegalStateException("No client logged in"));
    }

    public List<Transaction> viewTransactionHistory() {
        return authService.getCurrentClient()
                .map(client -> clientService.getClientTransactionHistory(client.getClientId()))
                .orElseThrow(() -> new IllegalStateException("No client logged in"));
    }

    public List<Transaction> filterTransactionsByType(TransactionType type) {
        return authService.getCurrentClient()
                .map(client -> clientService.filterTransactionsByType(client.getClientId(), type))
                .orElseThrow(() -> new IllegalStateException("No client logged in"));
    }

    public List<Transaction> filterTransactionsByAmount(double minAmount, double maxAmount) {
        return authService.getCurrentClient()
                .map(client -> clientService.filterTransactionsByAmount(client.getClientId(), minAmount, maxAmount))
                .orElseThrow(() -> new IllegalStateException("No client logged in"));
    }

    public List<Transaction> filterTransactionsByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return authService.getCurrentClient()
                .map(client -> clientService.filterTransactionsByDate(client.getClientId(), startDate, endDate))
                .orElseThrow(() -> new IllegalStateException("No client logged in"));
    }

    public List<Transaction> sortTransactionsByAmount(boolean ascending) {
        return authService.getCurrentClient()
                .map(client -> clientService.sortTransactionsByAmount(client.getClientId(), ascending))
                .orElseThrow(() -> new IllegalStateException("No client logged in"));
    }

    public List<Transaction> sortTransactionsByDate(boolean ascending) {
        return authService.getCurrentClient()
                .map(client -> clientService.sortTransactionsByDate(client.getClientId(), ascending))
                .orElseThrow(() -> new IllegalStateException("No client logged in"));
    }

    public double calculateTotalBalance() {
        return authService.getCurrentClient()
                .map(client -> clientService.calculateTotalBalance(client.getClientId()))
                .orElseThrow(() -> new IllegalStateException("No client logged in"));
    }

    public double calculateTotalDeposits() {
        return authService.getCurrentClient()
                .map(client -> clientService.calculateTotalDeposits(client.getClientId()))
                .orElseThrow(() -> new IllegalStateException("No client logged in"));
    }

    public double calculateTotalWithdrawals() {
        return authService.getCurrentClient()
                .map(client -> clientService.calculateTotalWithdrawals(client.getClientId()))
                .orElseThrow(() -> new IllegalStateException("No client logged in"));
    }

    public double calculateTotalTransfers() {
        return authService.getCurrentClient()
                .map(client -> clientService.calculateTotalTransfers(client.getClientId()))
                .orElseThrow(() -> new IllegalStateException("No client logged in"));
    }

    public Client updatePersonalInformation(String firstName, String lastName, String email) {
        return authService.getCurrentClient()
                .map(client -> clientService.updateClient(client.getClientId(), firstName, lastName, email))
                .orElseThrow(() -> new IllegalStateException("No client logged in"));
    }
}
