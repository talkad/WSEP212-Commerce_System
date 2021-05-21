package Server.DAL;

import Server.Domain.CommonClasses.Pair;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;

@Embedded
public class ShoppingBasketDTO {

    @Property(value = "storeID")
    private int storeID;

    @Property(value = "products")
    // map product to the amount of it
    // TODO must be converted to vector in domain layer
    private List<Pair<ProductDTO, Integer>> products;

    @Property(value = "totalPrice")
    private double totalPrice;

    public ShoppingBasketDTO(){
        // For Morphia
    }

    public ShoppingBasketDTO(int storeID, List<Pair<ProductDTO, Integer>> products, double totalPrice) {
        this.storeID = storeID;
        this.products = products;
        this.totalPrice = totalPrice;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }

    public List<Pair<ProductDTO, Integer>> getProducts() {
        return products;
    }

    public void setProducts(List<Pair<ProductDTO, Integer>> products) {
        this.products = products;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
