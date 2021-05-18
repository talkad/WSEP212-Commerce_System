package Server.Domain.ShoppingManager.PurchaseRules;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.Map;

public interface PurchaseRule {
    boolean isValidPurchase(Map<ProductClientDTO, Integer> shoppingBasket);
    int getID();
    String getDescription();
    void setID(int id);
}
