package Server.DAL;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;
import java.util.Vector;

@Embedded
@BsonDiscriminator("PurchaseDTO")

public class PurchaseDTO {

    @Property(value = "storeID")
    private int storeID;

    @Property(value = "basket")
    private List<ProductIntPair> basket;

    @Property(value = "totalPrice")
    private double totalPrice;

    @Property(value = "purchaseDate")
    private String purchaseDate;

    public PurchaseDTO(){
        // For Morphia
    }

    public PurchaseDTO(int storeID, List<ProductIntPair> basket, double totalPrice, String purchaseDate) {
        this.storeID = storeID;
        this.basket = basket;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
    }

    public List<ProductIntPair> getBasket() {
        return basket == null ? new Vector<>() : basket;
    }

    public void setBasket(List<ProductIntPair> basket) {
        this.basket = basket;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }
}
