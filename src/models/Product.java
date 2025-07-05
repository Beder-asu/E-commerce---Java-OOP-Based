
package models;

import java.time.LocalDate;

public abstract class Product{
    private static int counter = 0;
    private final int id;
    private String name;
    private double price;
    private double quantity;
    private final boolean canExpire;
    private final LocalDate expirationDate;

    // Constructors
     public Product(String name, double price, double quantity, boolean canExpire, LocalDate expirationDate) 
                                                                            throws IllegalArgumentException {
        this.id = ++counter;
        this.name = name;
        this.price = price;
          if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }else{
        this.quantity = quantity;
        }
        this.canExpire = canExpire;
        this.expirationDate = expirationDate;
        if (canExpire && expirationDate == null) {
            throw new IllegalArgumentException("Expiration date must be provided for products that can expire.");
        }
        if (!canExpire && expirationDate != null) {
            throw new IllegalArgumentException("Expiration date should not be provided for products that do not expire.");
        }
    }

    public Product(String name, double price, double quantity) {
      this(name, price, quantity, false, null);
    }

    //Logic Helpers

    public boolean canExpire() {
        return canExpire;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }
    public boolean isExpired() throws UnsupportedOperationException {
        if (!canExpire){
            throw new UnsupportedOperationException("This product does not expire.");
        }
        return expirationDate != null && LocalDate.now().isAfter(expirationDate);
    }

    public void reduceQuantity(double amount) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to reduce cannot be negative.");
        }
        if (amount > quantity) {
            throw new IllegalArgumentException("Cannot reduce more than available quantity.");
        }
        this.quantity -= amount;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
    
    public double getQuantity() {
        return quantity;
    }
    

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }  this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}