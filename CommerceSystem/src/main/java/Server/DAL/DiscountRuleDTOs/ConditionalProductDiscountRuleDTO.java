package Server.DAL.DiscountRuleDTOs;

import Server.DAL.PredicateDTOs.ProductPredicateDTO;
import Server.Domain.ShoppingManager.DiscountRules.ConditionalProductDiscountRule;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

@Embedded
public class ConditionalProductDiscountRuleDTO extends ProductDiscountRuleDTO{

    @Property(value = "productPredicate")
    private ProductPredicateDTO productPredicate;

    public ConditionalProductDiscountRuleDTO() {
        super();
        // For Morphia
    }

    public ConditionalProductDiscountRuleDTO(int id, double discount, int productID, ProductPredicateDTO productPredicate) {
        super(id, discount, productID);
        this.productPredicate = productPredicate;
    }

    public ProductPredicateDTO getProductPredicate() {
        return productPredicate == null ? new ProductPredicateDTO() : productPredicate;
    }

    public void setProductPredicate(ProductPredicateDTO productPredicate) {
        this.productPredicate = productPredicate;
    }

    @Override
    public DiscountRule toConcreteDiscountRule() {
        return new ConditionalProductDiscountRule(this);
    }
}
