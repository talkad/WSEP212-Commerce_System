package Server.Domain.ShoppingManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PurchasePolicy {
    private List<PurchaseRule> purchaseRules;

    public PurchasePolicy(){
        this.purchaseRules = Collections.synchronizedList(new LinkedList<>());
    }

    public Response<Boolean> isValidPurchase(Map<ProductDTO, Integer> shoppingBasket){
        for(PurchaseRule purchaseRule : purchaseRules)
            if(!purchaseRule.isValidPurchase(shoppingBasket))
                return new Response<>(false, true, "Not qualified of policy demands.");

        return new Response<>(true, false, "Shopping basket meets policy demands.");
    }

    public void addPurchaseRule(PurchaseRule purchaseRule) {
        if (purchaseRule != null)
            purchaseRules.add(purchaseRule);
    }

    public void removePurchaseRule(int purchaseRuleID) {
        PurchaseRule getRule = getPurchaseRule(purchaseRuleID);
        if (getRule != null)
            purchaseRules.remove(getRule);
    }

    public PurchaseRule getPurchaseRule(int id) {
        for (PurchaseRule purchaseRule : purchaseRules)
            if (purchaseRule.getID() == id)
                return purchaseRule;
        return null;
    }
}
