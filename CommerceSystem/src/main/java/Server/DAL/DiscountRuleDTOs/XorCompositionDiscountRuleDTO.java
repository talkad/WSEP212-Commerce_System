package Server.DAL.DiscountRuleDTOs;

import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.DiscountRules.XorCompositionDiscountRule;
import Server.Domain.ShoppingManager.DiscountRules.XorResolveType;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;

@Embedded
@BsonDiscriminator("XorCompositionDiscountRuleDTO")

public class XorCompositionDiscountRuleDTO extends CompoundDiscountRuleDTO {


    @Property(value = "xorResolveType")
    private XorResolveType xorResolveType;

    public XorCompositionDiscountRuleDTO(){
        super();
        // For Morphia
    }

    public XorCompositionDiscountRuleDTO(int id, List<DiscountRuleDTO> discountRules, double discount, XorResolveType xorResolveType) {
        super(id, discountRules, discount);

        this.xorResolveType = xorResolveType;
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
