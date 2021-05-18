package Server.DAL.PredicateDTOs;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

@Embedded
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
}
