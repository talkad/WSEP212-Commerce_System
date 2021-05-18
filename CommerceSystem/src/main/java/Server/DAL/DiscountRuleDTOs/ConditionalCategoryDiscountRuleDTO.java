package Server.DAL.DiscountRuleDTOs;

import Server.DAL.PredicateDTOs.CategoryPredicateDTO;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

@Embedded
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
        return categoryPredicate;
    }

    public void setCategoryPredicate(CategoryPredicateDTO categoryPredicate) {
        this.categoryPredicate = categoryPredicate;
    }
}
