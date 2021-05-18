package Server.DAL.DiscountRuleDTOs;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

@Embedded
public class CategoryDiscountRuleDTO extends LeafDiscountRuleDTO{

    @Property(value = "category")
    protected String category;

    public CategoryDiscountRuleDTO() {
        super();
        // For Morphia
    }

    public CategoryDiscountRuleDTO(int id, double discount, String category) {
        super(id, discount);
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
