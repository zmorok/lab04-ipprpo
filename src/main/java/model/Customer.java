package model;

public class Customer {
    public int id;
    public String surname;
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public double cash;

    public Customer(int id, String surname, String firstName, String lastName, String phoneNumber, double cash) {
        this.id = id;
        this.surname = surname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.cash = cash;
    }
}
