package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.ProductDTO;

import java.util.List;
import java.util.Map;

public class OrCompositionDiscountRule extends CompoundDiscountRule {
    private String category;

    public OrCompositionDiscountRule(int ruleID, String category, double discount, List<DiscountRule> policyRules) {
        super(ruleID, discount, policyRules);
        this.category = category;
    }

    @Override
    public double calcDiscount(Map<ProductDTO, Integer> shoppingBasket) {
        double totalPriceToDiscount = 0.0;
        boolean eligibleForDiscount = false;
        for(DiscountRule discountRule : discountRules)
            if(discountRule.calcDiscount(shoppingBasket) < 0) {
                eligibleForDiscount = true;
                break;
            }

        if(eligibleForDiscount){
            for(Map.Entry<ProductDTO, Integer> entry : shoppingBasket.entrySet())
                if(entry.getKey().getCategories().contains(category))
                    totalPriceToDiscount += entry.getKey().getPrice() * entry.getValue();
        }

        return totalPriceToDiscount * (discount / 100);
    }

    @Override
    public String getDescription() {
        return "Or Composition: "+ id;
    }
}
