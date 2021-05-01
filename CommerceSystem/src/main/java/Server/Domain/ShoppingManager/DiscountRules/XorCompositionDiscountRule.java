package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.ProductDTO;

import java.util.List;
import java.util.Map;

public class XorCompositionDiscountRule extends CompoundDiscountRule {
    private String category;
    private XorResolveType xorResolveType;

    public XorCompositionDiscountRule(int id, String category, double discount, List<DiscountRule> policyRules, XorResolveType xorResolveType) {
        super(id, discount, policyRules);
        this.category = category;
        this.xorResolveType = xorResolveType;
    }

    @Override
    public double calcDiscount(Map<ProductDTO, Integer> shoppingBasket) {
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
