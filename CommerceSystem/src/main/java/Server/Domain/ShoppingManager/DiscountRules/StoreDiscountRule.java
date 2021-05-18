package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.Map;

public class StoreDiscountRule extends LeafDiscountRule {

    public StoreDiscountRule(double discount){
        super(discount);
    }

    @Override
    public double calcDiscount(Map<ProductClientDTO, Integer> shoppingBasket) {
        double totalPrice = 0.0;
        for(Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet())
            totalPrice += entry.getKey().getPrice() * entry.getValue();
        return totalPrice * (this.discount / 100);
    }

    @Override
    public String getDescription() {
        return "Simple store discount: " + this.discount + "% discount on the entire store.";
    }
}
