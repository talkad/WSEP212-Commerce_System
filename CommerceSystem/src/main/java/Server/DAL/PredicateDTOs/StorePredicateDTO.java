package Server.DAL.PredicateDTOs;

import Server.Domain.ShoppingManager.Predicates.Predicate;
import Server.Domain.ShoppingManager.Predicates.StorePredicate;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Embedded
@BsonDiscriminator("StorePredicateDTO")

public class StorePredicateDTO implements PredicateDTO{

    @Property(value = "minUnits")
    private int minUnits;

    @Property(value = "maxUnits")
    private int maxUnits;

    @Property(value = "minPrice")
    private double minPrice;

    public StorePredicateDTO() {
        // For Morphia
    }

    public StorePredicateDTO(int minUnits, int maxUnits, double minPrice) {
        this.minUnits = minUnits;
        this.maxUnits = maxUnits;
        this.minPrice = minPrice;
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

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    @Override
    public Predicate toConcretePredicate() {
        return new StorePredicate(this);
    }
}
