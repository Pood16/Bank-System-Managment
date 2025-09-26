package services;

import models.Person;
import models.Client;
import models.Manager;
import models.enums.DepartmentType;
import models.enums.Role;

import java.util.Optional;

public class AuthService {
    private final ClientService clientService;
    private final ManagerService managerService;
    private Person currentUser;

    public AuthService(ClientService clientService, ManagerService managerService) {
        this.clientService = clientService;
        this.managerService = managerService;
        this.currentUser = null;
    }

    public Manager registerManager(String firstName, String lastName, String email, String password, DepartmentType department) {
        Optional<Manager> existingManager = managerService.getAllManagers().stream()
                .filter(manager -> manager.getEmail().equals(email))
                .findFirst();
        if (existingManager.isPresent()) {
            throw new IllegalArgumentException("Manager with this email already exists");
        }
        Manager manager = new Manager(firstName, lastName, email, password, department);
        managerService.addManager(manager);
        return manager;
    }


    public Optional<Person> login(String email, String password, Role role) {
        if (email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            role == null) {
            throw new IllegalArgumentException("Email, password, and role are required");
        }

        Person user = authenticateUser(email, password, role);
        if (user != null) {
            currentUser = user;
            return Optional.of(user);
        }

        return Optional.empty();
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public Optional<Person> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public boolean isClient() {
        return currentUser != null && currentUser.getRole() == Role.CLIENT;
    }

    public boolean isManager() {
        return currentUser != null && currentUser.getRole() == Role.MANAGER;
    }

    public Optional<Client> getCurrentClient() {
        if (isClient()) {
            return Optional.of((Client) currentUser);
        }
        return Optional.empty();
    }

    public Optional<Manager> getCurrentManager() {
        if (isManager()) {
            return Optional.of((Manager) currentUser);
        }
        return Optional.empty();
    }

    private Person authenticateUser(String email, String password, Role role) {
        switch (role) {
            case CLIENT:
                if (clientService.validateClientCredentials(email, password)) {
                    return clientService.findClientByEmail(email).orElse(null);
                }
                break;
            case MANAGER:
                if (managerService.validateManagerCredentials(email, password)) {
                    return managerService.findManagerByEmail(email).orElse(null);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }
        return null;
    }



}
