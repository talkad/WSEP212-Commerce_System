package Server.DAL;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;

import java.util.List;

@Embedded
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
        return purchases;
    }

    public void setPurchases(List<PurchaseDTO> purchases) {
        this.purchases = purchases;
    }
}
