package Server.Domain.ShoppingManager.DiscountRules;

import Server.DAL.DiscountRuleDTOs.DiscountRuleDTO;
import Server.DAL.DiscountRuleDTOs.MaximumCompositionDiscountRuleDTO;
import Server.Domain.ShoppingManager.ProductDTO;

import java.util.List;
import java.util.Map;

public class MaximumCompositionDiscountRule extends CompoundDiscountRule {

    public MaximumCompositionDiscountRule(List<DiscountRule> discountRules){
        super(0, discountRules);
    }

    public MaximumCompositionDiscountRule(MaximumCompositionDiscountRuleDTO ruleDTO){
        super(ruleDTO.getDiscount(), ruleDTO.getConcreteDiscountRules());
        this.setID(ruleDTO.getId());
    }

    @Override
    public DiscountRuleDTO toDTO(){
        return new MaximumCompositionDiscountRuleDTO(this.id, this.getDiscountRulesDTO(), this.discount);
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
