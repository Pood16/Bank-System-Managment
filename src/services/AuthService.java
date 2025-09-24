package services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import helpers.Utils;
import models.Client;
import models.Manager;
import models.Person;
import models.enums.DepartmentType;
import models.enums.Role;

public class AuthService {

    private final List<Person> users;

    public AuthService() {
        this.users = new ArrayList<>();
    }

    // authenticate the user
    public Optional<Person> login(String email, String password) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email) && user.getPassword().equals(password))
                .findFirst();
    }

    // Register client
    public Client registerClient(String firstName, String lastName, String email, String password) {
        if (Utils.isEmailInUse(users, email)) {
            throw new IllegalArgumentException("Email is already in use");
        }
        Client client = new Client(firstName, lastName, email, password, Role.CLIENT);
        users.add(client);
        return client;
    }

    // Register manager
    public Manager registerManager(String firstName, String lastName, String email, String password, DepartmentType departmentType) {
        if (Utils.isEmailInUse(users, email)) {
            throw new IllegalArgumentException("Email is already in use");
        }
        Manager manager = new Manager(firstName, lastName, email, password, Role.MANAGER, departmentType);
        users.add(manager);
        return manager;
    }

    public List<Person> getAllUsers() {
        return new ArrayList<>(users);
    }

    public Optional<Person> findUserByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public Optional<Person> findUserById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public boolean removeUser(Person user) {
        return users.remove(user);
    }

    public void updatePassword(Person user, String newPassword) {
        user.setPassword(newPassword);
    }

    public void updateEmail(Person user, String email) {
        user.setEmail(email);
    }
}
