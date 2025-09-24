package models;

import models.enums.Role;

import java.util.ArrayList;
import java.util.List;

public class Client extends Person {


    private List<Account> accounts;

    public Client(String firstName, String lastName, String email, String password, Role role) {
        super(firstName, lastName, email, password, role);
        this.accounts = new ArrayList<>();
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
    }

    @Override
    public String toString() {
        return "Client{" +
                ", id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                "accounts number =" + accounts.size() +
                '}';
    }

}
