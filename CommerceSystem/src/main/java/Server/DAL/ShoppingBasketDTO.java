package Server.DAL;

import Server.DAL.PairDTOs.ProductIntPair;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;
import java.util.Vector;

@Embedded
@BsonDiscriminator("ShoppingBasketDTO")

public class ShoppingBasketDTO {

    @Property(value = "storeID")
    private int storeID;

    @Property(value = "products")
    // map product to the amount of it
    // TODO must be converted to vector in domain layer
    private List<ProductIntPair> products;

    @Property(value = "totalPrice")
    private double totalPrice;

    public ShoppingBasketDTO(){
        // For Morphia
    }

    public ShoppingBasketDTO(int storeID, List<ProductIntPair> products, double totalPrice) {
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

    public List<ProductIntPair> getProducts() {
        return products == null ? new Vector<>() : products;
    }

    public void setProducts(List<ProductIntPair> products) {
        this.products = products;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
