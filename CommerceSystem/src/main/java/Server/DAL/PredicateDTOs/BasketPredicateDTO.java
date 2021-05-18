package Server.DAL.PredicateDTOs;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;

@Embedded
public class BasketPredicateDTO implements PredicateDTO{

    @Property(value = "minUnits")
    private int minUnits;

    @Property(value = "maxUnits")
    private int maxUnits;

    @Property(value = "minPrice")
    private double minPrice;

    @Property(value = "basketPredicates")
    private List<PredicateDTO> basketPredicates;

    public BasketPredicateDTO() {
        // For Morphia
    }

    public BasketPredicateDTO(int minUnits, int maxUnits, double minPrice, List<PredicateDTO> basketPredicates) {
        this.minUnits = minUnits;
        this.maxUnits = maxUnits;
        this.minPrice = minPrice;
        this.basketPredicates = basketPredicates;
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

    public List<PredicateDTO> getBasketPredicates() {
        return basketPredicates;
    }

    public void setBasketPredicates(List<PredicateDTO> basketPredicates) {
        this.basketPredicates = basketPredicates;
    }
}
