package models;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private static int counter = 0;
    private final int id;
    private Map<Product,Integer> products;

    public Cart() {
        this.products = new HashMap<>();
        this.id = ++counter;
    }

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, int quantity) {
        products.put(product, products.getOrDefault(product, 0) + quantity);
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }

    public Map<Product, Integer> getProducts() {
        return Map.copyOf(products);
    }
    public int getId() {
        return id;
    }
    public double getTotalPrice() {
        double total = 0;
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    public void clear() {
        products.clear();
    }
}
