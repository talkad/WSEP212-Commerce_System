package Server.Domain.ShoppingManager.PurchaseRules;

import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class CompoundPurchaseRule implements PurchaseRule {
    protected final static int NOT_SET = -1;
    protected int id;
    protected List<PurchaseRule> purchaseRules;

    public CompoundPurchaseRule(List<PurchaseRule> policyRules) {
        this.purchaseRules = (policyRules == null) ? Collections.synchronizedList(new LinkedList<>()) : Collections.synchronizedList(policyRules);
        this.id = NOT_SET;
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

