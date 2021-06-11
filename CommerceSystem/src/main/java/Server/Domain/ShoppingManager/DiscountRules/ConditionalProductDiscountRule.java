package Server.Domain.ShoppingManager.DiscountRules;

import Server.DAL.DiscountRuleDTOs.ConditionalProductDiscountRuleDTO;
import Server.DAL.DiscountRuleDTOs.DiscountRuleDTO;
import Server.DAL.PredicateDTOs.ProductPredicateDTO;
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
        return (discount != COMPOSITION_USE_ONLY) ? ("Conditional Product Discount Rule No." + id + ":\n" +
                                                                                            discount + "% Discount - " + productPredicate.toString())
                                                                                        : productPredicate.toString();
    }

    @Override
    public String toString() {
        return (discount != COMPOSITION_USE_ONLY) ? ("Conditional Product Discount Rule No." + id + ":\\n" +
                discount + "% Discount - \\n" + productPredicate.toString())
                : productPredicate.toString();
    }
}
