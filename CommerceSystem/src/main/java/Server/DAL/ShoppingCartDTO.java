package Server.DAL;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;
import java.util.Vector;

@Embedded
@BsonDiscriminator("ShoppingCartDTO")

public class ShoppingCartDTO {

    @Property(value = "baskets")
    private List<ShoppingBasketDTO> baskets;

    public ShoppingCartDTO(){
        // For Morphia
    }

    public ShoppingCartDTO(List<ShoppingBasketDTO> baskets) {
        this.baskets = baskets;
    }

    public List<ShoppingBasketDTO> getBaskets() {
        return baskets == null ? new Vector<>() : baskets;
    }

    public void setBaskets(List<ShoppingBasketDTO> baskets) {
        this.baskets = baskets;
    }
}
