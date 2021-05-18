package Server.DAL.DiscountRuleDTOs;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

@Embedded
public class ProductDiscountRuleDTO extends LeafDiscountRuleDTO{

    @Property(value = "productID")
    protected int productID;

    public ProductDiscountRuleDTO() {
        super();
        // For Morphia
    }

    public ProductDiscountRuleDTO(int id, double discount, int productID) {
        super(id, discount);
        this.productID = productID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }
}
