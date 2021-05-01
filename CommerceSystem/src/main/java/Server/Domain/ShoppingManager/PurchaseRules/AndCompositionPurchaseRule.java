package Server.Domain.ShoppingManager.PurchaseRules;

import Server.Domain.ShoppingManager.ProductDTO;

import java.util.List;
import java.util.Map;

public class AndCompositionPurchaseRule extends CompoundPurchaseRule {

    public AndCompositionPurchaseRule(int id, List<PurchaseRule> policyRules) {
        super(id, policyRules);
    }

    @Override
    public boolean isValidPurchase(Map<ProductDTO, Integer> shoppingBasket) {
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
