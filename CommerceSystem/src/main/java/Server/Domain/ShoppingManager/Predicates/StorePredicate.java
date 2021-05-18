package Server.Domain.ShoppingManager.Predicates;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.Map;

public class StorePredicate implements Predicate {
    private int minUnits;
    private int maxUnits;
    private double minPrice;

    public StorePredicate(int minUnits, int maxUnits, double minPrice){
        this.minUnits = minUnits;
        this.maxUnits = maxUnits;
        this.minPrice = minPrice;
    }

    @Override
    public boolean isValid(Map<ProductClientDTO, Integer> shoppingBasket) {
        int numOfProducts = 0;
        double totalPrice = 0.0;

        for (Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet()) {
            numOfProducts += entry.getValue();
            totalPrice += entry.getKey().getPrice() * entry.getValue();
        }

        return (numOfProducts >= minUnits && numOfProducts <= maxUnits) && totalPrice >= minPrice;
    }
}
