package models;

import models.enums.DepartmentType;
import models.enums.Role;

import java.util.ArrayList;
import java.util.List;

public class Manager extends Person {

    private DepartmentType type;
    private final List<Client> clients;


    public Manager(String firstName, String lastName, String email, String password, Role role, DepartmentType type) {
        super(firstName, lastName, email, password, role);
        this.type = type;
        this.clients = new ArrayList<>();
    }

    public DepartmentType getDepartmentType() {
        return type;
    }

    public void setDepartmentType(DepartmentType type) {
        this.type = type;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(Client client){
        this.clients.add(client);
    }

    @Override
    public String toString() {
        return "Manager{" +
                "type=" + type +
                ", clients=" + clients +
                ", id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
