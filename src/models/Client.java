package models;

import models.enums.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Client extends Person {
    private String clientId;
    private List<Account> accounts;

    public Client(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password, Role.CLIENT);
        this.clientId = UUID.randomUUID().toString();
        this.accounts = new ArrayList<>();
    }


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    public void removeAccount(Account account) {
        this.accounts.remove(account);
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientId='" + clientId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", accountsCount=" + accounts.size() +
                '}';
    }
}
