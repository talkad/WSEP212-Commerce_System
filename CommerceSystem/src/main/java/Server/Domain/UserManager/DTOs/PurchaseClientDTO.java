package Server.Domain.UserManager.DTOs;


import java.time.LocalDate;

public class PurchaseClientDTO {

    private BasketClientDTO basket;
    private double totalPrice;
    private String purchaseDate;

    public PurchaseClientDTO(BasketClientDTO basket, double totalPrice, LocalDate purchaseDate) {
        this.basket = basket;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate.toString();
    }

    public BasketClientDTO getBasket() {
        return basket;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getPurchaseDate() {
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
