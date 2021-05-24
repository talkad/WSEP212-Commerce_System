package Server.DAL.DiscountRuleDTOs;

import Server.Domain.ShoppingManager.DiscountRules.AndCompositionDiscountRule;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;

@Embedded
public class AndCompositionDiscountRuleDTO extends CompoundDiscountRuleDTO {

    @Property(value = "category")
    private String category;

    public AndCompositionDiscountRuleDTO(){
        super();
        // For Morphia
    }

    public AndCompositionDiscountRuleDTO(int id, List<DiscountRuleDTO> discountRules, double discount, String category) {
        super(id, discountRules, discount);
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    @Override
    public DiscountRule toConcreteDiscountRule() {
        AndCompositionDiscountRule rule = new AndCompositionDiscountRule(this.getCategory(), this.getDiscount(), this.getConcreteDiscountRules());
        rule.setID(this.id);
        return rule;
    }
}
