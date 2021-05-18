package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.Predicates.ProductPredicate;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.Map;

public class ConditionalProductDiscountRule extends ProductDiscountRule {
    private ProductPredicate productPredicate;

    public ConditionalProductDiscountRule(int productID, double discount, ProductPredicate productPredicate) {
        super(productID, discount);
        this.productPredicate = productPredicate;
    }

    @Override
    public double calcDiscount(Map<ProductClientDTO, Integer> shoppingBasket) {
        for(Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet()) {
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
