package Server.Domain.ShoppingManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PurchasePolicy {
    private List<PurchaseRule> purchaseRules;
    private AtomicInteger indexer;

    public PurchasePolicy(){
        this.purchaseRules = Collections.synchronizedList(new LinkedList<>());
        indexer = new AtomicInteger(0);
    }

    public Response<Boolean> isValidPurchase(Map<ProductDTO, Integer> shoppingBasket){
        for(PurchaseRule purchaseRule : purchaseRules)
            if(!purchaseRule.isValidPurchase(shoppingBasket))
                return new Response<>(false, true, "Not qualified of policy demands.");

        return new Response<>(true, false, "Shopping basket meets policy demands.");
    }

    public Response<Boolean> addPurchaseRule(PurchaseRule purchaseRule) {
        if (purchaseRule != null) {
            if(purchaseRule.getID() < 0)
                purchaseRule.setID(indexer.incrementAndGet());

            purchaseRules.add(purchaseRule);
        }

        return new Response<>(true, false, "OK");
    }

    public Response<Boolean> removePurchaseRule(int purchaseRuleID) {
        PurchaseRule getRule = getPurchaseRule(purchaseRuleID);
        if (getRule != null) {
            purchaseRules.remove(getRule);
            return new Response<>(true, false, "OK");
        }

        return new Response<>(false, true, "This discount policy doesn't exists");
    }

    public PurchaseRule getPurchaseRule(int id) {
        for (PurchaseRule purchaseRule : purchaseRules)
            if (purchaseRule.getID() == id)
                return purchaseRule;
        return null;
    }
}
