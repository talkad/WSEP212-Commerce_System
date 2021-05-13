package Server.Domain.UserManager;

import Server.Domain.ShoppingManager.ProductDTO;

import java.time.LocalDate;
import java.util.Map;

public class PurchaseDTO {

    private Map<ProductDTO, Integer> basket;
    private double totalPrice;
    private LocalDate purchaseDate;

    public PurchaseDTO(Map<ProductDTO, Integer> basket, double totalPrice, LocalDate purchaseDate) {
        this.basket = basket;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
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
