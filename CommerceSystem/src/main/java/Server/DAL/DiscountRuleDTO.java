package Server.DAL;

import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import dev.morphia.annotations.Embedded;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.io.Serializable;

@Embedded
@BsonDiscriminator("DiscountRuleDTO")

public class DiscountRuleDTO {
    public DiscountRuleDTO(){
        // For Morphia
    }
    public DiscountRule toConcreteDiscountRule(){return null;}
}
