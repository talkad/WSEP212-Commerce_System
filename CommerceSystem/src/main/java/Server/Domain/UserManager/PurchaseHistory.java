package Server.Domain.UserManager;

import Server.DAL.DomainDTOs.PurchaseDTO;
import Server.DAL.DomainDTOs.PurchaseHistoryDTO;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.UserManager.DTOs.PurchaseClientDTO;


import java.util.List;
import java.util.Vector;

public class PurchaseHistory {
    private List<PurchaseClientDTO> purchases;

    public PurchaseHistory() {
        this.purchases = new Vector<>();
    }

    public PurchaseHistory(PurchaseHistoryDTO purchaseHistoryDTO){
        this.purchases = new Vector<>();
        List<PurchaseDTO> purchasesList = purchaseHistoryDTO.getPurchases();
        if(purchases != null){
            for(PurchaseDTO purchaseDTO : purchasesList){
                this.purchases.add(new PurchaseClientDTO(purchaseDTO));
            }
        }
    }

    public List<PurchaseClientDTO> getPurchases() {
        return purchases;
    }

    public PurchaseHistoryDTO toDTO(){
        List<PurchaseDTO> purchasesList = new Vector<>();

        for(PurchaseClientDTO purchaseDTO : this.purchases){
            purchasesList.add(purchaseDTO.toDTO());
        }

        return new PurchaseHistoryDTO(purchasesList);
    }

    public void addPurchase(List<PurchaseClientDTO> purchase){
        purchases.addAll(purchase);
    }

    public void addSinglePurchase(PurchaseClientDTO purchase){
        purchases.add(purchase);
    }

    public boolean isPurchased(int productID) {

        for(PurchaseClientDTO purchaseDTO: purchases){
            for(ProductClientDTO productDTO: purchaseDTO.getBasket().getProductsDTO())
                if(productDTO.getProductID() == productID)
                    return true;
        }

        return false;
    }
}
