package Server.Domain.ShoppingManager.PurchaseRules;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.DAL.PurchaseRuleDTOs.AndCompositionPurchaseRuleDTO;
import Server.DAL.PurchaseRuleDTOs.PurchaseRuleDTO;
import java.util.List;
import java.util.Map;

public class AndCompositionPurchaseRule extends CompoundPurchaseRule {

    public AndCompositionPurchaseRule(List<PurchaseRule> policyRules) {
        super(policyRules);
    }

    public AndCompositionPurchaseRule(AndCompositionPurchaseRuleDTO ruleDTO){
        super(ruleDTO.getConcretePurchaseRules());
        this.setID(ruleDTO.getId());
    }

    @Override
    public PurchaseRuleDTO toDTO() {
        return new AndCompositionPurchaseRuleDTO(this.id, this.getPurchaseRulesDTO());
    }

    @Override
    public boolean isValidPurchase(Map<ProductClientDTO, Integer> shoppingBasket) {
       for(PurchaseRule purchaseRule : purchaseRules)
           if(!purchaseRule.isValidPurchase(shoppingBasket))
               return false;

       return true;
    }

    @Override
    public String getDescription() {
        return "And Composition Purchase Rule " + id;
    }
}
