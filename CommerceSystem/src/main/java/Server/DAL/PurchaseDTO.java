package Server.DAL;

import Server.Domain.CommonClasses.Pair;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;

@Embedded
public class PurchaseDTO {

    @Property(value = "basket")
    private List<Pair<ProductDTO, Integer>> basket;

    @Property(value = "totalPrice")
    private double totalPrice;

    @Property(value = "purchaseDate")
    private String purchaseDate;

    public PurchaseDTO(){
        // For Morphia
    }

    public PurchaseDTO(List<Pair<ProductDTO, Integer>> basket, double totalPrice, String purchaseDate) {
        this.basket = basket;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
    }

    public List<Pair<ProductDTO, Integer>> getBasket() {
        return basket;
    }

    public void setBasket(List<Pair<ProductDTO, Integer>> basket) {
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
}
