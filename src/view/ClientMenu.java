package view;

import controllers.AuthController;
import controllers.ClientController;
import controllers.TransactionController;
import models.Client;
import models.Account;
import models.Transaction;
import models.enums.TransactionType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.Collections;

public class ClientMenu {
    private final ClientController clientController;
    private final TransactionController transactionController;
    private final AuthController authController;
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ClientMenu(ClientController clientController, TransactionController transactionController, AuthController authController) {
        this.clientController = clientController;
        this.transactionController = transactionController;
        this.authController = authController;
        this.scanner = new Scanner(System.in);
    }

    public void displayClientMenu() {
        boolean running = true;
        while (running && authController.isLoggedIn() && authController.isClient()) {
            running = showClientOptions();
        }
    }

    private boolean showClientOptions() {
        Client currentClient = authController.getCurrentClient()
                .orElseThrow(() -> new IllegalStateException("No client logged in"));

        String separator = String.join("", Collections.nCopies(60, "="));
        System.out.println("\n" + separator);
        System.out.println("    CLIENT DASHBOARD - Welcome " + currentClient.getFullName());
        System.out.println(separator);
        System.out.println("1.  View Personal Information");
        System.out.println("2.  View My Accounts");
        System.out.println("3.  View Transaction History");
        System.out.println("4.  Make Deposit");
        System.out.println("5.  Make Withdrawal");
        System.out.println("6.  Make Transfer");
        System.out.println("7.  Filter Transactions by Type");
        System.out.println("8.  Filter Transactions by Amount Range");
        System.out.println("9.  Filter Transactions by Date Range");
        System.out.println("10. Sort Transactions by Amount");
        System.out.println("11. Sort Transactions by Date");
        System.out.println("12. Calculate Total Balance");
        System.out.println("13. View Balance Summary");
        System.out.println("14. Update Personal Information");
        System.out.println("15. Logout");
        System.out.println(separator);
        System.out.print("Choose an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1: viewPersonalInformation(); break;
                case 2: viewMyAccounts(); break;
                case 3: viewTransactionHistory(); break;
                case 4: makeDeposit(); break;
                case 5: makeWithdrawal(); break;
                case 6: makeTransfer(); break;
                case 7: filterTransactionsByType(); break;
                case 8: filterTransactionsByAmount(); break;
                case 9: filterTransactionsByDate(); break;
                case 10: sortTransactionsByAmount(); break;
                case 11: sortTransactionsByDate(); break;
                case 12: calculateTotalBalance(); break;
                case 13: viewBalanceSummary(); break;
                case 14: updatePersonalInformation(); break;
                case 15:
                    authController.logout();
                    System.out.println("Logged out successfully!");
                    return false;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        waitForEnter();
        return true;
    }

    private void viewPersonalInformation() {
        System.out.println("\n--- PERSONAL INFORMATION ---");
        clientController.viewPersonalInformation().ifPresent(client -> {
            System.out.println("Client ID: " + client.getId());
            System.out.println("Name: " + client.getFullName());
            System.out.println("Email: " + client.getEmail());
            System.out.println("Number of Accounts: " + client.getAccounts().size());
        });
    }

    private void viewMyAccounts() {
        System.out.println("\n--- MY ACCOUNTS ---");
        List<Account> accounts = clientController.viewMyAccounts();

        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        System.out.printf("%-15s %-15s %-15s %-10s%n", "Account ID", "Account Type", "Balance", "Transactions");
        System.out.println(String.join("", Collections.nCopies(60, "-")));

        for (Account account : accounts) {
            System.out.printf("%-15s %-15s %-15.2f %-10d%n",
                account.getAccountId(),
                account.getAccountType(),
                account.getBalance(),
                account.getTransactions().size());
        }
    }

    private void viewTransactionHistory() {
        System.out.println("\n--- TRANSACTION HISTORY ---");
        List<Transaction> transactions = clientController.viewTransactionHistory();
        displayTransactions(transactions);
    }

    private void makeDeposit() {
        System.out.println("\n--- MAKE DEPOSIT ---");
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine().trim();

        System.out.print("Amount: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Description: ");
            String description = scanner.nextLine().trim();

            Transaction transaction = transactionController.makeDeposit(accountId, amount, description);
            System.out.println("Deposit successful!");
            System.out.println("Transaction ID: " + transaction.getTransactionId());
            System.out.println("Amount: " + transaction.getAmount() + " MAD");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid amount.");
        } catch (Exception e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }

    private void makeWithdrawal() {
        System.out.println("\n--- MAKE WITHDRAWAL ---");
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine().trim();

        System.out.print("Amount: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Description: ");
            String description = scanner.nextLine().trim();

            Transaction transaction = transactionController.makeWithdrawal(accountId, amount, description);
            System.out.println("Withdrawal successful!");
            System.out.println("Transaction ID: " + transaction.getTransactionId());
            System.out.println("Amount: " + transaction.getAmount() + " MAD");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid amount.");
        } catch (Exception e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }

    private void makeTransfer() {
        System.out.println("\n--- MAKE TRANSFER ---");
        System.out.print("From Account ID: ");
        String fromAccountId = scanner.nextLine().trim();

        System.out.print("To Account ID: ");
        String toAccountId = scanner.nextLine().trim();

        System.out.print("Amount: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Description: ");
            String description = scanner.nextLine().trim();

            Transaction transaction = transactionController.makeTransfer(fromAccountId, toAccountId, amount, description);
            System.out.println("Transfer successful!");
            System.out.println("Transaction ID: " + transaction.getTransactionId());
            System.out.println("Amount: " + transaction.getAmount() + " MAD");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid amount.");
        } catch (Exception e) {
            System.out.println("Transfer failed: " + e.getMessage());
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

            List<Transaction> transactions = clientController.filterTransactionsByType(type);
            System.out.println("\n--- " + type + " TRANSACTIONS ---");
            displayTransactions(transactions);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void filterTransactionsByAmount() {
        System.out.println("\n--- FILTER BY AMOUNT RANGE ---");
        try {
            System.out.print("Minimum amount: ");
            double minAmount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Maximum amount: ");
            double maxAmount = Double.parseDouble(scanner.nextLine().trim());

            if (minAmount > maxAmount) {
                System.out.println("Minimum amount cannot be greater than maximum amount.");
                return;
            }

            List<Transaction> transactions = clientController.filterTransactionsByAmount(minAmount, maxAmount);
            System.out.println("\n--- TRANSACTIONS BETWEEN " + minAmount + " AND " + maxAmount + " ---");
            displayTransactions(transactions);
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numbers.");
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

            if (startDate.isAfter(endDate)) {
                System.out.println("Start date cannot be after end date.");
                return;
            }

            List<Transaction> transactions = clientController.filterTransactionsByDate(startDate, endDate);
            System.out.println("\n--- TRANSACTIONS FROM " + startDateStr + " TO " + endDateStr + " ---");
            displayTransactions(transactions);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd HH:mm");
        }
    }

    private void sortTransactionsByAmount() {
        System.out.println("\n--- SORT BY AMOUNT ---");
        System.out.println("1. Ascending (Low to High)");
        System.out.println("2. Descending (High to Low)");
        System.out.print("Choose sorting order: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            boolean ascending = choice == 1;

            List<Transaction> transactions = clientController.sortTransactionsByAmount(ascending);
            System.out.println("\n--- TRANSACTIONS SORTED BY AMOUNT " + (ascending ? "(ASC)" : "(DESC)") + " ---");
            displayTransactions(transactions);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void sortTransactionsByDate() {
        System.out.println("\n--- SORT BY DATE ---");
        System.out.println("1. Ascending (Oldest First)");
        System.out.println("2. Descending (Newest First)");
        System.out.print("Choose sorting order: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            boolean ascending = choice == 1;

            List<Transaction> transactions = clientController.sortTransactionsByDate(ascending);
            System.out.println("\n--- TRANSACTIONS SORTED BY DATE " + (ascending ? "(ASC)" : "(DESC)") + " ---");
            displayTransactions(transactions);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void calculateTotalBalance() {
        System.out.println("\n--- TOTAL BALANCE ---");
        double totalBalance = clientController.calculateTotalBalance();
        System.out.printf("Your total balance across all accounts: %.2f MAD%n", totalBalance);
    }

    private void viewBalanceSummary() {
        System.out.println("\n--- BALANCE SUMMARY ---");

        double totalBalance = clientController.calculateTotalBalance();
        double totalDeposits = clientController.calculateTotalDeposits();
        double totalWithdrawals = clientController.calculateTotalWithdrawals();
        double totalTransfers = clientController.calculateTotalTransfers();

        System.out.printf("Total Balance: %.2f MAD%n", totalBalance);
        System.out.printf("Total Deposits: %.2f MAD%n", totalDeposits);
        System.out.printf("Total Withdrawals: %.2f MAD%n", totalWithdrawals);
        System.out.printf("Total Transfers: %.2f MAD%n", totalTransfers);
        System.out.println(String.join("", Collections.nCopies(40, "-")));
        System.out.printf("Net Activity: %.2f MAD%n", totalDeposits - totalWithdrawals - totalTransfers);
    }

    private void updatePersonalInformation() {
        System.out.println("\n--- UPDATE PERSONAL INFORMATION ---");
        System.out.println("Leave fields empty to keep current values");

        System.out.print("First Name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Last Name: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        try {
            clientController.updatePersonalInformation(
                firstName.isEmpty() ? null : firstName,
                lastName.isEmpty() ? null : lastName,
                email.isEmpty() ? null : email
            );
            System.out.println("Personal information updated successfully!");
        } catch (Exception e) {
            System.out.println("Update failed: " + e.getMessage());
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

    private void waitForEnter() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
