package Server.DAL.DiscountRuleDTOs;

import Server.DAL.PredicateDTOs.PredicateDTO;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;

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
        return predicates;
    }

    public void setPredicates(List<PredicateDTO> predicates) {
        this.predicates = predicates;
    }
}
