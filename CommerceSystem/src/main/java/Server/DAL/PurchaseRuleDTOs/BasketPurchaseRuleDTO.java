package Server.DAL.PurchaseRuleDTOs;

import Server.DAL.PredicateDTOs.PredicateDTO;
import dev.morphia.annotations.Embedded;

@Embedded
public class BasketPurchaseRuleDTO extends LeafPurchaseRuleDTO{

    public BasketPurchaseRuleDTO() {
        super();
        // For Morphia
    }

    public BasketPurchaseRuleDTO(int id, PredicateDTO predicate) {
        super(id, predicate);
    }
}
