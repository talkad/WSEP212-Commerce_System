package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.DAL.DiscountRuleDTO;
import Server.DAL.SumCompositionDiscountRuleDTO;


import java.util.List;
import java.util.Map;

public class SumCompositionDiscountRule extends CompoundDiscountRule {

    public SumCompositionDiscountRule(List<DiscountRule> policyRules){
        super(0, policyRules);
    }

    public SumCompositionDiscountRule(SumCompositionDiscountRuleDTO ruleDTO){
        super(ruleDTO.getDiscount(), ruleDTO.getConcreteDiscountRules());
        this.setID(ruleDTO.getId());
    }

    @Override
    public DiscountRuleDTO toDTO(){
        return new SumCompositionDiscountRuleDTO(this.id, this.getDiscountRulesDTO(), this.discount);
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
