package Server.Domain.ShoppingManager.PurchaseRules;

import Server.Domain.ShoppingManager.Predicates.CategoryPredicate;
import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Map;

public class CategoryPurchaseRule extends LeafPurchaseRule {

    public CategoryPurchaseRule(int id, CategoryPredicate predicate) {
        super(id, predicate);
    }

    @Override
    public boolean isValidPurchase(Map<ProductDTO, Integer> shoppingBasket) {
        return predicate.isValid(shoppingBasket);
    }

    @Override
    public String getDescription() {
        return "Product Purchase Rule " + id;
    }
}
