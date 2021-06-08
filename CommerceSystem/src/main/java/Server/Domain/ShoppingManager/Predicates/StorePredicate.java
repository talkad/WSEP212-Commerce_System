package Server.Domain.ShoppingManager.Predicates;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.DAL.PredicateDTOs.PredicateDTO;
import Server.DAL.PredicateDTOs.StorePredicateDTO;
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

    public StorePredicate(StorePredicateDTO storePredicateDTO){
        this.minUnits = storePredicateDTO.getMinUnits();
        this.maxUnits = storePredicateDTO.getMaxUnits();
        this.minPrice = storePredicateDTO.getMinPrice();
    }

    @Override
    public PredicateDTO toDTO(){
        return new StorePredicateDTO(this.minUnits, this.maxUnits, this.minPrice);
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
