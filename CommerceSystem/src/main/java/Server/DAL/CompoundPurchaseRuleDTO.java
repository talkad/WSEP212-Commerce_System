package Server.DAL;

import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;
import java.util.Vector;

@Embedded
@BsonDiscriminator("CompoundPurchaseRuleDTO")

public class CompoundPurchaseRuleDTO extends PurchaseRuleDTO{

    @Property(value = "id")
    protected int id;

    @Property(value = "purchaseRules")
    protected List<PurchaseRuleDTO> purchaseRules;

    public CompoundPurchaseRuleDTO(){
        // For Morphia
    }

    public CompoundPurchaseRuleDTO(int id, List<PurchaseRuleDTO> purchaseRules) {
        this.id = id;
        this.purchaseRules = purchaseRules;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<PurchaseRuleDTO> getPurchaseRules() {
        return purchaseRules == null ? new Vector<>() : purchaseRules;
    }

    public void setPurchaseRules(List<PurchaseRuleDTO> purchaseRules) {
        this.purchaseRules = purchaseRules;
    }

    public List<PurchaseRule> getConcretePurchaseRules(){
        List<PurchaseRule> concretePurchaseRules = new Vector<>();
        for(PurchaseRuleDTO purchaseRuleDTO : this.purchaseRules){
            concretePurchaseRules.add(purchaseRuleDTO.toConcretePurchaseRule());
        }
        return concretePurchaseRules;
    }

}
