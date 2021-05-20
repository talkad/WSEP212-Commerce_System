package Server.Domain.ShoppingManager.PurchaseRules;

import Server.DAL.DiscountRuleDTOs.DiscountRuleDTO;
import Server.DAL.PurchaseRuleDTOs.PurchaseRuleDTO;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.ProductDTO;

import java.util.*;

public abstract class CompoundPurchaseRule implements PurchaseRule {
    protected final static int NOT_SET = -1;
    protected int id;
    protected List<PurchaseRule> purchaseRules;

    public CompoundPurchaseRule(List<PurchaseRule> policyRules) {
        this.purchaseRules = (policyRules == null) ? Collections.synchronizedList(new LinkedList<>()) : Collections.synchronizedList(policyRules);
        this.id = NOT_SET;
    }

    public List<PurchaseRuleDTO> getPurchaseRulesDTO(){
        List<PurchaseRuleDTO> purchaseRuleDTOS = new Vector<>();
        for(PurchaseRule purchaseRule : this.purchaseRules){
            purchaseRuleDTOS.add(purchaseRule.toDTO());
        }
        return purchaseRuleDTOS;
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

