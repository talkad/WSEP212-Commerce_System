package Server.Domain.ShoppingManager.DiscountRules;

import Server.DAL.DiscountRuleDTOs.AndCompositionDiscountRuleDTO;
import Server.DAL.DiscountRuleDTOs.DiscountRuleDTO;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.List;
import java.util.Map;

public class AndCompositionDiscountRule extends CompoundDiscountRule {
    private String category;

    public AndCompositionDiscountRule(String category, double discount, List<DiscountRule> policyRules) {
        super(discount, policyRules);
        this.category = category;
    }

    public AndCompositionDiscountRule(AndCompositionDiscountRuleDTO ruleDTO){
        super(ruleDTO.getDiscount(), ruleDTO.getConcreteDiscountRules());
        this.id = ruleDTO.getId();
        this.category = ruleDTO.getCategory();
    }

    @Override
    public DiscountRuleDTO toDTO(){
        return new AndCompositionDiscountRuleDTO(this.id, this.getDiscountRulesDTO(), this.discount, this.category);
    }

    @Override
    public double calcDiscount(Map<ProductClientDTO, Integer> shoppingBasket) {
        double totalPriceToDiscount = 0.0;
        boolean eligibleForDiscount = true;
        for (DiscountRule discountRule : discountRules)
            if (discountRule.calcDiscount(shoppingBasket) == 0) {
                eligibleForDiscount = false;
                break;
            }

        if (eligibleForDiscount) {
            for (Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet())
                if (entry.getKey().getCategories().contains(category))
                    totalPriceToDiscount += entry.getKey().getPrice() * entry.getValue();
        }

        return totalPriceToDiscount * (discount / 100);
    }

    @Override
    public String getDescription() {
        return "And Composition: " + id;
    }
}
