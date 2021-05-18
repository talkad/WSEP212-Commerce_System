package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.Predicates.CategoryPredicate;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.Map;

public class ConditionalCategoryDiscountRule extends CategoryDiscountRule {
    private CategoryPredicate categoryPredicate;

    public ConditionalCategoryDiscountRule(String category, double discount, CategoryPredicate categoryPredicate) {
        super(category, discount);
        this.categoryPredicate = categoryPredicate;
    }

    @Override
    public double calcDiscount(Map<ProductClientDTO, Integer> shoppingBasket) {
        if(categoryPredicate.isValid(shoppingBasket)) {
            double categoryTotalPrice = 0.0;
            for (Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet())
                if (entry.getKey().getCategories().contains(category))
                    categoryTotalPrice += entry.getKey().getPrice() * entry.getValue();
            return categoryTotalPrice * (this.discount / 100);
        }
        else
            return 0.0;
    }

    @Override
    public String getDescription() {
        return "Conditional category discount: Products that belong to category " + category + " have a discount of " + discount + "%";
    }
}
