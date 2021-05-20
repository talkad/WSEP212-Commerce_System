package Server.DAL.DiscountRuleDTOs;

import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.DiscountRules.MaximumCompositionDiscountRule;
import dev.morphia.annotations.Embedded;

import java.util.List;

@Embedded
public class MaximumCompositionDiscountRuleDTO extends CompoundDiscountRuleDTO{

    public MaximumCompositionDiscountRuleDTO() {
        super();
        // For Morphia
    }

    public MaximumCompositionDiscountRuleDTO(int id, List<DiscountRuleDTO> discountRules, double discount) {
        super(id, discountRules, discount);
    }

    @Override
    public DiscountRule toConcreteDiscountRule() {
        return new MaximumCompositionDiscountRule(this);
    }
}
