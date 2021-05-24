package Server.Domain.ShoppingManager.PurchaseRules;

import Server.DAL.PurchaseRuleDTOs.ProductPurchaseRuleDTO;
import Server.DAL.PurchaseRuleDTOs.PurchaseRuleDTO;
import Server.Domain.ShoppingManager.Predicates.ProductPredicate;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.Map;

public class ProductPurchaseRule extends LeafPurchaseRule {

    public ProductPurchaseRule(ProductPredicate predicate) {
        super(predicate);
    }

    public ProductPurchaseRule(ProductPurchaseRuleDTO ruleDTO){
        super(ruleDTO.getPredicate().toConcretePredicate());
        this.setID(ruleDTO.getId());
    }

    @Override
    public PurchaseRuleDTO toDTO() {
        return new ProductPurchaseRuleDTO(this.id, this.predicate.toDTO());
    }

    @Override
    public boolean isValidPurchase(Map<ProductClientDTO, Integer> shoppingBasket) {
        return predicate.isValid(shoppingBasket);
    }

    @Override
    public String getDescription() {
        return "Product Purchase rule: " + id;
    }


}
