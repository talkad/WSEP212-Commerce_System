package Server.DAL;

import Server.Domain.ShoppingManager.DiscountRules.ConditionalCategoryDiscountRule;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Embedded
@BsonDiscriminator("ConditionalCategoryDiscountRuleDTO")

public class ConditionalCategoryDiscountRuleDTO extends CategoryDiscountRuleDTO{

    @Property(value = "categoryPredicate")
    private CategoryPredicateDTO categoryPredicate;

    public ConditionalCategoryDiscountRuleDTO() {
        super();
        // For Morphia
    }

    public ConditionalCategoryDiscountRuleDTO(int id, double discount, String category, CategoryPredicateDTO categoryPredicate) {
        super(id, discount, category);
        this.categoryPredicate = categoryPredicate;
    }

    public CategoryPredicateDTO getCategoryPredicate() {
        return categoryPredicate == null ? new CategoryPredicateDTO() : categoryPredicate;
    }

    public void setCategoryPredicate(CategoryPredicateDTO categoryPredicate) {
        this.categoryPredicate = categoryPredicate;
    }

    @Override
    public DiscountRule toConcreteDiscountRule(){
        return new ConditionalCategoryDiscountRule(this);
    }
}
