package controllers;

import models.Person;
import models.Client;
import models.Manager;
import models.enums.Role;
import services.AuthService;

import java.util.Optional;

public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public Optional<Person> login(String email, String password, Role role) {
        return authService.login(email, password, role);
    }

    public void logout() {
        authService.logout();
    }

    public boolean isLoggedIn() {
        return authService.isLoggedIn();
    }

    public boolean isClient() {
        return authService.isClient();
    }

    public boolean isManager() {
        return authService.isManager();
    }

    public Optional<Client> getCurrentClient() {
        return authService.getCurrentClient();
    }

    public Optional<Manager> getCurrentManager() {
        return authService.getCurrentManager();
    }

}
