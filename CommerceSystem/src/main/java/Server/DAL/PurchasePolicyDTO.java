package Server.DAL;

import Server.DAL.PurchaseRuleDTOs.PurchaseRuleDTO;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;
import java.util.Vector;

@Embedded
@BsonDiscriminator("PurchasePolicyDTO")

public class PurchasePolicyDTO {

    @Property(value = "purchaseRules")
    private List<PurchaseRuleDTO> purchaseRules;

    @Property(value = "indexer")
    private int indexer;

    public PurchasePolicyDTO(){
        // For Morphia
    }

    public PurchasePolicyDTO(List<PurchaseRuleDTO> purchaseRules, int indexer) {
        this.purchaseRules = purchaseRules;
        this.indexer = indexer;
    }

    public List<PurchaseRuleDTO> getPurchaseRules() {
        return purchaseRules == null ? new Vector<>() : purchaseRules;
    }

    public void setPurchaseRules(List<PurchaseRuleDTO> purchaseRules) {
        this.purchaseRules = purchaseRules;
    }

    public int getIndexer() {
        return indexer;
    }

    public void setIndexer(int indexer) {
        this.indexer = indexer;
    }
}
