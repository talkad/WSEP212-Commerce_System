package Server.DAL.DiscountRuleDTOs;

import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.DiscountRules.SumCompositionDiscountRule;
import dev.morphia.annotations.Embedded;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;

@Embedded
@BsonDiscriminator("SumCompositionDiscountRuleDTO")

public class SumCompositionDiscountRuleDTO extends CompoundDiscountRuleDTO {

    public SumCompositionDiscountRuleDTO() {
        super();
        // For Morphia
    }

    public SumCompositionDiscountRuleDTO(int id, List<DiscountRuleDTO> discountRules, double discount) {
        super(id, discountRules, discount);
    }

    @Override
    public DiscountRule toConcreteDiscountRule() {
        return new SumCompositionDiscountRule(this);
    }
}
