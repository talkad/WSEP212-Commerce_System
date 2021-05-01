package Server.Domain.ShoppingManager.Predicates;

import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Map;

public interface Predicate {
    boolean isValid(Map<ProductDTO, Integer> shoppingBasket);
}
