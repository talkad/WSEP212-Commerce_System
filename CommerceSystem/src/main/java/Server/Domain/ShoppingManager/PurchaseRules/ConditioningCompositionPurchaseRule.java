package Server.Domain.ShoppingManager.PurchaseRules;

import Server.Domain.ShoppingManager.Predicates.Predicate;
import Server.Domain.ShoppingManager.ProductDTO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConditioningCompositionPurchaseRule extends CompoundPurchaseRule{
    Map<Predicate, Predicate> conditionsMap;

    public ConditioningCompositionPurchaseRule(int id, List<Predicate> conditions, List<Predicate> impliedConditions) {
        super(id, null);
        if(conditions == null || impliedConditions == null)
            throw new IllegalArgumentException("Error, one of lists given is null.");
        if(conditions.size() != impliedConditions.size())
            throw new IllegalArgumentException("Error, conditional lists supplied must be of the same size.");

        conditionsMap = new ConcurrentHashMap<>();
        for(int i=0; i<conditions.size(); ++i)
            conditionsMap.put(conditions.get(i), impliedConditions.get(i));
    }

    @Override
    public boolean isValidPurchase(Map<ProductDTO, Integer> shoppingBasket) {
        for(Map.Entry<Predicate, Predicate> entry : conditionsMap.entrySet())
            if(entry.getKey().isValid(shoppingBasket))
                return entry.getValue().isValid(shoppingBasket);

       return true;
    }

    @Override
    public String getDescription() {
        return "Conditioning Composition Purchase Rule " + id;
    }
}
