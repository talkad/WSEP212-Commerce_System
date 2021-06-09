package Server.DAL.PurchaseRuleDTOs;

import Server.DAL.PredicateDTOs.PredicateDTO;
import Server.DAL.PredicateDTOs.ProductPredicateDTO;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Embedded
@BsonDiscriminator("LeafPurchaseRuleDTO")

public class LeafPurchaseRuleDTO extends PurchaseRuleDTO {

    @Property(value = "id")
    protected int id;

    @Property(value = "predicate")
    protected PredicateDTO predicate;

    public LeafPurchaseRuleDTO(){
        // For Morphia
    }

    public LeafPurchaseRuleDTO(int id, PredicateDTO predicate) {
        this.id = id;
        this.predicate = predicate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PredicateDTO getPredicate() {
        return predicate == null ? new ProductPredicateDTO() : predicate;
    }

    public void setPredicate(PredicateDTO predicate) {
        this.predicate = predicate;
    }
}
