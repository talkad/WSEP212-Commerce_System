package Server.DAL.PurchaseRuleDTOs;

import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
import dev.morphia.annotations.Embedded;

@Embedded
public interface PurchaseRuleDTO {
    public PurchaseRule toConcretePurchaseRule();
}
