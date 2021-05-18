package Server.DAL.PurchaseRuleDTOs;

import dev.morphia.annotations.Embedded;

import java.util.List;

@Embedded
public class OrCompositionPurchaseRuleDTO extends CompoundPurchaseRuleDTO{

    public OrCompositionPurchaseRuleDTO() {
        super();
        // For Morphia
    }

    public OrCompositionPurchaseRuleDTO(int id, List<PurchaseRuleDTO> purchaseRules) {
        super(id, purchaseRules);
    }
}
