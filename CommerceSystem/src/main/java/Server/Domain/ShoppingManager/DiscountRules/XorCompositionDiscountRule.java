package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.DAL.DiscountRuleDTOs.DiscountRuleDTO;
import Server.DAL.DiscountRuleDTOs.XorCompositionDiscountRuleDTO;
import java.util.List;
import java.util.Map;

public class XorCompositionDiscountRule extends CompoundDiscountRule {
    private String category;
    private XorResolveType xorResolveType;

    public XorCompositionDiscountRule(String category, double discount, List<DiscountRule> policyRules, XorResolveType xorResolveType) {
        super(discount, policyRules);
        this.category = category;
        this.xorResolveType = xorResolveType;
    }

    public XorCompositionDiscountRule(XorCompositionDiscountRuleDTO ruleDTO){
        super(ruleDTO.getDiscount(), ruleDTO.getConcreteDiscountRules());
        this.setID(ruleDTO.getId());
        this.category = ruleDTO.getCategory();
        this.xorResolveType = ruleDTO.getXorResolveType();
    }

    @Override
    public DiscountRuleDTO toDTO(){
        return new XorCompositionDiscountRuleDTO(this.id, this.getDiscountRulesDTO(), this.discount, this.category, this.xorResolveType);
    }

    @Override
    public double calcDiscount(Map<ProductClientDTO, Integer> shoppingBasket) {
        double totalPriceToDiscount = 0.0;
        double discountRes;

        switch (xorResolveType) {
            case FIRST:
                for (DiscountRule discountRule : discountRules)
                    if (discountRule.calcDiscount(shoppingBasket) < 0) {
                        discountRule.setDiscount(discount);
                        totalPriceToDiscount = discountRule.calcDiscount(shoppingBasket);
                        discountRule.setDiscount(COMPOSITION_USE_ONLY);
                        break;
                    }
                break;

            case LOWEST:
                totalPriceToDiscount = Double.POSITIVE_INFINITY;
                for (DiscountRule discountRule : discountRules)
                    if (discountRule.calcDiscount(shoppingBasket) < 0) {
                        discountRule.setDiscount(discount);
                        discountRes = discountRule.calcDiscount(shoppingBasket);
                        discountRule.setDiscount(COMPOSITION_USE_ONLY);
                        if (discountRes < totalPriceToDiscount)
                            totalPriceToDiscount = discountRes;
                    }
                break;

            case HIGHEST:
                totalPriceToDiscount = 0.0;
                for (DiscountRule discountRule : discountRules)
                    if (discountRule.calcDiscount(shoppingBasket) < 0) {
                        discountRule.setDiscount(discount);
                        discountRes = discountRule.calcDiscount(shoppingBasket);
                        discountRule.setDiscount(COMPOSITION_USE_ONLY);
                        if (discountRes > totalPriceToDiscount)
                            totalPriceToDiscount = discountRes;
                    }
                break;
        }

        return totalPriceToDiscount * (discount / 100);
    }

    @Override
    public String getDescription() {
        return "Xor Composition: " + id;
    }
}
