package Server.DAL;

import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.DiscountRules.ProductDiscountRule;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Embedded
@BsonDiscriminator("ProductDiscountRuleDTO")

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

    @Override
    public DiscountRule toConcreteDiscountRule() {
        return new ProductDiscountRule(this);
    }
}
