package Server.DAL.DiscountRuleDTOs;

import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.DiscountRules.StoreDiscountRule;
import dev.morphia.annotations.Embedded;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Embedded
@BsonDiscriminator("StoreDiscountRuleDTO")

public class StoreDiscountRuleDTO extends LeafDiscountRuleDTO {

    public StoreDiscountRuleDTO() {
        super();
        // For Morphia
    }

    public StoreDiscountRuleDTO(int id, double discount) {
        super(id, discount);
    }


    @Override
    public DiscountRule toConcreteDiscountRule() {
        return new StoreDiscountRule(this);
    }
}
