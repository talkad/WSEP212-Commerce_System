package Server.Domain.ShoppingManager.PurchaseRules;

import Server.Domain.ShoppingManager.Predicates.BasketPredicate;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.Map;

public class BasketPurchaseRule extends LeafPurchaseRule {

    public BasketPurchaseRule(BasketPredicate predicate) {
        super(predicate);
    }

    @Override
    public boolean isValidPurchase(Map<ProductClientDTO, Integer> shoppingBasket) {
        return predicate.isValid(shoppingBasket);
    }

    @Override
    public String getDescription() {
        return "Basket Purchase Rule " + id;
    }
}
