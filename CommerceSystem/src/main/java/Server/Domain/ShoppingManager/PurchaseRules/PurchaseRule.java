package Server.Domain.ShoppingManager.PurchaseRules;

import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Map;

public interface PurchaseRule {
    boolean isValidPurchase(Map<ProductDTO, Integer> shoppingBasket);
    int getID();
    String getDescription();
    void setID(int id);
}
