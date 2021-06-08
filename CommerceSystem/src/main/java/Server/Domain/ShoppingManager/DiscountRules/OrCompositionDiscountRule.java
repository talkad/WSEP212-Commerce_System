package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.DAL.DiscountRuleDTOs.DiscountRuleDTO;
import Server.DAL.DiscountRuleDTOs.OrCompositionDiscountRuleDTO;


import java.util.List;
import java.util.Map;

public class OrCompositionDiscountRule extends CompoundDiscountRule {
    private String category;

    public OrCompositionDiscountRule(String category, double discount, List<DiscountRule> policyRules) {
        super(discount, policyRules);
        this.category = category;
    }

    public OrCompositionDiscountRule(OrCompositionDiscountRuleDTO ruleDTO){
        super(ruleDTO.getDiscount(), ruleDTO.getConcreteDiscountRules());
        this.setID(ruleDTO.getId());
        this.category = ruleDTO.getCategory();
    }

    @Override
    public DiscountRuleDTO toDTO(){
        return new OrCompositionDiscountRuleDTO(this.id, this.getDiscountRulesDTO(), this.discount, this.category);
    }

    @Override
    public double calcDiscount(Map<ProductClientDTO, Integer> shoppingBasket) {
        double totalPriceToDiscount = 0.0;
        boolean eligibleForDiscount = false;
        for(DiscountRule discountRule : discountRules)
            if(discountRule.calcDiscount(shoppingBasket) < 0) {
                eligibleForDiscount = true;
                break;
            }

        if(eligibleForDiscount){
            for(Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet())
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
