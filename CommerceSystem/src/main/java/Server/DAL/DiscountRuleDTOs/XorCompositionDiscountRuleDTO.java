package Server.DAL.DiscountRuleDTOs;

import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.DiscountRules.XorCompositionDiscountRule;
import Server.Domain.ShoppingManager.DiscountRules.XorResolveType;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;

@Embedded
public class XorCompositionDiscountRuleDTO extends CompoundDiscountRuleDTO{

    @Property(value = "category")
    private String category;

    @Property(value = "xorResolveType")
    private XorResolveType xorResolveType;

    public XorCompositionDiscountRuleDTO(){
        super();
        // For Morphia
    }

    public XorCompositionDiscountRuleDTO(int id, List<DiscountRuleDTO> discountRules, double discount, String category, XorResolveType xorResolveType) {
        super(id, discountRules, discount);
        this.category = category;
        this.xorResolveType = xorResolveType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public XorResolveType getXorResolveType() {
        return xorResolveType;
    }

    public void setXorResolveType(XorResolveType xorResolveType) {
        this.xorResolveType = xorResolveType;
    }

    @Override
    public DiscountRule toConcreteDiscountRule() {
        return new XorCompositionDiscountRule(this);
    }
}
