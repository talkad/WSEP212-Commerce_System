package Server.Domain.UserManager;

import Server.DAL.PurchaseHistoryDTO;
import Server.Domain.ShoppingManager.ProductDTO;

import java.util.List;
import java.util.Vector;

public class PurchaseHistory {
    private List<PurchaseDTO> purchases;

    public PurchaseHistory() {
        this.purchases = new Vector<>();
    }

    public PurchaseHistory(PurchaseHistoryDTO purchaseHistoryDTO){
        this.purchases = new Vector<>();
        List<Server.DAL.PurchaseDTO> purchasesList = purchaseHistoryDTO.getPurchases();
        if(purchases != null){
            for(Server.DAL.PurchaseDTO purchaseDTO : purchasesList){
                this.purchases.add(new PurchaseDTO(purchaseDTO));
            }
        }
    }

    public PurchaseHistoryDTO toDTO(){
        List<Server.DAL.PurchaseDTO> purchasesList = new Vector<>();

        for(PurchaseDTO purchaseDTO : this.purchases){
            purchasesList.add(purchaseDTO.toDTO());
        }

        return new PurchaseHistoryDTO(purchasesList);
    }

    public List<PurchaseDTO> getPurchases() {
        return purchases;
    }

    public void addPurchase(List<PurchaseDTO> purchase){
        purchases.addAll(purchase);
    }

    public void addSinglePurchase(PurchaseDTO purchase){
        purchases.add(purchase);
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
