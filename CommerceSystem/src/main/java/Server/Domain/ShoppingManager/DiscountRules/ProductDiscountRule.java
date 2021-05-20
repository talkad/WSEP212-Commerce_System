package Server.Domain.ShoppingManager.DiscountRules;

import Server.DAL.DiscountRuleDTOs.DiscountRuleDTO;
import Server.DAL.DiscountRuleDTOs.ProductDiscountRuleDTO;
import Server.Domain.ShoppingManager.ProductDTO;

import java.util.Map;

public class ProductDiscountRule extends LeafDiscountRule {
    protected int productID;

    public ProductDiscountRule(int productID, double discount){
        super(discount);
        this.productID = productID;
    }

    public ProductDiscountRule(ProductDiscountRuleDTO ruleDTO){
        super(ruleDTO.getDiscount());
        this.setID(ruleDTO.getId());
        this.productID = ruleDTO.getProductID();
    }

    @Override
    public DiscountRuleDTO toDTO() {
        return new ProductDiscountRuleDTO(this.id, this.discount, this.productID);
    }
    @Override
    public double calcDiscount(Map<ProductDTO, Integer> shoppingBasket) {
        for(Map.Entry<ProductDTO, Integer> entry : shoppingBasket.entrySet())
            if(entry.getKey().getProductID() == productID)
                return (entry.getValue() * entry.getKey().getPrice()) * (this.discount / 100);

        return 0.0;
    }

    @Override
    public String getDescription() {
        return "Simple Product discount: ProductID - " + productID + " with a discount of " + this.discount + "%";
    }


}
