package Server.DAL.DiscountRuleDTOs;

import dev.morphia.annotations.Embedded;

@Embedded
public class StoreDiscountRuleDTO extends LeafDiscountRuleDTO {

    public StoreDiscountRuleDTO() {
        super();
        // For Morphia
    }

    public StoreDiscountRuleDTO(int id, double discount) {
        super(id, discount);
    }
}
