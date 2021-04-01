package Server.Domain.UserManager;

import java.util.LinkedList;
import java.util.List;

public class PurchaseHistory {
    private List<Purchase> purchases;

    public PurchaseHistory() {
        this.purchases = new LinkedList<>();
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }
}
