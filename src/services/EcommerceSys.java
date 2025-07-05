package services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Cart;
import models.Customer;
import models.Order;
import models.Product;
import models.Shippable;

public class EcommerceSys {

    private final CartManager cartManager;
    private final ShippingService shippingManager;

    public EcommerceSys(CartManager cartManager, ShippingService shippingManager) {
        this.cartManager = cartManager;
        this.shippingManager = shippingManager;
    }

    public void checkout(Customer customer) {
      
        if (customer == null) {
            System.out.println("Customer cannot be null. Please Log in");
            return;
        }

    
        System.out.println("======================================================");
        System.out.printf("<<< check out Customer %s, id: %s >>>%n%n", customer.getName(), customer.getId());


        Cart cart = cartManager.getCart(customer);
      
        if (cart.getProducts().isEmpty()) {
            System.out.println("Your cart is empty. Please add products before checking out.");
            return;
        }

        List<Shippable> shippableItems = new ArrayList<>();
        double subtotal = 0.0;

       
        for (Map.Entry<Product, Integer> entry : cart.getProducts().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();

            // Check stock
            if (quantity > product.getQuantity()) {
                System.out.printf("Not enough stock for %s. Available: %d, Requested: %d%n",
                        product.getName(), (int) product.getQuantity(), quantity);
                return;
            }

            if (product.canExpire()) {
                try {
                    if (product.isExpired()) {
                        System.out.printf("%s has expired and cannot be purchased.%n", product.getName());
                        return;
                    }
                } catch (UnsupportedOperationException e) {
                    // Product doesn't expire
                }
            }

            
            subtotal += product.getPrice() * quantity;

            // Collect shippable items
            if (product instanceof Shippable) {
                for (int i = 0; i < quantity; i++) {
                    shippableItems.add((Shippable) product);
                }
            }
        }


        double shippingCost = shippingManager.ship(shippableItems);
        double totalAmount = subtotal + shippingCost;

        
        if (customer.getBalance() < totalAmount) {
            System.out.printf("Insufficient balance. Total: %.2f (Subtotal: %.2f + Shipping: %.2f), Your Balance: %.2f%n",
                    totalAmount, subtotal, shippingCost, customer.getBalance());
            return;
        }

        
        customer.deductAmount(totalAmount);

        
        for (Map.Entry<Product, Integer> entry : cart.getProducts().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            product.reduceQuantity(quantity);
        }

         Order order = MakeOrder(customer, cart, subtotal, shippingCost);

    
  
  
        System.out.println(order);

        System.out.printf("Remaining Balance:  %.2f%n", customer.getBalance());
        cartManager.clearCart(customer);

        System.out.println("Thank you for your purchase!");
        System.out.println("======================================================\n\n");
    }

    private Order MakeOrder(Customer customer, Cart cart,
                                 double subtotal, double shippingCost) {
        double totalAmount = subtotal + shippingCost;
        return new Order(customer, cart.getProducts(), subtotal, shippingCost, totalAmount);
    }


}
