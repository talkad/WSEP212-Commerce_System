package Server.DAL.PurchaseRuleDTOs;

import Server.Domain.ShoppingManager.PurchaseRules.AndCompositionPurchaseRule;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
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

    @Override
    public PurchaseRule toConcretePurchaseRule() {
        return new AndCompositionPurchaseRule(this);
    }
}
