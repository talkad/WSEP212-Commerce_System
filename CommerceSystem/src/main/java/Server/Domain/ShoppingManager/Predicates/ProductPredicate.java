package Server.Domain.ShoppingManager.Predicates;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.Map;

public class ProductPredicate implements Predicate {
    int productID;
    int minUnits;
    int maxUnits;

    public ProductPredicate(int productID, int minUnits, int maxUnits){
        this.productID = productID;
        this.minUnits = minUnits;
        this.maxUnits = maxUnits;
    }
    @Override
    public boolean isValid(Map<ProductClientDTO, Integer> shoppingBasket) {
        for(Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet())
            if(entry.getKey().getProductID() == productID)
                return (entry.getValue() >= minUnits && entry.getValue() <= maxUnits);
       return true;
    }
}
