package Server.Domain.UserManager;

import Server.Domain.ShoppingManager.ProductDTO;

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

    public void addPurchase(List<PurchaseDTO> purchase){
        purchases.addAll(purchase);
    }

    public boolean isPurchased(int productID) {

        for(PurchaseDTO purchaseDTO: purchases){
            for(ProductDTO productDTO: purchaseDTO.getBasket().keySet())
                if(productDTO.getProductID() == productID)
                    return true;
        }

        return false;
    }
}
