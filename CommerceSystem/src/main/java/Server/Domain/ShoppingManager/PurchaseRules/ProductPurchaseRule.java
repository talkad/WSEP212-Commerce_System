package Server.Domain.ShoppingManager.PurchaseRules;

import Server.Domain.ShoppingManager.Predicates.ProductPredicate;
import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Map;

public class ProductPurchaseRule extends LeafPurchaseRule {

    public ProductPurchaseRule(ProductPredicate predicate) {
        super(predicate);
    }

    @Override
    public boolean isValidPurchase(Map<ProductDTO, Integer> shoppingBasket) {
        return predicate.isValid(shoppingBasket);
    }

    @Override
    public String getDescription() {
        return "Product Purchase rule: " + id;
    }
}
