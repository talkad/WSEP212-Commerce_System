package Server.DAL;

import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;
import java.util.Vector;

@Embedded
@BsonDiscriminator("CompoundDiscountRuleDTO")

public class CompoundDiscountRuleDTO extends DiscountRuleDTO {

    @Property(value = "id")
    protected int id;

    @Property(value = "discountRules")
    protected List<DiscountRuleDTO> discountRules;

    @Property(value = "discount")
    protected double discount;

    public CompoundDiscountRuleDTO(){
        // For Morphia
    }

    public CompoundDiscountRuleDTO(int id, List<DiscountRuleDTO> discountRules, double discount) {
        this.id = id;
        this.discountRules = discountRules;
        this.discount = discount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DiscountRuleDTO> getDiscountRules() {
        return discountRules == null ? new Vector<>() : discountRules;
    }

    public void setDiscountRules(List<DiscountRuleDTO> discountRules) {
        this.discountRules = discountRules;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public List<DiscountRule> getConcreteDiscountRules(){
        List<DiscountRule> concreteDiscountRules = new Vector<>();
        for(DiscountRuleDTO discountRuleDTO : this.getDiscountRules()){
            concreteDiscountRules.add(discountRuleDTO.toConcreteDiscountRule());
        }
        return concreteDiscountRules;
    }
}
