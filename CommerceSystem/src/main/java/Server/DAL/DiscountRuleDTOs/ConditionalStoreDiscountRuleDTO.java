package Server.DAL.DiscountRuleDTOs;

import Server.DAL.PredicateDTOs.StorePredicateDTO;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

@Embedded
public class ConditionalStoreDiscountRuleDTO extends StoreDiscountRuleDTO{

    @Property(value = "productID")
    private int productID;

    @Property(value = "storePredicate")
    private StorePredicateDTO storePredicate;

    public ConditionalStoreDiscountRuleDTO() {
        super();
        // For Morphia
    }

    public ConditionalStoreDiscountRuleDTO(int id, double discount, int productID, StorePredicateDTO storePredicate) {
        super(id, discount);
        this.productID = productID;
        this.storePredicate = storePredicate;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public StorePredicateDTO getStorePredicate() {
        return storePredicate;
    }

    public void setStorePredicate(StorePredicateDTO storePredicate) {
        this.storePredicate = storePredicate;
    }
}
