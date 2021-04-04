package Server.Domain.UserManager;

import java.util.List;
import java.util.Vector;

public class PurchaseHistory {
    private List<Purchase> purchases;

    public PurchaseHistory() {
        this.purchases = new Vector<>();
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }
}
