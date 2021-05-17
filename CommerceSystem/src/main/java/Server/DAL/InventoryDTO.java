package Server.DAL;

import Server.Domain.CommonClasses.Pair;
import Server.Domain.ShoppingManager.Product;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;
import java.util.Vector;

@Embedded
public class InventoryDTO {

    @Property(value = "amounts")
    // map productID to the amount of it
    private List<Pair<Integer, Integer>> amounts;

    @Property(value = "products")
    // map productID to Product
    // TODO must be converted to vector in domain layer
    private List<Product> products;

    public InventoryDTO(){
        // For Morphia
    }

    public List<Pair<Integer, Integer>> getAmounts() {
        return amounts;
    }

    public void setAmounts(List<Pair<Integer, Integer>> amounts) {
        this.amounts = new Vector<>(amounts);
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
