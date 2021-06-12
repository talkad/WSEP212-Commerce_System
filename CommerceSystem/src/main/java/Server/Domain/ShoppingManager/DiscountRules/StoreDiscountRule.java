package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.DAL.DiscountRuleDTOs.DiscountRuleDTO;
import Server.DAL.DiscountRuleDTOs.StoreDiscountRuleDTO;


import java.util.Map;

public class StoreDiscountRule extends LeafDiscountRule {

    public StoreDiscountRule(double discount){
        super(discount);
    }

    public StoreDiscountRule(StoreDiscountRuleDTO ruleDTO){
        super(ruleDTO.getDiscount());
        this.setID(ruleDTO.getId());
    }

    @Override
    public DiscountRuleDTO toDTO() {
        return new StoreDiscountRuleDTO(this.id, this.discount);
    }

    @Override
    public double calcDiscount(Map<ProductClientDTO, Integer> shoppingBasket) {
        double totalPrice = 0.0;
        for(Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet())
            totalPrice += entry.getKey().getPrice() * entry.getValue();
        return totalPrice * (this.discount / 100);
    }

    @Override
    public String getDescription() {
        return "Store Discount Rule No." + id + ": " + this.discount + "% discount on the entire store.\\n";
    }
}
