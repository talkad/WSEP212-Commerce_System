package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Map;

public interface DiscountRule {

    double calcDiscount(Map<ProductDTO, Integer> shoppingBasket);

    int getID();

    String getDescription();

    void setDiscount(double discount);

    void setID(int id);
}
