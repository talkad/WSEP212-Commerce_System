package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.ProductDTO;

import java.util.List;
import java.util.Map;

public class SumCompositionDiscountRule extends CompoundDiscountRule {

    public SumCompositionDiscountRule(int ruleID, List<DiscountRule> policyRules){
        super(ruleID, 0, policyRules);
    }

    @Override
    public double calcDiscount(Map<ProductDTO, Integer> shoppingBasket) {
        double discount = 0.0;

        for(DiscountRule policyRule : discountRules)
            discount += policyRule.calcDiscount(shoppingBasket);

        return discount;
    }

    @Override
    public String getDescription() {
        return "Sum Composition: " + id;
    }
}
