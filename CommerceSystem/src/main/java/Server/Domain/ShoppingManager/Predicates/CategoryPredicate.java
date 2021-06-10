package Server.Domain.ShoppingManager.Predicates;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.DAL.PredicateDTOs.CategoryPredicateDTO;
import Server.DAL.PredicateDTOs.PredicateDTO;

import java.util.Map;

public class CategoryPredicate implements Predicate {
    private String category;
    private int minUnits;
    private int maxUnits;

    public CategoryPredicate(String category, int minUnits, int maxUnits) {
        this.category = category;
        this.minUnits = minUnits;
        this.maxUnits = maxUnits;
    }

    public CategoryPredicate(CategoryPredicateDTO categoryPredicateDTO){
        this.category = categoryPredicateDTO.getCategory();
        this.minUnits = categoryPredicateDTO.getMinUnits();
        this.maxUnits = categoryPredicateDTO.getMaxUnits();
    }

    public PredicateDTO toDTO(){
        return new CategoryPredicateDTO(this.category, this.minUnits, this.maxUnits);
    }

    @Override
    public boolean isValid(Map<ProductClientDTO, Integer> shoppingBasket) {
        int numOfProducts = 0;
        for (Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet())
            if (entry.getKey().getCategories().contains(category))
                numOfProducts += entry.getValue();

        return numOfProducts >= minUnits && numOfProducts <= maxUnits;
    }

    @Override
    public String toString() {
        return "If you buy at least " + minUnits + "and at most " + maxUnits + "that belong to category " + category +".\n" ;
    }
}
