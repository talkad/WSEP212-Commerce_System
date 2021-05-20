package Server.DAL;

import Server.Domain.CommonClasses.Pair;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;
import java.util.Vector;

@Embedded
public class InventoryDTO {

    @Property(value = "products")
    // map product to the amount of it
    // TODO must be converted to vector in domain layer
    private List<Pair<ProductDTO, Integer>> products;

    public InventoryDTO(){
        // For Morphia
    }

    public InventoryDTO(List<Pair<ProductDTO, Integer>> products) {
        this.products = products;
    }

    public List<Pair<ProductDTO, Integer>> getProducts() {
        return products;
    }

    public void setProducts(List<Pair<ProductDTO, Integer>> products) {
        this.products = new Vector<>(products);
    }
}
