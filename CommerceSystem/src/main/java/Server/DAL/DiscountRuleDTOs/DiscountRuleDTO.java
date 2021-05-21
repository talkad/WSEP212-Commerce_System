package Server.DAL.DiscountRuleDTOs;

import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import dev.morphia.annotations.Embedded;

@Embedded
public interface DiscountRuleDTO {
    public DiscountRule toConcreteDiscountRule();
}
