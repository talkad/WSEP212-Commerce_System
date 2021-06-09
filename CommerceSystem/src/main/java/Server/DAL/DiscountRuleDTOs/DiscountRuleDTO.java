package Server.DAL.DiscountRuleDTOs;

import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import dev.morphia.annotations.Embedded;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.io.Serializable;

@Embedded
@BsonDiscriminator("DiscountRuleDTO")

public interface DiscountRuleDTO {
    public DiscountRule toConcreteDiscountRule();
}
