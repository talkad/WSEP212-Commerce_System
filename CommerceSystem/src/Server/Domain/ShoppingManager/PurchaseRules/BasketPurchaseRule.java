package Server.Domain.ShoppingManager.PurchaseRules;

import Server.Domain.ShoppingManager.Predicates.BasketPredicate;
import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Map;

public class BasketPurchaseRule extends LeafPurchaseRule {

    public BasketPurchaseRule(int id, BasketPredicate predicate) {
        super(id, predicate);
    }

    @Override
    public boolean isValidPurchase(Map<ProductDTO, Integer> shoppingBasket) {
        return predicate.isValid(shoppingBasket);
    }

    @Override
    public String getDescription() {
        return "Basket Purchase Rule " + id;
    }
}
