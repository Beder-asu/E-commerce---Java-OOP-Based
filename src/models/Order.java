package models;

import java.time.LocalDateTime;
import java.util.Map;

public class Order {
    private static int counter = 0;
    private final int id;
    private final Customer customer;
    private final Map<Product, Integer> items; // Product -> Quantity
    private final double subtotal;
    private final double shippingCost;
    private final double totalAmount;
    private final LocalDateTime createdAt;

    public Order(Customer customer, Map<Product, Integer> items,
                 double subtotal, double shippingCost, double totalAmount) {
        this.id = ++counter;
        this.customer = customer;
        this.items = Map.copyOf(items); 
        this.subtotal = subtotal;
        this.shippingCost = shippingCost;
        this.totalAmount = totalAmount;
        this.createdAt = LocalDateTime.now();
    }

    public Order(Customer customer, Map<Product, Integer> items,
                 double subtotal, double shippingCost, double totalAmount, LocalDateTime createdAt) {
        this.id = ++counter;
        this.customer = customer;
        this.items = Map.copyOf(items); 
        this.subtotal = subtotal;
        this.shippingCost = shippingCost;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // Getters
    public int getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Map<Product, Integer> getItems() { return items; }
    public double getSubtotal() { return subtotal; }
    public double getShippingCost() { return shippingCost; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void printOrder() {
        System.out.println("** Order #" + id + " **");
        System.out.println("Customer: " + customer.getName());
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            System.out.println(entry.getValue() + "x "
                + entry.getKey().getName() + " - "
                + (entry.getKey().getPrice() * entry.getValue()));
        }
        System.out.println("-----------------------------");
        System.out.printf("Subtotal:      %.2f%n", subtotal);
        System.out.printf("Shipping:      %.2f%n", shippingCost);
        System.out.printf("Total Amount:  %.2f%n", totalAmount);
        System.out.println("Order Placed At: " + createdAt);
        System.out.println("-----------------------------");
    }
}
