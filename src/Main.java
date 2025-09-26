
import controllers.AuthController;
import controllers.ClientController;
import controllers.ManagerController;
import controllers.TransactionController;
import services.*;
import utils.DataInitializer;
import view.MainMenu;
import view.ClientMenu;
import view.ManagerMenu;

public class Main {
    public static void main(String[] args) {
        try {
            ClientService clientService = new ClientService();
            AccountService accountService = new AccountService(clientService);
            TransactionService transactionService = new TransactionService(accountService);
            ManagerService managerService = new ManagerService(clientService, accountService, transactionService);
            AuthService authService = new AuthService(clientService, managerService);


            AuthController authController = new AuthController(authService);
            ClientController clientController = new ClientController(clientService, authService);
            ManagerController managerController = new ManagerController(managerService, clientService, accountService, authService);
            TransactionController transactionController = new TransactionController(accountService, authService);

            ClientMenu clientMenu = new ClientMenu(clientController, transactionController, authController);
            ManagerMenu managerMenu = new ManagerMenu(managerController, authController);
            MainMenu mainMenu = new MainMenu(authController, clientMenu, managerMenu);

            System.out.println("Initializing Bank Management System...");

            DataInitializer.initializeFakeData(managerService, clientService, accountService, transactionService);


            mainMenu.displayMainMenu();

        } catch (Exception e) {
            System.out.println("Error starting the application: " + e.getMessage());
        }
    }
}

