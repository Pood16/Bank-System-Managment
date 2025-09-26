package services;

import models.Account;
import models.Client;
import models.enums.AccountType;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class AccountService {
    private final List<Account> accounts;
    private final ClientService clientService;

    public AccountService(ClientService clientService) {
        this.accounts = new ArrayList<>();
        this.clientService = clientService;
    }

    public Account createAccount(AccountType accountType, String clientId, double amount) {
        Client client = clientService.findClientById(clientId);
        Account account = new Account(accountType, client, amount);
        accounts.add(account);
        client.addAccount(account);
        return account;
    }

    public Account updateAccountType(String accountId, AccountType newAccountType) {
        Account account = findAccountById(accountId);
        account.setAccountType(newAccountType);
        return account;
    }

    public void deleteAccount(String accountId) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("The account ID is required");
        }
        Account account = findAccountById(accountId);
        if (account.getBalance() != 0) {
            throw new IllegalStateException("Cannot delete account with balance");
        }
        account.getClient().removeAccount(account);
        accounts.remove(account);
    }

    public Account findAccountById(String accountId) {
        return accounts.stream()
                .filter(account -> account.getAccountId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("NO account found with ID: " + accountId));

    }

    public List<Account> getAccountsByClientID(String clientId) {
        return accounts.stream()
            .filter(account -> account.getClient().getClientId().equals(clientId))
            .collect(Collectors.toList());
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts);
    }

    public double getAccountBalance(String accountId) {
        Account account = findAccountById(accountId);
        return account.getBalance();
    }

    public void updateAccountBalance(String accountId,double newBalance) {
        Account account = findAccountById(accountId);
        account.setBalance(newBalance);
    }

    public List<Account> getAccountsByType(AccountType accountType) {
        return accounts.stream()
                .filter(account -> account.getAccountType().equals(accountType))
                .collect(Collectors.toList());
    }

    public List<Account> getAccountsWithBalanceAbove(double balance) {
        return accounts
                .stream()
                .filter(account -> account.getBalance() > balance)
                .collect(Collectors.toList());
    }

    public List<Account> getAccountsWithBalanceBelow(double balance) {
        return accounts
                .stream()
                .filter(account -> account.getBalance() < balance)
                .collect(Collectors.toList());
    }

    public boolean canWithdraw(Account account, double amount) {
        return account.getBalance() > amount;
    }
}
