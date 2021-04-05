package Server.Domain.UserManager;

import java.util.List;
import java.util.Vector;

public class PurchaseHistory {
    private List<PurchaseDTO> purchases;

    public PurchaseHistory() {
        this.purchases = new Vector<>();
    }

    public List<PurchaseDTO> getPurchases() {
        return purchases;
    }
}
