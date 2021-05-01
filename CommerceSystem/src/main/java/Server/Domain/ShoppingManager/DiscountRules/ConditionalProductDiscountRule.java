package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.Predicates.ProductPredicate;
import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Map;

public class ConditionalProductDiscountRule extends ProductDiscountRule {
    private ProductPredicate productPredicate;

    public ConditionalProductDiscountRule(int ruleID, int productID, double discount, ProductPredicate productPredicate) {
        super(ruleID, productID, discount);
        this.productPredicate = productPredicate;
    }

    @Override
    public double calcDiscount(Map<ProductDTO, Integer> shoppingBasket) {
        for(Map.Entry<ProductDTO, Integer> entry : shoppingBasket.entrySet()) {
            if (entry.getKey().getProductID() == productID) {
                return productPredicate.isValid(shoppingBasket) ? (entry.getValue() * entry.getKey().getPrice()) * (discount / 100) : 0.0;
            }
        }
        return 0.0;
    }

    @Override
    public String getDescription() {
        return "Conditional Product discount: ProductID - " + productID + " with a discount of " + discount + "%";
    }
}
