package utils;


import models.Account;
import models.Client;
import models.Manager;
import models.Transaction;
import models.enums.AccountType;
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
            Account account1 = managerService.createAccountForClient(client1.getClientId(), AccountType.CURRENT, 100000.0);
            Account account2 = managerService.createAccountForClient(client1.getClientId(), AccountType.SAVINGS, 1000000.0);
            Account account3 = managerService.createAccountForClient(client1.getClientId(), AccountType.TERM_DEPOSIT, 100000.0);
            Account account4 = managerService.createAccountForClient(client2.getClientId(), AccountType.CURRENT, 100.0);
            Account account5 = managerService.createAccountForClient(client2.getClientId(), AccountType.SAVINGS, 100.0);
            Account account6 = managerService.createAccountForClient(client2.getClientId(), AccountType.TERM_DEPOSIT, 100.0);
            Transaction transfer = transactionService.makeTransfer(account1.getAccountId(), account2.getAccountId(), 10000, "Transfer money");
            Transaction deposit = transactionService.MakeDeposit(account1.getAccountId(), 5000000, "Deposit an amount");
            Transaction withdrawal = transactionService.MakeWithdrawal(account1.getAccountId(), 5000, "withdrawal money money");
            Transaction transfer1 = transactionService.makeTransfer(account2.getAccountId(), account2.getAccountId(), 10000, "Transfer money");
            Transaction deposit2 = transactionService.MakeDeposit(account2.getAccountId(), 5000000, "Deposit an amount");
            Transaction withdrawal3 = transactionService.MakeWithdrawal(account2.getAccountId(), 5000, "withdrawal money money");

        }catch (Exception e) {
            System.out.println("Failed To create Fake data");
        }
    }
}