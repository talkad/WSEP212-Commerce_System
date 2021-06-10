package Server.Domain.ShoppingManager.PurchaseRules;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.DAL.PurchaseRuleDTOs.OrCompositionPurchaseRuleDTO;
import Server.DAL.PurchaseRuleDTOs.PurchaseRuleDTO;
import java.util.List;
import java.util.Map;

public class OrCompositionPurchaseRule extends CompoundPurchaseRule {

    public OrCompositionPurchaseRule(List<PurchaseRule> policyRules) {
        super(policyRules);
    }

    public OrCompositionPurchaseRule(OrCompositionPurchaseRuleDTO ruleDTO){
        super(ruleDTO.getConcretePurchaseRules());
        this.setID(ruleDTO.getId());
    }

    @Override
    public PurchaseRuleDTO toDTO() {
        return new OrCompositionPurchaseRuleDTO(this.id, this.getPurchaseRulesDTO());
    }

    @Override
    public boolean isValidPurchase(Map<ProductClientDTO, Integer> shoppingBasket) {
        for(PurchaseRule purchaseRule : purchaseRules)
            if(purchaseRule.isValidPurchase(shoppingBasket))
                return true;
       return false;
    }

    @Override
    public String getDescription() {
        return "Or Composition Purchase Rule " + id;
    }

    @Override
    public String toString() {
        String [] compoundStrings = new String[purchaseRules.size()];
        int i = 0;
        for(PurchaseRule rule: purchaseRules) {
            compoundStrings[i] = rule.toString();
            ++i;
        }

        return "Or Composition Purchase Rule No." + id + ":\n" + String.join("OR\n", compoundStrings);
    }
}
