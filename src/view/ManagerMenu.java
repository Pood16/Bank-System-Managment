package view;

import controllers.AuthController;
import controllers.ManagerController;
import models.Manager;
import models.Client;
import models.Account;
import models.Transaction;
import models.enums.AccountType;
import models.enums.TransactionType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Collections;

public class ManagerMenu {
    private final ManagerController managerController;
    private final AuthController authController;
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ManagerMenu(ManagerController managerController, AuthController authController) {
        this.managerController = managerController;
        this.authController = authController;
        this.scanner = new Scanner(System.in);
    }

    public void displayManagerMenu() {
        boolean running = true;
        while (running && authController.isLoggedIn() && authController.isManager()) {
            running = showManagerOptions();
        }
    }

    private boolean showManagerOptions() {
        Manager currentManager = authController.getCurrentManager().orElseThrow(() -> new IllegalStateException("No manager logged in"));
        System.out.println("    MANAGER DASHBOARD - Welcome " + currentManager.getFullName());
        System.out.println("CLIENT MANAGEMENT:");
        System.out.println("1.  Create New Client");
        System.out.println("2.  Update Client Information");
        System.out.println("3.  Delete Client");
        System.out.println("4.  View My Clients");
        System.out.println();
        System.out.println("ACCOUNT MANAGEMENT:");
        System.out.println("5.  Create Account for Client");
        System.out.println("6.  Update Account Type");
        System.out.println("7.  Delete Account");
        System.out.println();
        System.out.println("TRANSACTION MANAGEMENT:");
        System.out.println("8.  Add Deposit Transaction");
        System.out.println("9. Add Withdrawal Transaction");
        System.out.println("10. View Client Transactions");
        System.out.println("11. View All Transactions");
        System.out.println("12. Filter Transactions by Type");
        System.out.println("13. Filter Transactions by Amount");
        System.out.println("14. Filter Transactions by Date");
        System.out.println();
        System.out.println("REPORTS:");
        System.out.println("15. Detect Suspicious Transactions");
        System.out.println("16. View Transaction Statistics");
        System.out.println("17. View Account Type Statistics");
        System.out.println("18. View Top Clients by Balance");
        System.out.println("19. Calculate Total System Balance");
        System.out.println();
        System.out.println("20. Logout");
        System.out.print("Choose an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1: createClient(); break;
                case 2: updateClient(); break;
                case 3: deleteClient(); break;
                case 4: viewMyClients(); break;
                case 5: createAccountForClient(); break;
                case 6: updateAccountType(); break;
                case 7: deleteAccount(); break;
                case 8: addDepositTransaction(); break;
                case 9: addWithdrawalTransaction(); break;
                case 10: viewClientTransactions(); break;
                case 11: viewAllTransactions(); break;
                case 12: filterTransactionsByType(); break;
                case 13: filterTransactionsByAmount(); break;
                case 14: filterTransactionsByDate(); break;
                case 15: detectSuspiciousTransactions(); break;
                case 16: viewTransactionStatistics(); break;
                case 17: viewAccountTypeStatistics(); break;
                case 18: viewTopClientsByBalance(); break;
//                case 19: calculateTotalSystemBalance(); break;
                case 20:
                    authController.logout();
                    System.out.println("Logged out successfully!");
                    return false;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }

    private void createClient() {
        System.out.println("\n--- CREATE NEW CLIENT ---");
        System.out.print("First Name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Last Name: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        try {
            Client client = managerController.createClient(firstName, lastName, email, password);
            System.out.println("Client created successfully!");
            System.out.println("Client ID: " + client.getId());
            System.out.println("Name: " + client.getFullName());
        } catch (Exception e) {
            System.out.println("Failed to create client: " + e.getMessage());
        }
    }

    private void updateClient() {
        System.out.println("\n--- UPDATE CLIENT ---");
        System.out.print("Client ID: ");
        String clientId = scanner.nextLine().trim();

        if (clientId.isEmpty()) {
            System.out.println("Client ID is required!");
            return;
        }

        System.out.println("Leave fields empty to keep current values");
        System.out.print("New First Name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("New Last Name: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("New Email: ");
        String email = scanner.nextLine().trim();

        try {
            Client client = managerController.updateClient(
                clientId,
                firstName.isEmpty() ? null : firstName,
                lastName.isEmpty() ? null : lastName,
                email.isEmpty() ? null : email
            );
            System.out.println("Client updated successfully!");
            System.out.println("Updated Name: " + client.getFullName());
        } catch (Exception e) {
            System.out.println("Failed to update client: " + e.getMessage());
        }
    }

    private void deleteClient() {
        System.out.println("\n--- DELETE CLIENT ---");
        System.out.print("Client ID: ");
        String clientId = scanner.nextLine().trim();

        if (clientId.isEmpty()) {
            System.out.println("Client ID is required!");
            return;
        }

        System.out.print("Are you sure you want to delete this client? (yes/no): ");
        String confirmation = scanner.nextLine().trim();

        if (!"yes".equalsIgnoreCase(confirmation)) {
            System.out.println("Delete cancelled.");
            return;
        }

        try {
            managerController.deleteClient(clientId);
            System.out.println("Client deleted successfully!");
        } catch (Exception e) {
            System.out.println("Failed to delete client: " + e.getMessage());
        }
    }

    private void viewMyClients() {
        System.out.println("\n--- MY CLIENTS ---");
        List<Client> clients = managerController.viewMyClients();
        for (Client client : clients) {
            System.out.println(client);
        }
    }


    private void createAccountForClient() {
        System.out.println("\n--- CREATE ACCOUNT ---");
        System.out.print("Client ID: ");
        String clientId = scanner.nextLine().trim();

        System.out.println("Account Types:");
        System.out.println("1. CURRENT");
        System.out.println("2. SAVINGS");
        System.out.println("3. TERM_DEPOSIT");
        System.out.print("Choose account type: ");

        try {
            int typeChoice = Integer.parseInt(scanner.nextLine().trim());
            AccountType accountType = null;
            switch (typeChoice) {
                case 1: accountType = AccountType.CURRENT; break;
                case 2: accountType = AccountType.SAVINGS; break;
                case 3: accountType = AccountType.TERM_DEPOSIT; break;
                default:
                    System.out.println("Invalid account type choice.");
                    return;
            }

            System.out.print("Initial Balance: ");
            double initialBalance = Double.parseDouble(scanner.nextLine().trim());

            Account account = managerController.createAccountForClient(clientId, accountType, initialBalance);
            System.out.println("Account created successfully!");
            System.out.println("Account ID: " + account.getAccountId());
            System.out.println("Type: " + account.getAccountType());
            System.out.println("Initial Balance: " + account.getBalance() + " MAD");
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numbers.");
        } catch (Exception e) {
            System.out.println("Failed to create account: " + e.getMessage());
        }
    }

    private void updateAccountType() {
        System.out.println("\n--- UPDATE ACCOUNT TYPE ---");
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine().trim();

        System.out.println("New Account Types:");
        System.out.println("1. CURRENT");
        System.out.println("2. SAVINGS");
        System.out.println("3. TERM_DEPOSIT");
        System.out.print("Choose new account type: ");

        try {
            int typeChoice = Integer.parseInt(scanner.nextLine().trim());
            AccountType newAccountType = null;
            switch (typeChoice) {
                case 1: newAccountType = AccountType.CURRENT; break;
                case 2: newAccountType = AccountType.SAVINGS; break;
                case 3: newAccountType = AccountType.TERM_DEPOSIT; break;
                default:
                    System.out.println("Invalid account type choice.");
                    return;
            }

            Account account = managerController.updateAccountType(accountId, newAccountType);
            System.out.println("Account type updated successfully!");
            System.out.println("Account ID: " + account.getAccountId());
            System.out.println("New Type: " + account.getAccountType());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Failed to update account type: " + e.getMessage());
        }
    }

    private void deleteAccount() {
        System.out.println("\n--- DELETE ACCOUNT ---");
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine().trim();

        if (accountId.isEmpty()) {
            System.out.println("Account ID is required!");
            return;
        }

        System.out.print("Are you sure you want to delete this account? (yes/no): ");
        String confirmation = scanner.nextLine().trim();

        if (!"yes".equalsIgnoreCase(confirmation)) {
            System.out.println("Delete cancelled.");
            return;
        }

        try {
            managerController.deleteAccount(accountId);
            System.out.println("Account deleted successfully!");
        } catch (Exception e) {
            System.out.println("Failed to delete account: " + e.getMessage());
        }
    }


    private void addDepositTransaction() {
        System.out.println("\n--- ADD DEPOSIT ---");
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine().trim();

        System.out.print("Amount: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Description: ");
            String description = scanner.nextLine().trim();

            Transaction transaction = managerController.addDepositTransaction(accountId, amount, description);
            System.out.println("Deposit transaction added successfully!");
            System.out.println("Transaction ID: " + transaction.getTransactionId());
            System.out.println("Amount: " + transaction.getAmount() + " MAD");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid amount.");
        } catch (Exception e) {
            System.out.println("Failed to add deposit: " + e.getMessage());
        }
    }

    private void addWithdrawalTransaction() {
        System.out.println("\n--- ADD WITHDRAWAL ---");
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine().trim();

        System.out.print("Amount: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Description: ");
            String description = scanner.nextLine().trim();

            Transaction transaction = managerController.addWithdrawalTransaction(accountId, amount, description);
            System.out.println("Withdrawal transaction added successfully!");
            System.out.println("Transaction ID: " + transaction.getTransactionId());
            System.out.println("Amount: " + transaction.getAmount() + " MAD");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid amount.");
        } catch (Exception e) {
            System.out.println("Failed to add withdrawal: " + e.getMessage());
        }
    }

    private void viewClientTransactions() {
        System.out.println("\n--- CLIENT TRANSACTIONS ---");
        System.out.print("Client ID: ");
        String clientId = scanner.nextLine().trim();

        if (clientId.isEmpty()) {
            System.out.println("Client ID is required!");
            return;
        }

        try {
            List<Transaction> transactions = managerController.viewClientTransactions(clientId);
            System.out.println("\n--- TRANSACTIONS FOR CLIENT " + clientId + " ---");
            displayTransactions(transactions);
        } catch (Exception e) {
            System.out.println("Failed to retrieve transactions: " + e.getMessage());
        }
    }

    private void viewAllTransactions() {
        System.out.println("\n--- ALL SYSTEM TRANSACTIONS ---");
        try {
            List<Transaction> transactions = managerController.viewAllTransactions();
            displayTransactions(transactions);
        } catch (Exception e) {
            System.out.println("Failed to retrieve transactions: " + e.getMessage());
        }
    }

    private void filterTransactionsByType() {
        System.out.println("\n--- FILTER BY TRANSACTION TYPE ---");
        System.out.println("1. DEPOSIT");
        System.out.println("2. WITHDRAWAL");
        System.out.println("3. TRANSFER");
        System.out.print("Choose transaction type: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            TransactionType type = null;
            switch (choice) {
                case 1: type = TransactionType.DEPOSIT; break;
                case 2: type = TransactionType.WITHDRAWAL; break;
                case 3: type = TransactionType.TRANSFER; break;
                default:
                    System.out.println("Invalid choice.");
                    return;
            }

            List<Transaction> transactions = managerController.filterTransactionsByType(type);
            System.out.println("\n--- " + type + " TRANSACTIONS ---");
            displayTransactions(transactions);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Failed to filter transactions: " + e.getMessage());
        }
    }

    private void filterTransactionsByAmount() {
        System.out.println("\n--- FILTER BY AMOUNT RANGE ---");
        try {
            System.out.print("Minimum amount: ");
            double minAmount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Maximum amount: ");
            double maxAmount = Double.parseDouble(scanner.nextLine().trim());

            List<Transaction> transactions = managerController.filterTransactionsByAmount(minAmount, maxAmount);
            System.out.println("\n--- TRANSACTIONS BETWEEN " + minAmount + " AND " + maxAmount + " MAD ---");
            displayTransactions(transactions);
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numbers.");
        } catch (Exception e) {
            System.out.println("Failed to filter transactions: " + e.getMessage());
        }
    }

    private void filterTransactionsByDate() {
        System.out.println("\n--- FILTER BY DATE RANGE ---");
        System.out.println("Date format: yyyy-MM-dd HH:mm (e.g., 2024-01-15 10:30)");

        try {
            System.out.print("Start date: ");
            String startDateStr = scanner.nextLine().trim();
            LocalDateTime startDate = LocalDateTime.parse(startDateStr, dateFormatter);

            System.out.print("End date: ");
            String endDateStr = scanner.nextLine().trim();
            LocalDateTime endDate = LocalDateTime.parse(endDateStr, dateFormatter);

            List<Transaction> transactions = managerController.filterTransactionsByDate(startDate, endDate);
            System.out.println("\n--- TRANSACTIONS FROM " + startDateStr + " TO " + endDateStr + " ---");
            displayTransactions(transactions);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd HH:mm");
        } catch (Exception e) {
            System.out.println("Failed to filter transactions: " + e.getMessage());
        }
    }


    private void detectSuspiciousTransactions() {
        System.out.println("\n--- SUSPICIOUS TRANSACTIONS DETECTED ---");
        try {
            List<Transaction> suspiciousTransactions = managerController.detectSuspiciousTransactions();

            if (suspiciousTransactions.isEmpty()) {
                System.out.println("No suspicious transactions detected.");
            } else {
                System.out.println("Found " + suspiciousTransactions.size() + " suspicious transactions:");
                for (Transaction transaction : suspiciousTransactions ) {
                    System.out.println(transaction);
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to detect suspicious transactions: " + e.getMessage());
        }
    }

    private void viewTransactionStatistics() {
        System.out.println("\n--- TRANSACTION STATISTICS ---");
        try {
            Map<TransactionType, Long> stats = managerController.getTransactionStatistics();

            System.out.println("Transaction counts by type:");
            for (Map.Entry<TransactionType, Long> entry : stats.entrySet()) {
                System.out.printf("%-12s: %d transactions%n", entry.getKey(), entry.getValue());
            }

            long total = stats.values().stream().mapToLong(Long::longValue).sum();
            System.out.println(String.join("", Collections.nCopies(30, "-")));
            System.out.printf("Total: %d transactions%n", total);
        } catch (Exception e) {
            System.out.println("Failed to get transaction statistics: " + e.getMessage());
        }
    }

    private void viewAccountTypeStatistics() {
        System.out.println("\n--- ACCOUNT TYPE STATISTICS ---");
        try {
            Map<AccountType, Long> stats = managerController.getAccountTypeStatistics();

            System.out.println("Account counts by type:");
            for (Map.Entry<AccountType, Long> entry : stats.entrySet()) {
                System.out.printf("%-15s: %d accounts%n", entry.getKey(), entry.getValue());
            }

            long total = stats.values().stream().mapToLong(Long::longValue).sum();
            System.out.println(String.join("", Collections.nCopies(30, "-")));
            System.out.printf("Total: %d accounts%n", total);
        } catch (Exception e) {
            System.out.println("Failed to get account statistics: " + e.getMessage());
        }
    }

    private void viewTopClientsByBalance() {
        System.out.println("\n--- TOP CLIENTS BY BALANCE ---");
        System.out.print("Number of top clients to show (default 10): ");
        String input = scanner.nextLine().trim();

        int limit = 10;
        if (!input.isEmpty()) {
            try {
                limit = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, using default (10).");
            }
        }

        try {
            List<Client> topClients = managerController.getTopClientsByBalance(limit);

            if (topClients.isEmpty()) {
                System.out.println("No clients found.");
                return;
            }

            System.out.printf("%-15s %-25s %-15s%n", "Client ID", "Name", "Total Balance");
            System.out.println(String.join("", Collections.nCopies(60, "-")));

            for (Client client : topClients) {
                double balance = managerController.calculateClientTotalBalance(client.getId());
                System.out.printf("%-15s %-25s %-15.2f%n",
                    client.getId(),
                    client.getFullName(),
                    balance);
            }
        } catch (Exception e) {
            System.out.println("Failed to get top clients: " + e.getMessage());
        }
    }

    private void displayTransactions(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.printf("%-15s %-12s %-10s %-20s %-30s%n",
            "Transaction ID", "Type", "Amount", "Date", "Description");
        System.out.println(String.join("", Collections.nCopies(90, "-")));

        for (Transaction transaction : transactions) {
            System.out.printf("%-15s %-12s %-10.2f %-20s %-30s%n",
                transaction.getTransactionId().substring(0, Math.min(12, transaction.getTransactionId().length())),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getDate().format(dateFormatter),
                transaction.getDescription() != null ? transaction.getDescription() : "N/A");
        }

        System.out.println("\nTotal transactions: " + transactions.size());
    }

}
