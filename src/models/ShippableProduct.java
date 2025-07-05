package models;

import java.time.LocalDate;

public class ShippableProduct extends Product implements Shippable {
    private double weight; // in grams or kilograms


    public ShippableProduct(String name, double price, double quantity,
                            boolean canExpire, LocalDate expirationDate,
                            double weight) throws IllegalArgumentException {
        super(name, price, quantity, canExpire, expirationDate);
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive for shippable products.");
        }
        this.weight = weight;
    }

    public ShippableProduct(String name, double price, double quantity, double weight) {
        this(name, price, quantity, false, null, weight);
    }

    @Override
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive.");
        }
        this.weight = weight;
    }

    @Override
    public String toString() {
        return super.toString() + " (Shippable, weight=" + weight + ")";
    }
}
