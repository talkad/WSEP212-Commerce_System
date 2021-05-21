package Server.DAL.PurchaseRuleDTOs;

import Server.DAL.PredicateDTOs.PredicateDTO;
import Server.Domain.ShoppingManager.PurchaseRules.CategoryPurchaseRule;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
import dev.morphia.annotations.Embedded;

@Embedded
public class CategoryPurchaseRuleDTO extends LeafPurchaseRuleDTO{

    public CategoryPurchaseRuleDTO() {
        super();
        // For Morphia
    }

    public CategoryPurchaseRuleDTO(int id, PredicateDTO predicate) {
        super(id, predicate);
    }

    @Override
    public PurchaseRule toConcretePurchaseRule() {
        return new CategoryPurchaseRule(this);
    }
}
