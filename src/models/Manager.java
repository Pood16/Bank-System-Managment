package models;

import models.enums.DepartmentType;
import models.enums.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Manager extends Person {
    private String managerId;
    private DepartmentType department;
    private List<Client> clientList;

    public Manager(String firstName, String lastName, String email, String password, DepartmentType department) {
        super(firstName, lastName, email, password, Role.MANAGER);
        this.managerId = UUID.randomUUID().toString();
        this.department = department;
        this.clientList = new ArrayList<>();
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public DepartmentType getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentType department) {
        this.department = department;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    public void addClient(Client client) {
        this.clientList.add(client);
    }

    public void removeClient(Client client) {
        this.clientList.remove(client);
    }

    @Override
    public String toString() {
        return "Manager{" +
                "managerId='" + managerId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", department=" + department +
                ", clientCount=" + clientList.size() +
                '}';
    }
}
