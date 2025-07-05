package services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Shippable;

public class ShippingService {

    private final int costPerKg; 

    public ShippingService(int costPerKg) {
        this.costPerKg = costPerKg;
    }

    public double ship(List<Shippable> items) {
        if (items == null || items.isEmpty()) {
            System.out.println("No items to ship.");
            return 0.0;
        }

        Map<String, Double> weightMap = new HashMap<>();
        Map<String, Integer> quantityMap = new HashMap<>();
        double totalWeight = 0.0;

        
        for (Shippable item : items) {
            String key = item.getName() + " | " + item.getWeight() + "kg";

            quantityMap.put(key, quantityMap.getOrDefault(key, 0) + 1);
            weightMap.put(key, item.getWeight());
            totalWeight += item.getWeight();
        }

        
        System.out.println("** Shipment Notice **");
        System.out.println("-----------------------------");
        for (String key : quantityMap.keySet()) {
            int quantity = quantityMap.get(key);
            double unitWeight = weightMap.get(key);
            double groupWeight = unitWeight * quantity;

            System.out.printf("%dx %-20s (%.2fkg each, Total: %.2fkg)%n",
                    quantity,
                    key.split("\\|")[0].trim(),
                    unitWeight,
                    groupWeight);
        }

        double shippingCost = totalWeight * costPerKg;
        System.out.printf("Total package weight: %.2fkg%n", totalWeight);
        System.out.printf("Total shipping cost: %.2f%n", shippingCost);
        System.out.println("-----------------------------\n");

        return shippingCost;
    }
}
