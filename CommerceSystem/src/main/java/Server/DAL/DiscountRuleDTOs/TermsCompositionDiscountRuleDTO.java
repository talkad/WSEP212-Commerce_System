package Server.DAL.DiscountRuleDTOs;

import Server.DAL.PredicateDTOs.PredicateDTO;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.DiscountRules.TermsCompositionDiscountRule;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;
import java.util.Vector;

@Embedded
public class TermsCompositionDiscountRuleDTO extends CompoundDiscountRuleDTO{

    @Property(value = "category")
    private String category;

    @Property(value = "predicates")
    private List<PredicateDTO> predicates;

    public TermsCompositionDiscountRuleDTO() {
        super();
        // For Morphia
    }

    public TermsCompositionDiscountRuleDTO(int id, List<DiscountRuleDTO> discountRules, double discount, String category, List<PredicateDTO> predicates) {
        super(id, discountRules, discount);
        this.category = category;
        this.predicates = predicates;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<PredicateDTO> getPredicates() {
        return predicates == null ? new Vector<>() : predicates;
    }

    public void setPredicates(List<PredicateDTO> predicates) {
        this.predicates = predicates;
    }


    @Override
    public DiscountRule toConcreteDiscountRule() {
        return new TermsCompositionDiscountRule(this);
    }
}
