package test;

import java.time.LocalDate;
import models.*;
import services.*;

public class TestSuite {

    private final CartManager cartManager = new CartManager();
    private final ShippingManager shippingManager = new ShippingManager(10);
    private final EcommerceSys ecommerceSys = new EcommerceSys(cartManager, shippingManager);

    private int passed = 0;
    private int total = 0;

    // ANSI escape codes for colors
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";

    public static void main(String[] args) {
        TestSuite suite = new TestSuite();
        suite.runAllTests();
        suite.printSummary();
    }

    public void runAllTests() {
        System.out.println("\n==== E-COMMERCE SYSTEM PRESENTATION TESTS ====");
        runTest("Happy Path", this::testHappyPath);
        runTest("Expired Product Edge Case", this::testExpiredProduct);
        runTest("Out of Stock Edge Case", this::testOutOfStock);
        runTest("Insufficient Balance Edge Case", this::testInsufficientBalance);
        runTest("Stock Exhaustion Race Condition", this::testStockExhaustionRace);
    }

    private void runTest(String name, Runnable testMethod) {
        System.out.println("\n======================================================");
        System.out.printf("TEST: %s%n", name);
        System.out.println("======================================================\n");

        try {
            testMethod.run();
            System.out.printf("\nPASS: %s%n", name);
            passed++;
        } catch (AssertionError ae) {
            System.out.printf("\nFAIL: %s%n", name);
            System.out.println(ae.getMessage());
        } catch (Exception e) {
            System.out.printf("\nERROR: %s%n", name);
            e.printStackTrace();
        } finally {
            total++;
        }
    }

    private void printSummary() {
        System.out.println("\n==== TEST SUMMARY ====");
        System.out.printf("%d PASSED%n", passed);
        System.out.printf("%d FAILED%n", total - passed);
        System.out.printf("TOTAL: %d%n", total);
    }

    private void printCodeLine(String line) {
        System.out.println(CYAN + ">> " + line + RESET);
    }

    // --- Test Methods ---

    private void testHappyPath() {
        System.out.println("CODE:");
        printCodeLine("Customer alice = new Customer(\"C1\", \"Alice\", \"alice@example.com\");");
        printCodeLine("alice.addFunds(1000);");
        printCodeLine("Product tv = new ShippableProduct(\"Smart TV\", 300, 2, 10.0);");
        printCodeLine("Product cheese = new ShippableProduct(\"Cheddar Cheese\", 20, 5, true, LocalDate.now().plusDays(5), 0.5);");
        printCodeLine("Product ebook = new NonShippableProduct(\"E-Book\", 15, 100);");
        printCodeLine("cartManager.addProductToCart(alice, tv, 1);");
        printCodeLine("cartManager.addProductToCart(alice, cheese, 2);");
        printCodeLine("cartManager.addProductToCart(alice, ebook, 1);");
        printCodeLine("ecommerceSys.checkout(alice);");

        // Setup
        Customer alice = new Customer("C1", "Alice", "alice@example.com");
        alice.addFunds(1000);
        Product tv = new ShippableProduct("Smart TV", 300, 2, 10.0);
        Product cheese = new ShippableProduct("Cheddar Cheese", 20, 5, true, LocalDate.now().plusDays(5), 0.5);
        Product ebook = new NonShippableProduct("E-Book", 15, 100);

        cartManager.addProductToCart(alice, tv, 1);
        cartManager.addProductToCart(alice, cheese, 2);
        cartManager.addProductToCart(alice, ebook, 1);

        // Run
        ecommerceSys.checkout(alice);

        // Assertions
        assert alice.getBalance() < 1000 : "Balance should be deducted.";
        assert tv.getQuantity() == 1 : "TV stock should be reduced.";
    }

    private void testExpiredProduct() {
        System.out.println("CODE:");
        printCodeLine("Customer carol = new Customer(\"C3\", \"Carol\", \"carol@example.com\");");
        printCodeLine("carol.addFunds(100);");
        printCodeLine("Product milk = new ShippableProduct(\"Milk\", 5, 10, true, LocalDate.now().minusDays(1), 1.0);");
        printCodeLine("cartManager.addProductToCart(carol, milk, 2);");
        printCodeLine("ecommerceSys.checkout(carol);");

        Customer carol = new Customer("C3", "Carol", "carol@example.com");
        carol.addFunds(100);
        Product milk = new ShippableProduct("Milk", 5, 10, true, LocalDate.now().minusDays(1), 1.0);

        cartManager.addProductToCart(carol, milk, 2);
        ecommerceSys.checkout(carol);

        assert milk.getQuantity() == 10 : "Expired product stock should not change.";
        assert carol.getBalance() == 100 : "Balance should not change.";
    }

    private void testOutOfStock() {
        System.out.println("CODE:");
        printCodeLine("Customer dave = new Customer(\"C4\", \"Dave\", \"dave@example.com\");");
        printCodeLine("dave.addFunds(500);");
        printCodeLine("Product phone = new ShippableProduct(\"Smartphone\", 400, 1, 0.4);");
        printCodeLine("cartManager.addProductToCart(dave, phone, 2);");
        printCodeLine("ecommerceSys.checkout(dave);");

        Customer dave = new Customer("C4", "Dave", "dave@example.com");
        dave.addFunds(500);
        Product phone = new ShippableProduct("Smartphone", 400, 1, 0.4);

        cartManager.addProductToCart(dave, phone, 2);
        ecommerceSys.checkout(dave);

        assert phone.getQuantity() == 1 : "Stock should remain unchanged.";
    }

    private void testInsufficientBalance() {
        System.out.println("CODE:");
        printCodeLine("Customer eve = new Customer(\"C5\", \"Eve\", \"eve@example.com\");");
        printCodeLine("eve.addFunds(50);");
        printCodeLine("Product laptop = new ShippableProduct(\"Laptop\", 1000, 1, 2.5);");
        printCodeLine("cartManager.addProductToCart(eve, laptop, 1);");
        printCodeLine("ecommerceSys.checkout(eve);");

        Customer eve = new Customer("C5", "Eve", "eve@example.com");
        eve.addFunds(50);
        Product laptop = new ShippableProduct("Laptop", 1000, 1, 2.5);

        cartManager.addProductToCart(eve, laptop, 1);
        ecommerceSys.checkout(eve);

        assert eve.getBalance() == 50 : "Balance should remain unchanged.";
        assert laptop.getQuantity() == 1 : "Stock should remain unchanged.";
    }

    private void testStockExhaustionRace() {
        System.out.println("CODE:");
        printCodeLine("Customer a = new Customer(\"C11\", \"A\", \"a@example.com\");");
        printCodeLine("Customer b = new Customer(\"C12\", \"B\", \"b@example.com\");");
        printCodeLine("a.addFunds(1000); b.addFunds(1000);");
        printCodeLine("Product rareItem = new ShippableProduct(\"Rare Item\", 100, 1, 1.0);");
        printCodeLine("cartManager.addProductToCart(a, rareItem, 1);");
        printCodeLine("cartManager.addProductToCart(b, rareItem, 1);");
        printCodeLine("ecommerceSys.checkout(a);");
        printCodeLine("ecommerceSys.checkout(b);");

        Customer a = new Customer("C11", "A", "a@example.com");
        Customer b = new Customer("C12", "B", "b@example.com");
        a.addFunds(1000);
        b.addFunds(1000);

        Product rareItem = new ShippableProduct("Rare Item", 100, 1, 1.0);
        cartManager.addProductToCart(a, rareItem, 1);
        cartManager.addProductToCart(b, rareItem, 1);

        ecommerceSys.checkout(a);
        ecommerceSys.checkout(b);

        assert rareItem.getQuantity() == 0 : "Stock should be zero after first purchase.";
    }
}
