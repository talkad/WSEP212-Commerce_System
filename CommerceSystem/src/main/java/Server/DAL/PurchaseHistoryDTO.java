package Server.DAL;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;
import java.util.Vector;

@Embedded
@BsonDiscriminator("PurchaseHistoryDTO")

public class PurchaseHistoryDTO {

    @Property(value = "purchases")
    private List<PurchaseDTO> purchases;

    public PurchaseHistoryDTO(){
        // For Morphia
    }

    public PurchaseHistoryDTO(List<PurchaseDTO> purchases) {
        this.purchases = purchases;
    }

    public List<PurchaseDTO> getPurchases() {
        return purchases == null ? new Vector<>() : purchases;
    }

    public void setPurchases(List<PurchaseDTO> purchases) {
        this.purchases = purchases;
    }
}
