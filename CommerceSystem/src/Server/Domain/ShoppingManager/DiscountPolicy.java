package Server.Domain.ShoppingManager;

import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DiscountPolicy {
    private List<DiscountRule> discountRules;

    public DiscountPolicy() {
        discountRules = Collections.synchronizedList(new LinkedList<>());
    }

    public double calcDiscount(Map<ProductDTO, Integer> shoppingBasket) {
        double totalPrice = 0.0;
        double discount = 0.0;
        for (Map.Entry<ProductDTO, Integer> entry : shoppingBasket.entrySet())
            totalPrice += entry.getKey().getPrice() * entry.getValue();
        for (DiscountRule discountRule : discountRules)
            discount += discountRule.calcDiscount(shoppingBasket);

        return totalPrice - discount;
    }

    public void addDiscountRule(DiscountRule discountRule) {
        if (discountRule != null)
            discountRules.add(discountRule);
    }

    public void removeDiscountRule(int discountRuleID) {
        DiscountRule getRule = getDiscountRule(discountRuleID);
        if (getRule != null)
            discountRules.remove(getRule);
    }

    public DiscountRule getDiscountRule(int id) {
        for (DiscountRule discountRule : discountRules)
            if (discountRule.getID() == id)
                return discountRule;
        return null;
    }
}
