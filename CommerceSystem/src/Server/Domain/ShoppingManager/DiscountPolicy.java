package Server.Domain.ShoppingManager;

import java.util.Map;

public class DiscountPolicy {
    private int type;

    public DiscountPolicy(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getDiscount(Map<ProductDTO, Integer> shoppingBasket){
        int productsAmount = 0;
        double totalPrice = 0.0;
        for(Map.Entry<ProductDTO, Integer> entry : shoppingBasket.entrySet()){
            productsAmount += entry.getValue();
            totalPrice += entry.getKey().getPrice() * entry.getValue();
        }

        double discount = productsAmount > 3 ? 0.1*totalPrice : 0.0;

        return discount;
    }
}
