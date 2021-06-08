package Server.DAL;

import Server.Domain.ShoppingManager.PurchaseRules.OrCompositionPurchaseRule;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
import dev.morphia.annotations.Embedded;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;

@Embedded
@BsonDiscriminator("OrCompositionPurchaseRuleDTO")

public class OrCompositionPurchaseRuleDTO extends CompoundPurchaseRuleDTO{

    public OrCompositionPurchaseRuleDTO() {
        super();
        // For Morphia
    }

    public OrCompositionPurchaseRuleDTO(int id, List<PurchaseRuleDTO> purchaseRules) {
        super(id, purchaseRules);
    }

    @Override
    public PurchaseRule toConcretePurchaseRule() {
        return new OrCompositionPurchaseRule(this);
    }
}
