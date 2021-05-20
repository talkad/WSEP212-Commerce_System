package Server.DAL.PurchaseRuleDTOs;

import Server.Domain.ShoppingManager.PurchaseRules.OrCompositionPurchaseRule;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
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

    @Override
    public PurchaseRule toConcretePurchaseRule() {
        return new OrCompositionPurchaseRule(this);
    }
}
