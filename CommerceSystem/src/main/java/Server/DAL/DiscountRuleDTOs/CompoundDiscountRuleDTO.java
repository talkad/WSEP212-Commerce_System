package Server.DAL.DiscountRuleDTOs;

import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;
import java.util.Vector;

@Embedded
public abstract class CompoundDiscountRuleDTO implements DiscountRuleDTO {

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
        return discountRules;
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
        for(DiscountRuleDTO discountRuleDTO : this.discountRules){
            concreteDiscountRules.add(discountRuleDTO.toConcreteDiscountRule());
        }
        return concreteDiscountRules;
    }
}
