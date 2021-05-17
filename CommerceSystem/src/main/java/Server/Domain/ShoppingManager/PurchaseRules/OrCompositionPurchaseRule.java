package Server.Domain.ShoppingManager.PurchaseRules;

import Server.Domain.ShoppingManager.ProductDTO;

import java.util.List;
import java.util.Map;

public class OrCompositionPurchaseRule extends CompoundPurchaseRule {

    public OrCompositionPurchaseRule(List<PurchaseRule> policyRules) {
        super(policyRules);
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
