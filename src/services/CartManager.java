package services;

import models.Cart;
import models.Customer;
import models.Product;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class CartManager {
    private final Map<Customer, Cart> customerCartMap;

    public CartManager() {
        this.customerCartMap = new HashMap<>();
    }

 
    public Cart getCart(Customer customer) {
        return customerCartMap.computeIfAbsent(customer, _ -> new Cart());
    }

    
    public void addProductToCart(Customer customer, Product product, int quantity) {
        getCart(customer).addProduct(product, quantity);
    }

   
    public void removeProductFromCart(Customer customer, Product product) {
        getCart(customer).removeProduct(product);
    }

   
    public double getTotalCartPrice(Customer customer) {
        return getCart(customer).getTotalPrice();
    }

   
    public void clearCart(Customer customer) {
        getCart(customer).clear();
    }

 
    public Set<Product> viewCartProducts(Customer customer) {
        return getCart(customer).getProducts().keySet();
    }

   
    public Map<Product, Integer> viewCartDetails(Customer customer) {
        return getCart(customer).getProducts();
    }
}
