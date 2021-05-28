package Server.DAL;

import Server.DAL.PairDTOs.ProductIntPair;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Reference;

import java.util.List;
import java.util.Vector;

@Embedded
public class InventoryDTO {

    @Property(value = "products")
    // map product to the amount of it
    // TODO must be converted to vector in domain layer
    private List<ProductIntPair> products;

    public InventoryDTO(){
        // For Morphia
    }

    public InventoryDTO(List<ProductIntPair> products) {
        this.products = products;
    }

    public List<ProductIntPair> getProducts() {
        return products == null ? new Vector<>() : products;
    }

    public void setProducts(List<ProductIntPair> products) {
        this.products = new Vector<>(products);
    }
}
