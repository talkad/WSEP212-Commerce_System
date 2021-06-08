package Server.DAL;

import Server.Domain.ShoppingManager.PurchaseRules.CategoryPurchaseRule;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
import dev.morphia.annotations.Embedded;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Embedded
@BsonDiscriminator("CategoryPurchaseRuleDTO")

public class CategoryPurchaseRuleDTO extends LeafPurchaseRuleDTO{

    public CategoryPurchaseRuleDTO() {
        super();
        // For Morphia
    }

    public CategoryPurchaseRuleDTO(int id, PredicateDTO predicate) {
        super(id, predicate);
    }

    @Override
    public PurchaseRule toConcretePurchaseRule() {
        return new CategoryPurchaseRule(this);
    }
}
