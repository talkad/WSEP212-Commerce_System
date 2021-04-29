package Server.Domain.ShoppingManager;


import java.util.List;

public class StoreDTO {

    private int storeID;
    private String storeName;
    private List<ProductDTO> products;

    public StoreDTO(int storeID, String storeName, List<ProductDTO> products){
        this.storeID = storeID;
        this.storeName = storeName;
        this.products = products;
    }

    public String getStoreName() {
        return storeName;
    }
}
