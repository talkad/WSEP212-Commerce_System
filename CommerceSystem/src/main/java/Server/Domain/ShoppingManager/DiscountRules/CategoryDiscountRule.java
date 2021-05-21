package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.DAL.DiscountRuleDTOs.CategoryDiscountRuleDTO;
import Server.DAL.DiscountRuleDTOs.DiscountRuleDTO;


import java.util.Map;

public class CategoryDiscountRule extends LeafDiscountRule {
    protected String category;

    public CategoryDiscountRule(String category, double discount){
        super(discount);
        this.category = category;
    }

    public CategoryDiscountRule(CategoryDiscountRuleDTO ruleDTO){
        super(ruleDTO.getDiscount());
        this.setID(ruleDTO.getId());
        this.category = ruleDTO.getCategory();
    }

    @Override
    public DiscountRuleDTO toDTO(){
        return new CategoryDiscountRuleDTO(this.id, this.discount, this.category);
    }

    @Override
    public double calcDiscount(Map<ProductClientDTO, Integer> shoppingBasket) {
        double categoryTotalPrice = 0.0;
        for(Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet())
            if(entry.getKey().getCategories().contains(category))
                categoryTotalPrice += entry.getKey().getPrice() * entry.getValue();

        return categoryTotalPrice * (this.discount / 100);
    }

    @Override
    public String getDescription() {
        return "Simple category discount: Products that belong to category " + category + " have a discount of " + this.discount + "%";
    }
}
