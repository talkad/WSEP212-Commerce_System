package Server.Domain.UserManager;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.UserManager.DTOs.PurchaseClientDTO;

import java.util.List;
import java.util.Vector;

public class PurchaseHistory {
    private List<PurchaseClientDTO> purchases;

    public PurchaseHistory() {
        this.purchases = new Vector<>();
    }

    public List<PurchaseClientDTO> getPurchases() {
        return purchases;
    }

    public void addPurchase(List<PurchaseClientDTO> purchase){
        purchases.addAll(purchase);
    }

    public void addSinglePurchase(PurchaseClientDTO purchase){
        purchases.add(purchase);
    }

    public boolean isPurchased(int productID) {

        for(PurchaseClientDTO purchaseDTO: purchases){
            for(ProductClientDTO productDTO: purchaseDTO.getBasket().keySet())
                if(productDTO.getProductID() == productID)
                    return true;
        }

        return false;
    }
}
