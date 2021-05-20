package Server.Domain.ShoppingManager.Predicates;

import Server.DAL.PredicateDTOs.PredicateDTO;
import Server.DAL.PredicateDTOs.ProductPredicateDTO;
import Server.Domain.ShoppingManager.ProductDTO;

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

    public ProductPredicate(ProductPredicateDTO productPredicateDTO){
        this.productID = productPredicateDTO.getProductID();
        this.minUnits = productPredicateDTO.getMinUnits();
        this.maxUnits = productPredicateDTO.getMaxUnits();
    }

    @Override
    public PredicateDTO toDTO(){
        return new ProductPredicateDTO(this.productID, this.minUnits, this.maxUnits);
    }

    @Override
    public boolean isValid(Map<ProductDTO, Integer> shoppingBasket) {
        for(Map.Entry<ProductDTO, Integer> entry : shoppingBasket.entrySet())
            if(entry.getKey().getProductID() == productID)
                return (entry.getValue() >= minUnits && entry.getValue() <= maxUnits);
       return true;
    }
}
