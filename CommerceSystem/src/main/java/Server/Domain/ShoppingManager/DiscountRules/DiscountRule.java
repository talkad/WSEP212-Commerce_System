package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.DAL.DiscountRuleDTOs.DiscountRuleDTO;


import java.util.Map;

public interface DiscountRule {

    double calcDiscount(Map<ProductClientDTO, Integer> shoppingBasket);

    int getID();

    String getDescription();

    void setDiscount(double discount);

    void setID(int id);

    DiscountRuleDTO toDTO();
}
