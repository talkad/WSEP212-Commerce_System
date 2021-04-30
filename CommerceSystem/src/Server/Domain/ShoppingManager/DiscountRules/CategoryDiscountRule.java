package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Map;

public class CategoryDiscountRule extends LeafDiscountRule {
    protected String category;

    public CategoryDiscountRule(int ruleID, String category, double discount){
        super(ruleID, discount);
        this.category = category;
    }
    @Override
    public double calcDiscount(Map<ProductDTO, Integer> shoppingBasket) {
        double categoryTotalPrice = 0.0;
        for(Map.Entry<ProductDTO, Integer> entry : shoppingBasket.entrySet())
            if(entry.getKey().getCategories().contains(category))
                categoryTotalPrice += entry.getKey().getPrice() * entry.getValue();

        return categoryTotalPrice * (this.discount / 100);
    }

    @Override
    public String getDescription() {
        return "Simple category discount: Products that belong to category " + category + " have a discount of " + this.discount + "%";
    }
}
