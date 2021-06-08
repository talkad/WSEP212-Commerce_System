package Server.Domain.ShoppingManager.Predicates;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.DAL.PredicateDTO;

import java.util.Map;

public interface Predicate {

    boolean isValid(Map<ProductClientDTO, Integer> shoppingBasket);
    PredicateDTO toDTO();
}
