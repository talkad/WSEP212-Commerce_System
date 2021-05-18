package Server.DAL.PurchaseRuleDTOs;

import Server.DAL.PredicateDTOs.PredicateDTO;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

@Embedded
public abstract class LeafPurchaseRuleDTO implements PurchaseRuleDTO {

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
        return predicate;
    }

    public void setPredicate(PredicateDTO predicate) {
        this.predicate = predicate;
    }
}
