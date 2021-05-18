package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.Predicates.StorePredicate;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.Map;

public class ConditionalStoreDiscountRule extends StoreDiscountRule {
    private int productID;
    private StorePredicate storePredicate;

    public ConditionalStoreDiscountRule(double discount, StorePredicate storePredicate) {
        super(discount);
        this.storePredicate = storePredicate;
        this.productID = -1;
    }

    public ConditionalStoreDiscountRule(double discount, StorePredicate storePredicate, int productID) {
        super(discount);
        this.storePredicate = storePredicate;
        this.productID = productID;
    }

    @Override
    public double calcDiscount(Map<ProductClientDTO, Integer> shoppingBasket) {
        if(storePredicate.isValid(shoppingBasket)) {
            double totalPrice = 0.0;
            if(productID != -1) {
                for (Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet())
                    totalPrice += entry.getKey().getPrice() * entry.getValue();
            }
            else{
                for (Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet())
                    if(entry.getKey().getProductID() == productID) {
                        totalPrice += entry.getKey().getPrice() * entry.getValue();
                        break;
                    }
            }
            return totalPrice * (this.discount / 100);
        }
        else
            return 0.0;
    }

    @Override
    public String getDescription() {
        return "Conditional store discount: " + this.discount + "% discount on the entire store.";
    }
}
