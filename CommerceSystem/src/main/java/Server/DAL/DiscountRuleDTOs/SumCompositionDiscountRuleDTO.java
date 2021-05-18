package Server.DAL.DiscountRuleDTOs;

import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import dev.morphia.annotations.Embedded;

import java.util.List;

@Embedded
public class SumCompositionDiscountRuleDTO extends CompoundDiscountRuleDTO{

    public SumCompositionDiscountRuleDTO() {
        super();
        // For Morphia
    }

    public SumCompositionDiscountRuleDTO(int id, List<DiscountRuleDTO> discountRules, double discount) {
        super(id, discountRules, discount);
    }

}
