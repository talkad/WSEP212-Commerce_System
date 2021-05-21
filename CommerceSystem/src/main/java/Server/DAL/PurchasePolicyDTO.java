package Server.DAL;

import Server.DAL.PurchaseRuleDTOs.PurchaseRuleDTO;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;

@Embedded
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
        return purchaseRules;
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
