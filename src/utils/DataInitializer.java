package utils;


import models.Client;
import models.Manager;
import models.enums.DepartmentType;
import services.AccountService;
import services.ClientService;
import services.ManagerService;
import services.TransactionService;

public class DataInitializer {



    public static void initializeFakeData(ManagerService managerService, ClientService clientService, AccountService accountService, TransactionService transactionService) {
        try{
            Manager manager1 = managerService.createManager("manager1", "manager1", "manager1@gmail.com", "password123", DepartmentType.IT);
            Manager manager2 = managerService.createManager("manager2", "manager2", "manager2@gmail.com", "password123", DepartmentType.COMPLIANCE);
            Client client1 = clientService.createClient(manager1, "client1", "client1", "client1@gmail.com", "password123");
            Client client2 = clientService.createClient(manager1, "client2", "client2", "client2@gmail.com", "password123");
            Client client3 = clientService.createClient(manager2, "client3", "client3", "client3@gmail.com", "password123");
            Client client4 = clientService.createClient(manager2, "client4", "client4", "client4@gmail.com", "password123");
        }catch (Exception e) {
            System.out.println("Failed To create Fake data");
        }
    }
}