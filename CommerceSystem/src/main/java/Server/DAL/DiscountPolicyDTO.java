package Server.DAL;

import Server.DAL.DiscountRuleDTOs.DiscountRuleDTO;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;
import java.util.Vector;

@Embedded
@BsonDiscriminator("DiscountPolicyDTO")

public class DiscountPolicyDTO {

    @Property(value = "discountRules")
    private List<DiscountRuleDTO> discountRules;

    @Property(value = "indexer")
    private int indexer;

    public DiscountPolicyDTO(){
        // For Morphia
    }

    public DiscountPolicyDTO(List<DiscountRuleDTO> discountRules, int indexer) {
        this.discountRules = discountRules;
        this.indexer = indexer;
    }

    public List<DiscountRuleDTO> getDiscountRules() {
        return discountRules == null ? new Vector<>() : discountRules;
    }

    public void setDiscountRules(List<DiscountRuleDTO> discountRules) {
        this.discountRules = discountRules;
    }

    public int getIndexer() {
        return indexer;
    }

    public void setIndexer(int indexer) {
        this.indexer = indexer;
    }
}
