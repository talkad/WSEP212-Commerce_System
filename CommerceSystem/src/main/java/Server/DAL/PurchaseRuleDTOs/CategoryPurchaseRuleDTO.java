package Server.DAL.PurchaseRuleDTOs;

import Server.DAL.PredicateDTOs.PredicateDTO;
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
}
