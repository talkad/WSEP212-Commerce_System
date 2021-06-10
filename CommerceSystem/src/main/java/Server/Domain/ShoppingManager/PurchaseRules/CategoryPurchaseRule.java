package Server.Domain.ShoppingManager.PurchaseRules;

import Server.DAL.PurchaseRuleDTOs.CategoryPurchaseRuleDTO;
import Server.DAL.PurchaseRuleDTOs.PurchaseRuleDTO;
import Server.Domain.ShoppingManager.Predicates.CategoryPredicate;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.Map;

public class CategoryPurchaseRule extends LeafPurchaseRule {

    public CategoryPurchaseRule(CategoryPredicate predicate) {
        super(predicate);
    }

    public CategoryPurchaseRule(CategoryPurchaseRuleDTO ruleDTO){
        super(ruleDTO.getPredicate().toConcretePredicate());
        this.setID(ruleDTO.getId());
    }

    @Override
    public PurchaseRuleDTO toDTO() {
        return new CategoryPurchaseRuleDTO(this.id, this.predicate.toDTO());
    }

    @Override
    public boolean isValidPurchase(Map<ProductClientDTO, Integer> shoppingBasket) {
        return predicate.isValid(shoppingBasket);
    }

    @Override
    public String getDescription() {
        return "Product Purchase Rule " + id;
    }

    @Override
    public String toString() {
        return "" + predicate;
    }
}
