package Server.DAL;

import Server.Domain.ShoppingManager.DiscountRules.CategoryDiscountRule;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Embedded
@BsonDiscriminator("CategoryDiscountRuleDTO")

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

    @Override
    public DiscountRule toConcreteDiscountRule() {
        return new CategoryDiscountRule(this);
    }
}
