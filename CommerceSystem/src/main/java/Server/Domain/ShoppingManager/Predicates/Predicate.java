package Server.Domain.ShoppingManager.Predicates;

import Server.DAL.PredicateDTOs.PredicateDTO;
import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Map;

public interface Predicate {
    boolean isValid(Map<ProductDTO, Integer> shoppingBasket);
    PredicateDTO toDTO();
}
