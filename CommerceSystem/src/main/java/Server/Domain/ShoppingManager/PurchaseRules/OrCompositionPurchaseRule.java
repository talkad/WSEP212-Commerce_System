package Server.Domain.ShoppingManager.PurchaseRules;

import Server.DAL.PurchaseRuleDTOs.OrCompositionPurchaseRuleDTO;
import Server.DAL.PurchaseRuleDTOs.PurchaseRuleDTO;
import Server.Domain.ShoppingManager.ProductDTO;

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
    public boolean isValidPurchase(Map<ProductDTO, Integer> shoppingBasket) {
        for(PurchaseRule purchaseRule : purchaseRules)
            if(purchaseRule.isValidPurchase(shoppingBasket))
                return true;
       return false;
    }

    @Override
    public String getDescription() {
        return "Or Composition Purchase Rule " + id;
    }
}
