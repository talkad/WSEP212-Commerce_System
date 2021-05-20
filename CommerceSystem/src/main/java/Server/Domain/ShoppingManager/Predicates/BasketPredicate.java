package Server.Domain.ShoppingManager.Predicates;

import Server.DAL.PredicateDTOs.BasketPredicateDTO;
import Server.DAL.PredicateDTOs.PredicateDTO;
import Server.Domain.ShoppingManager.ProductDTO;

import java.util.List;
import java.util.Map;
import java.util.Vector;

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

    public BasketPredicate(BasketPredicateDTO basketPredicateDTO){
        this.minUnits = basketPredicateDTO.getMinUnits();
        this.maxUnits = basketPredicateDTO.getMaxUnits();
        this.minPrice = basketPredicateDTO.getMinPrice();

        this.basketPredicates = new Vector<>();
        List<PredicateDTO> predicateDTOS = basketPredicateDTO.getBasketPredicates();
        if(predicateDTOS != null){
            for(PredicateDTO predicateDTO : predicateDTOS){
                this.basketPredicates.add(predicateDTO.toConcretePredicate());
            }
        }
    }

    @Override
    public PredicateDTO toDTO(){
        List<PredicateDTO> predicates = new Vector<>();
        for(Predicate predicate : this.basketPredicates){
            predicates.add(predicate.toDTO());
        }
        return new BasketPredicateDTO(this.minUnits, this.maxUnits, this.minPrice, predicates);
    }

    @Override
    public boolean isValid(Map<ProductDTO, Integer> shoppingBasket) {
        double totalPrice = 0.0;
        int amountOfProducts = 0;

        if (basketPredicates != null) {
            for (Predicate predicate : basketPredicates)
                if (!predicate.isValid(shoppingBasket))
                    return false;
        }

        for (Map.Entry<ProductDTO, Integer> entry : shoppingBasket.entrySet()) {
            totalPrice += entry.getKey().getPrice() * entry.getValue();
            amountOfProducts += entry.getValue();
        }

        return (amountOfProducts >= minUnits && amountOfProducts <= maxUnits) && totalPrice >= minPrice;
    }
}
