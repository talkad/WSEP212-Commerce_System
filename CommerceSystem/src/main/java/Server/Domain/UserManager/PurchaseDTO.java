package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Pair;
import Server.Domain.ShoppingManager.ProductDTO;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class PurchaseDTO {

    private Map<ProductDTO, Integer> basket;
    private double totalPrice;
    private LocalDate purchaseDate;

    public PurchaseDTO(Map<ProductDTO, Integer> basket, double totalPrice, LocalDate purchaseDate) {
        this.basket = basket;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
    }

    // TODO Refactor constructor name, not arg name
    public PurchaseDTO(Server.DAL.PurchaseDTO purchaseDTO){
        // TODO maybe make thread-safe
        this.basket = new HashMap<>();
        this.totalPrice = purchaseDTO.getTotalPrice();
        this.purchaseDate = LocalDate.parse(purchaseDTO.getPurchaseDate());
        List<Pair<Server.DAL.ProductDTO, Integer>> basketList = purchaseDTO.getBasket();
        if(basketList != null){
            for(Pair<Server.DAL.ProductDTO, Integer> pair : basketList){
                //this.basket.put() // TODO sort out if need product or productdto
            }
        }
    }

    public Server.DAL.PurchaseDTO toDTO(){
        // TODO sort out product issue then implement
        return new Server.DAL.PurchaseDTO();
    }

    public Map<ProductDTO, Integer> getBasket() {
        return basket;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    @Override
    public String toString() {
        return "PurchaseDTO{" +
                "basket=" + basket +
                ", totalPrice=" + totalPrice +
                ", purchaseDate=" + purchaseDate +
                '}';
    }
}
