package Server.Domain.ShoppingManager;

import Server.DAL.DomainDTOs.DiscountPolicyDTO;
import Server.DAL.DiscountRuleDTOs.DiscountRuleDTO;
import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DiscountPolicy {
    private List<DiscountRule> discountRules;
    private AtomicInteger indexer;

    public DiscountPolicy() {
        discountRules = Collections.synchronizedList(new LinkedList<>());
        indexer = new AtomicInteger(0);
    }

    public DiscountPolicy(DiscountPolicyDTO policyDTO){
        this.discountRules = new Vector<>();
        List<DiscountRuleDTO> ruleDTOS = policyDTO.getDiscountRules();
        if(ruleDTOS != null){
            for(DiscountRuleDTO ruleDTO : ruleDTOS){
                this.discountRules.add(ruleDTO.toConcreteDiscountRule());
            }
        }
        this.indexer = new AtomicInteger(policyDTO.getIndexer());
    }

    public DiscountPolicyDTO toDTO(){
        List<DiscountRuleDTO> ruleDTOS = new Vector<>();
        for(DiscountRule rule : this.discountRules){
            ruleDTOS.add(rule.toDTO());
        }
        return new DiscountPolicyDTO(ruleDTOS, this.indexer.get());
    }

    public double calcDiscount(Map<ProductClientDTO, Integer> shoppingBasket) {
        double totalPrice = 0.0;
        double discount = 0.0;
        for (Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet())
            totalPrice += entry.getKey().getPrice() * entry.getValue();
        for (DiscountRule discountRule : discountRules)
            discount += discountRule.calcDiscount(shoppingBasket);

        return totalPrice - discount;
    }

    public Response<Boolean> addDiscountRule(DiscountRule discountRule) {
        if (discountRule != null) {
            if (discountRule.getID() < 0)
                discountRule.setID(indexer.incrementAndGet());

            discountRules.add(discountRule);
        }

        return new Response<>(true, false, "OK");
    }

    public  Response<Boolean> removeDiscountRule(int discountRuleID) {
        DiscountRule getRule = getDiscountRule(discountRuleID);
        if (getRule != null) {
            discountRules.remove(getRule);
            return new Response<>(true, false, "OK");
        }

        return new Response<>(false, true, "This discount policy doesn't exists");
    }

    public DiscountRule getDiscountRule(int id) {
        for (DiscountRule discountRule : discountRules)
            if (discountRule.getID() == id)
                return discountRule;
        return null;
    }

    public String getDescription() {
        StringBuilder result = new StringBuilder();

        for(DiscountRule discount: discountRules)
            result.append("- ").append(discount.toString()).append("\n");

        return result.toString();
    }
}
