package models;

public class Customer {
    private String id;
    private String name;
    private String email;
    private double balance;

    public Customer(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.balance = 0.0; 
    }

    public double getBalance() {
        return balance;
    }

    public void addFunds(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    public void deductAmount(double amount) {
        if (this.hasSufficientBalance(amount)) {
            balance -= amount;
        }
    }
    public boolean hasSufficientBalance(double amount) {
        return amount > 0 && amount <= balance;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
