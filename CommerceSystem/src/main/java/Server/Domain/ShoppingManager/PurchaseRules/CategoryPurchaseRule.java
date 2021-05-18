package Server.Domain.ShoppingManager.PurchaseRules;

import Server.Domain.ShoppingManager.Predicates.CategoryPredicate;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.Map;

public class CategoryPurchaseRule extends LeafPurchaseRule {

    public CategoryPurchaseRule(CategoryPredicate predicate) {
        super(predicate);
    }

    @Override
    public boolean isValidPurchase(Map<ProductClientDTO, Integer> shoppingBasket) {
        return predicate.isValid(shoppingBasket);
    }

    @Override
    public String getDescription() {
        return "Product Purchase Rule " + id;
    }
}
