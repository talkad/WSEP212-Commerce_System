package Server.Domain.ShoppingManager.PurchaseRules;

import Server.DAL.PurchaseRuleDTOs.BasketPurchaseRuleDTO;
import Server.DAL.PurchaseRuleDTOs.PurchaseRuleDTO;
import Server.Domain.ShoppingManager.Predicates.BasketPredicate;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.Map;

public class BasketPurchaseRule extends LeafPurchaseRule {

    public BasketPurchaseRule(BasketPredicate predicate) {
        super(predicate);
    }

    public BasketPurchaseRule(BasketPurchaseRuleDTO ruleDTO){
        super(ruleDTO.getPredicate().toConcretePredicate());
        this.setID(ruleDTO.getId());
    }

    @Override
    public PurchaseRuleDTO toDTO() {
        return new BasketPurchaseRuleDTO(this.id, this.predicate.toDTO());
    }

    @Override
    public boolean isValidPurchase(Map<ProductClientDTO, Integer> shoppingBasket) {
        return predicate.isValid(shoppingBasket);
    }

    @Override
    public String getDescription() {
        return "Basket Purchase Rule " + id;
    }

    @Override
    public String toString() {
        return predicate.toString();
    }
}
