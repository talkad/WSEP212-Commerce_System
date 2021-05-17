package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Map;

public abstract class LeafDiscountRule implements DiscountRule {
    protected final static int NOT_SET = -1;
    protected int id;
    protected double discount;

    public LeafDiscountRule(double discount) {
        this.discount = discount;
        this.id = NOT_SET;
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
