package Server.Domain.ShoppingManager.Predicates;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.List;
import java.util.Map;

public class BasketPredicate implements Predicate {
    private int minUnits;
    private int maxUnits;
    private double minPrice;
    private List<Predicate> basketPredicates;

    public BasketPredicate(int minUnits, int maxUnits, double minPrice){
        this.minUnits = minUnits;
        this.maxUnits = maxUnits;
        this.minPrice = minPrice;
        this.basketPredicates = null;
    }

    public BasketPredicate(int minUnits, int maxUnits, double minPrice, List<Predicate> basketPredicates){
        this.minUnits = minUnits;
        this.maxUnits = maxUnits;
        this.minPrice = minPrice;
        this.basketPredicates = basketPredicates;
    }

    @Override
    public boolean isValid(Map<ProductClientDTO, Integer> shoppingBasket) {
        double totalPrice = 0.0;
        int amountOfProducts = 0;

        if (basketPredicates != null) {
            for (Predicate predicate : basketPredicates)
                if (!predicate.isValid(shoppingBasket))
                    return false;
        }

        for (Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet()) {
            totalPrice += entry.getKey().getPrice() * entry.getValue();
            amountOfProducts += entry.getValue();
        }

        return (amountOfProducts >= minUnits && amountOfProducts <= maxUnits) && totalPrice >= minPrice;
    }
}
