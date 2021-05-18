package Server.Domain.UserManager.DTOs;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.time.LocalDate;
import java.util.Map;

public class PurchaseClientDTO {

    private Map<ProductClientDTO, Integer> basket;
    private double totalPrice;
    private LocalDate purchaseDate;

    public PurchaseClientDTO(Map<ProductClientDTO, Integer> basket, double totalPrice, LocalDate purchaseDate) {
        this.basket = basket;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
    }

    public Map<ProductClientDTO, Integer> getBasket() {
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
