package Server.DAL.PurchaseRuleDTOs;

import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
import dev.morphia.annotations.Embedded;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Embedded
@BsonDiscriminator("PurchaseRuleDTO")

public class PurchaseRuleDTO {
    public PurchaseRuleDTO(){
        // For Morphia
    }

    public PurchaseRule toConcretePurchaseRule(){return null;}
}
