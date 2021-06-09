package Server.DAL.PredicateDTOs;

import Server.Domain.ShoppingManager.Predicates.Predicate;
import Server.Domain.ShoppingManager.Predicates.ProductPredicate;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Embedded
@BsonDiscriminator("ProductPredicateDTO")

public class ProductPredicateDTO implements PredicateDTO{

    @Property(value = "productID")
    int productID;

    @Property(value = "minUnits")
    int minUnits;

    @Property(value = "maxUnits")
    int maxUnits;

    public ProductPredicateDTO() {
        // For Morphia
    }

    public ProductPredicateDTO(int productID, int minUnits, int maxUnits) {
        this.productID = productID;
        this.minUnits = minUnits;
        this.maxUnits = maxUnits;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getMinUnits() {
        return minUnits;
    }

    public void setMinUnits(int minUnits) {
        this.minUnits = minUnits;
    }

    public int getMaxUnits() {
        return maxUnits;
    }

    public void setMaxUnits(int maxUnits) {
        this.maxUnits = maxUnits;
    }

    @Override
    public Predicate toConcretePredicate() {
        return new ProductPredicate(this);
    }
}
