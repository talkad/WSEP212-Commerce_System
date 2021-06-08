package Server.DAL.PredicateDTOs;

import Server.Domain.ShoppingManager.Predicates.CategoryPredicate;
import Server.Domain.ShoppingManager.Predicates.Predicate;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Embedded
@BsonDiscriminator("CategoryPredicateDTO")

public class CategoryPredicateDTO extends PredicateDTO {

    @Property(value = "category")
    private String category;

    @Property(value = "minUnits")
    private int minUnits;

    @Property(value = "maxUnits")
    private int maxUnits;

    public CategoryPredicateDTO(){
        // For Morphia
    }
    public CategoryPredicateDTO(String category, int minUnits, int maxUnits) {
        this.category = category;
        this.minUnits = minUnits;
        this.maxUnits = maxUnits;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
        return new CategoryPredicate(this);
    }
}
