package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class CompoundDiscountRule implements DiscountRule {
    protected final static double COMPOSITION_USE_ONLY = -100;
    protected int id;
    protected List<DiscountRule> discountRules;
    protected double discount;

    public CompoundDiscountRule(int id, double discount,  List<DiscountRule> policyRules){
        this.discountRules = (policyRules == null) ? Collections.synchronizedList(new LinkedList<>()) : Collections.synchronizedList(policyRules);
        this.id = id;
        this.discount = discount;
    }

    public void add(DiscountRule discountRule) { discountRules.add(discountRule); }

    public void remove(DiscountRule discountRule){
        discountRules.remove(discountRule);
    }

    @Override
    public abstract double calcDiscount(Map<ProductDTO, Integer> shoppingBasket);

    @Override
    public abstract String getDescription();

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setDiscount(double discount) {
        this.discount = discount;
    }

    @Override
    public void setID(int id)
    {
        this.id = id;
    }
}
