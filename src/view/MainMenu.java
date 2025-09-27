package view;

import controllers.AuthController;

import models.Person;
import models.enums.Role;
import java.util.Optional;
import java.util.Scanner;
import java.util.Collections;

public class MainMenu {
    private final AuthController authController;
    private final ClientMenu clientMenu;
    private final ManagerMenu managerMenu;
    private final Scanner scanner;

    public MainMenu(AuthController authController, ClientMenu clientMenu, ManagerMenu managerMenu) {
        this.authController = authController;
        this.clientMenu = clientMenu;
        this.managerMenu = managerMenu;
        this.scanner = new Scanner(System.in);
    }



    public void displayMainMenu() {
        boolean running = true;
        while (running) {
            if (!authController.isLoggedIn()) {
                running = showLoginMenu();
            } else {
                if (authController.isClient()) {
                    clientMenu.displayClientMenu();
                } else if (authController.isManager()) {
                    managerMenu.displayManagerMenu();
                }
            }
        }
        System.out.println("Thank you for using Moroccan Bank Management System!");
        scanner.close();
    }

    private boolean showLoginMenu() {
        String separator = String.join("", Collections.nCopies(50, "="));
        System.out.println("\n" + separator);
        System.out.println("    MOROCCAN BANK MANAGEMENT SYSTEM");
        System.out.println(separator);
        System.out.println("1. Login as Client");
        System.out.println("2. Login as Manager");
        System.out.println("3. Exit");
        System.out.println(separator);
        System.out.print("Choose an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1:
                    return handleLogin(Role.CLIENT);
                case 2:
                    return handleLogin(Role.MANAGER);
                case 3:
                    return false;
                default:
                    System.out.println("Invalid option. Please try again.");
                    return true;
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return true;
        }
    }

    private boolean handleLogin(Role role) {
        System.out.println("\n----------------------- LOGIN ----------------------- ");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("Email and password cannot be empty!");
            return true;
        }

        try {
            Optional<Person> user = authController.login(email, password, role);
            if (user.isPresent()) {
                System.out.println("Login successful! Welcome, " + user.get().getFullName());
                System.out.println("------------------------------------------\n");
                return true;
            } else {
                System.out.println("Invalid credentials. Please try again.");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
            return true;
        }
    }


}
