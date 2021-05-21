package Server.Domain.ShoppingManager.DTOs;


import java.util.List;

public class StoreClientDTO {

    private int storeID;
    private String storeName;
    private List<ProductClientDTO> products;

    public StoreClientDTO(int storeID, String storeName, List<ProductClientDTO> products){
        this.storeID = storeID;
        this.storeName = storeName;
        this.products = products;
    }

    public String getStoreName() {
        return storeName;
    }
}
