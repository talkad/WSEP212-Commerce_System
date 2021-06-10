package Server.Domain.ShoppingManager;

import Server.DAL.PurchasePolicyDTO;
import Server.DAL.PurchaseRuleDTOs.PurchaseRuleDTO;
import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PurchasePolicy {
    private List<PurchaseRule> purchaseRules;
    private AtomicInteger indexer;

    public PurchasePolicy(){
        this.purchaseRules = Collections.synchronizedList(new LinkedList<>());
        indexer = new AtomicInteger(0);
    }

    public PurchasePolicy(PurchasePolicyDTO policyDTO){
        this.purchaseRules = new Vector<>();
        List<PurchaseRuleDTO> rulesList = policyDTO.getPurchaseRules();
        if(rulesList != null){
            for(PurchaseRuleDTO ruleDTO : rulesList){
                this.purchaseRules.add(ruleDTO.toConcretePurchaseRule());
            }
        }
        this.indexer = new AtomicInteger(policyDTO.getIndexer());
    }

    public PurchasePolicyDTO toDTO(){
        List<PurchaseRuleDTO> rulesList = new Vector<>();
        for(PurchaseRule rule : this.purchaseRules){
            rulesList.add(rule.toDTO());
        }
        return new PurchasePolicyDTO(rulesList, this.indexer.get());
    }

    public Response<Boolean> isValidPurchase(Map<ProductClientDTO, Integer> shoppingBasket){
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

    public String getDescription() {
        StringBuilder result = new StringBuilder();

        for(PurchaseRule rule: purchaseRules)
            result.append("- ").append(rule.toString()).append("\n");

        return result.toString();
    }
}
