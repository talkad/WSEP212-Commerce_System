package Server.Domain.ShoppingManager.DiscountRules;

import Server.Domain.ShoppingManager.Predicates.Predicate;
import Server.Domain.ShoppingManager.ProductDTO;

import java.util.List;
import java.util.Map;

public class TermsCompositionDiscountRule extends CompoundDiscountRule {
    private String category;
    private List<Predicate> predicates;

    public TermsCompositionDiscountRule(int id, double discount, String category, List<Predicate> predicates) {
        super(id, discount, null);
        this.category = category;
        this.predicates = predicates;
    }

    @Override
    public double calcDiscount(Map<ProductDTO, Integer> shoppingBasket) {
        double totalPriceToDiscount = 0.0;
        for(Predicate predicate : predicates)
            if(!predicate.isValid(shoppingBasket))
                return 0;

       for(Map.Entry<ProductDTO, Integer> entry : shoppingBasket.entrySet())
           if(entry.getKey().getCategories().contains(category))
               totalPriceToDiscount += entry.getKey().getPrice() * entry.getValue();

      return totalPriceToDiscount * (discount / 100);
    }

    @Override
    public String getDescription() {
        return "Terms Composition: " + id;
    }
}
