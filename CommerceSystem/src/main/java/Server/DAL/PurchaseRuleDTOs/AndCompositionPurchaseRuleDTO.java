package Server.DAL.PurchaseRuleDTOs;

import dev.morphia.annotations.Embedded;

import java.util.List;

@Embedded
public class AndCompositionPurchaseRuleDTO extends CompoundPurchaseRuleDTO{

    public AndCompositionPurchaseRuleDTO() {
        super();
        // For Morphia
    }

    public AndCompositionPurchaseRuleDTO(int id, List<PurchaseRuleDTO> purchaseRules) {
        super(id, purchaseRules);
    }
}
