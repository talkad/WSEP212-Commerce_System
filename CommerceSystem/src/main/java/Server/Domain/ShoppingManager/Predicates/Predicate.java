package Server.Domain.ShoppingManager.Predicates;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.Map;

public interface Predicate {
    boolean isValid(Map<ProductClientDTO, Integer> shoppingBasket);
}
