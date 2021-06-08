package Server.Domain.ShoppingManager.DiscountRules;

import Server.DAL.ConditionalProductDiscountRuleDTO;
import Server.DAL.DiscountRuleDTO;
import Server.DAL.ProductPredicateDTO;
import Server.Domain.ShoppingManager.Predicates.ProductPredicate;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.Map;

public class ConditionalProductDiscountRule extends ProductDiscountRule {
    private ProductPredicate productPredicate;

    public ConditionalProductDiscountRule(int productID, double discount, ProductPredicate productPredicate) {
        super(productID, discount);
        this.productPredicate = productPredicate;
    }

    public ConditionalProductDiscountRule(ConditionalProductDiscountRuleDTO ruleDTO){
        super(ruleDTO.getProductID(), ruleDTO.getDiscount());
        this.setID(ruleDTO.getId());
        this.productPredicate = (ProductPredicate) ruleDTO.getProductPredicate().toConcretePredicate();
    }

    @Override
    public DiscountRuleDTO toDTO(){
        return new ConditionalProductDiscountRuleDTO(this.id, this.discount, this.productID, (ProductPredicateDTO) this.productPredicate.toDTO());
    }

    @Override
    public double calcDiscount(Map<ProductClientDTO, Integer> shoppingBasket) {
        for(Map.Entry<ProductClientDTO, Integer> entry : shoppingBasket.entrySet()) {
            if (entry.getKey().getProductID() == productID) {
                return productPredicate.isValid(shoppingBasket) ? (entry.getValue() * entry.getKey().getPrice()) * (discount / 100) : 0.0;
            }
        }
        return 0.0;
    }

    @Override
    public String getDescription() {
        return "Conditional Product discount: ProductID - " + productID + " with a discount of " + discount + "%";
    }
}
