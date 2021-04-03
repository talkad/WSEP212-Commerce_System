package Server.Domain.UserManager;

import java.time.LocalDate;

public class PurchaseDTO {

    private ShoppingCart cart;
    private double totalPrice;
    private LocalDate purchaseDate;

    public PurchaseDTO(ShoppingCart cart, double totalPrice, LocalDate purchaseDate) {
        this.cart = cart;
        this.totalPrice = totalPrice;
        this.purchaseDate = purchaseDate;
    }

    @Override
    public String toString() {
        return "PurchaseDTO{" + "\n" +
                "cart=" + cart.toString() + "\n" +
                ", totalPrice=" + totalPrice + "\n" +
                ", purchaseDate=" + purchaseDate.toString() + "\n" +
                '}';
    }
}
