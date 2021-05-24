package Server.Domain.ShoppingManager.PurchaseRules;

import Server.DAL.PredicateDTOs.PredicateDTO;
import Server.DAL.PurchaseRuleDTOs.ConditioningCompositionPurchaseRuleDTO;
import Server.DAL.PurchaseRuleDTOs.PurchaseRuleDTO;
import Server.Domain.CommonClasses.Pair;
import Server.Domain.ShoppingManager.Predicates.Predicate;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class ConditioningCompositionPurchaseRule extends CompoundPurchaseRule{
    Map<Predicate, Predicate> conditionsMap;

    public ConditioningCompositionPurchaseRule(List<Predicate> conditions, List<Predicate> impliedConditions) {
        super( null);
        if(conditions == null || impliedConditions == null)
            throw new IllegalArgumentException("Error, one of lists given is null.");
        if(conditions.size() != impliedConditions.size())
            throw new IllegalArgumentException("Error, conditional lists supplied must be of the same size.");

        conditionsMap = new ConcurrentHashMap<>();
        for(int i=0; i<conditions.size(); ++i)
            conditionsMap.put(conditions.get(i), impliedConditions.get(i));
    }

    public ConditioningCompositionPurchaseRule(ConditioningCompositionPurchaseRuleDTO ruleDTO){
        super(ruleDTO.getConcretePurchaseRules());
        this.setID(ruleDTO.getId());

        this.conditionsMap = new ConcurrentHashMap<>();
        List<Pair<PredicateDTO, PredicateDTO>> conditionsList = ruleDTO.getConditionsMap();
        if(conditionsList != null){
            for(Pair<PredicateDTO, PredicateDTO> pair : conditionsList){
                this.conditionsMap.put(pair.getFirst().toConcretePredicate(), pair.getSecond().toConcretePredicate());
            }
        }
    }

    @Override
    public PurchaseRuleDTO toDTO() {
        List<Pair<PredicateDTO, PredicateDTO>> conditionsList = new Vector<>();
        for(Predicate key : this.conditionsMap.keySet()){
            conditionsList.add(new Pair<>(key.toDTO(), this.conditionsMap.get(key).toDTO()));
        }
        return new ConditioningCompositionPurchaseRuleDTO(this.id, this.getPurchaseRulesDTO(), conditionsList);
    }

    @Override
    public boolean isValidPurchase(Map<ProductClientDTO, Integer> shoppingBasket) {
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
