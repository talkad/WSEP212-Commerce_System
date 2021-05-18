package Server.DAL.DiscountRuleDTOs;

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
}
