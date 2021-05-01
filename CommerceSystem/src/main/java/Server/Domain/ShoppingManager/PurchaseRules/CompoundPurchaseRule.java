package Server.Domain.ShoppingManager.PurchaseRules;

import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class CompoundPurchaseRule implements PurchaseRule {
    protected int id;
    protected List<PurchaseRule> purchaseRules;

    public CompoundPurchaseRule(int id, List<PurchaseRule> policyRules) {
        this.purchaseRules = (policyRules == null) ? Collections.synchronizedList(new LinkedList<>()) : Collections.synchronizedList(policyRules);
        this.id = id;
    }

    public void add(PurchaseRule discountRule) {
        purchaseRules.add(discountRule);
    }

    public void remove(PurchaseRule discountRule) {
        purchaseRules.remove(discountRule);
    }

    public abstract boolean isValidPurchase(Map<ProductDTO, Integer> shoppingBasket);

    public int getID() {
        return id;
    }

    public String getDescription() {
        return "Compound Policy Rule " + id;
    }

    @Override
    public void setID(int id){
        this.id = id;
    }
}

