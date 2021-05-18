package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.List;
import java.util.Map;

public class SumCompositionDiscountRule extends CompoundDiscountRule {

    public SumCompositionDiscountRule(List<DiscountRule> policyRules){
        super(0, policyRules);
    }

    @Override
    public double calcDiscount(Map<ProductClientDTO, Integer> shoppingBasket) {
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
