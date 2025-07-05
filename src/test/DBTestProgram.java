import services.DBManager;
import models.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

public class DBTestProgram {
    public static void main(String[] args) {
        DBManager dbManager = new DBManager();
        System.out.println("--- DBManager Test Suite ---");
        testCustomerMethods(dbManager);
        testProductMethods(dbManager);
        testCartMethods(dbManager);
        testOrderMethods(dbManager);
        System.out.println("All tests completed.");
    }

    private static void testCustomerMethods(DBManager dbManager) {
        System.out.println("\nTesting Customer Methods...");
        Customer c = new Customer("C100", "Test User", "test@example.com");
        c.addFunds(100.0);
        dbManager.saveCustomer(c);
        Customer fetched = dbManager.getCustomer("C100");
        assert fetched != null && fetched.getName().equals("Test User");
        dbManager.deleteCustomer("C100");
        assert dbManager.getCustomer("C100") == null;
        System.out.println("Customer methods passed.");
    }

    private static void testProductMethods(DBManager dbManager) {
        System.out.println("\nTesting Product Methods...");
        Product p1 = new ShippableProduct("Laptop", 1500.0, 10, false, null, 2.5);
        Product p2 = new NonShippableProduct("E-Book", 20.0, 100);
        dbManager.saveProduct(p1);
        dbManager.saveProduct(p2);
        Product fetched1 = dbManager.getProduct(p1.getId());
        Product fetched2 = dbManager.getProduct(p2.getId());
        assert fetched1 != null && fetched1.getName().equals("Laptop");
        assert fetched2 != null && fetched2.getName().equals("E-Book");
        dbManager.deleteProduct(p1.getId());
        dbManager.deleteProduct(p2.getId());
        assert dbManager.getProduct(p1.getId()) == null;
        assert dbManager.getProduct(p2.getId()) == null;
        System.out.println("Product methods passed.");
    }

    private static void testCartMethods(DBManager dbManager) {
        System.out.println("\nTesting Cart Methods...");
        Customer c = new Customer("C200", "Cart User", "cart@example.com");
        dbManager.saveCustomer(c);
        int cartId = dbManager.createCartForCustomer("C200");
        Product p = new ShippableProduct("Mouse", 25.0, 50, false, null, 0.2);
        dbManager.saveProduct(p);
        dbManager.addProductToCart(cartId, p.getId(), 3);
        Map<Product, Integer> cartContents = dbManager.getCartContents(cartId);
        assert cartContents.containsKey(p) && cartContents.get(p) == 3;
        dbManager.removeProductFromCart(cartId, p.getId());
        cartContents = dbManager.getCartContents(cartId);
        assert !cartContents.containsKey(p);
        dbManager.clearCart(cartId);
        cartContents = dbManager.getCartContents(cartId);
        assert cartContents.isEmpty();
        dbManager.deleteProduct(p.getId());
        dbManager.deleteCustomer("C200");
        System.out.println("Cart methods passed.");
    }

    private static void testOrderMethods(DBManager dbManager) {
        System.out.println("\nTesting Order Methods...");
        Customer c = new Customer("C300", "Order User", "order@example.com");
        dbManager.saveCustomer(c);
        Product p1 = new ShippableProduct("Phone", 800.0, 5, false, null, 0.3);
        Product p2 = new NonShippableProduct("License", 100.0, 20);
        dbManager.saveProduct(p1);
        dbManager.saveProduct(p2);
        Map<Product, Integer> items = new HashMap<>();
        items.put(p1, 2);
        items.put(p2, 1);
        double subtotal = p1.getPrice() * 2 + p2.getPrice();
        double shipping = ((ShippableProduct)p1).getWeight() * 2 * 5.0;
        double total = subtotal + shipping;
        java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
        Order order = new Order(c, items, subtotal, shipping, total, createdAt);
        dbManager.saveOrder(order);
        // Fetch order by customerId and createdAt, then by id
        Order fetched = dbManager.getOrderByCustomerAndCreatedAt(order.getCustomer().getId(), createdAt);
        assert fetched != null && fetched.getCustomer().getId().equals("C300");
        dbManager.deleteOrder(fetched.getId());
        dbManager.deleteProduct(p1.getId());
        dbManager.deleteProduct(p2.getId());
        dbManager.deleteCustomer("C300");
        System.out.println("Order methods passed.");
    }
}