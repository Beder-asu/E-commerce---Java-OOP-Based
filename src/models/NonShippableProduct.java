package models;

import java.time.LocalDate;

public class NonShippableProduct extends Product {

    public NonShippableProduct(String name, double price, double quantity,
                               boolean canExpire, LocalDate expirationDate) throws IllegalArgumentException {
        super(name, price, quantity, canExpire, expirationDate);
    }

    public NonShippableProduct(String name, double price, double quantity) {
        super(name, price, quantity);
    }

    @Override
    public String toString() {
        return super.toString() + " (Non-shippable)";
    }
}
