package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.ProductDTO;

import java.util.List;
import java.util.Map;

public class MaximumCompositionDiscountRule extends CompoundDiscountRule {

    public MaximumCompositionDiscountRule(int ruleID, List<DiscountRule> discountRules){
        super(ruleID, discountRules);
    }

    @Override
    public double calcDiscount(Map<ProductDTO, Integer> shoppingBasket) {
        double discount;
        double maxDiscount = 0.0;

        for (DiscountRule policyRule : discountRules) {
            discount = policyRule.calcDiscount(shoppingBasket);
            if (discount > maxDiscount)
                maxDiscount = discount;
        }
        return maxDiscount;
    }

    @Override
    public String getDescription() {
        return "Maximum Composition: " + id;
    }
}
