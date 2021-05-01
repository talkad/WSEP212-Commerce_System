package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Map;

public abstract class LeafDiscountRule implements DiscountRule {
    protected int id;
    protected double discount;

    public LeafDiscountRule(int id, double discount) {
        this.discount = discount;
        this.id = id;
    }

    public abstract double calcDiscount(Map<ProductDTO, Integer> shoppingBasket);

    public abstract String getDescription();

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int id)
    {
        this.id = id;
    }
}
