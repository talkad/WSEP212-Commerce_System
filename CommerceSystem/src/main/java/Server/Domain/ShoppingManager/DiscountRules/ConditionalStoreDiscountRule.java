package Server.Domain.ShoppingManager.DiscountRules;

import Server.DAL.DiscountRuleDTOs.ConditionalStoreDiscountRuleDTO;
import Server.DAL.DiscountRuleDTOs.DiscountRuleDTO;
import Server.DAL.DiscountRuleDTOs.StoreDiscountRuleDTO;
import Server.DAL.PredicateDTOs.StorePredicateDTO;
import Server.Domain.ShoppingManager.Predicates.StorePredicate;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.Map;

public class ConditionalStoreDiscountRule extends StoreDiscountRule {
    private int productID;
    private StorePredicate storePredicate;

    public ConditionalStoreDiscountRule(double discount, StorePredicate storePredicate) {
        super(discount);
        this.storePredicate = storePredicate;
        this.productID = -1;
    }

    public ConditionalStoreDiscountRule(double discount, StorePredicate storePredicate, int productID) {
        super(discount);
        this.storePredicate = storePredicate;
        this.productID = productID;
    }

    public ConditionalStoreDiscountRule(ConditionalStoreDiscountRuleDTO ruleDTO){
        super(ruleDTO.getDiscount());
        this.setID(ruleDTO.getId());
        this.storePredicate = (StorePredicate) ruleDTO.getStorePredicate().toConcretePredicate();
        this.productID = ruleDTO.getProductID();
    }

    @Override
    public DiscountRuleDTO toDTO() {
        return new ConditionalStoreDiscountRuleDTO(this.id, this.discount, this.productID, (StorePredicateDTO) this.storePredicate.toDTO());
    }


    @Override
    public double calcDiscount(Map<ProductClientDTO, Integer> shoppingBasket) {
        if(storePredicate.isValid(shoppingBasket)) {
            double totalPrice = 0.0;
            if(productID != -1) {
                for (Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet())
                    totalPrice += entry.getKey().getPrice() * entry.getValue();
            }
            else{
                for (Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet())
                    if(entry.getKey().getProductID() == productID) {
                        totalPrice += entry.getKey().getPrice() * entry.getValue();
                        break;
                    }
            }
            return totalPrice * (this.discount / 100);
        }
        else
            return 0.0;
    }

    @Override
    public String getDescription() {
        return "Conditional Store Discount Rule No." + id + ":\n " + this.discount + "% discount on the entire store - " + storePredicate;
    }
}
