package Server.Domain.UserManager.DTOs;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;

import java.util.Collection;
import java.util.List;

public class BasketClientDTO {

    private int storeID;
    private String storeName;
    private List<ProductClientDTO> productsDTO;
    private Collection<Integer> amounts;

    public BasketClientDTO(int storeID, String storeName, List<ProductClientDTO> productsDTO, Collection<Integer> amounts) {
        this.storeID = storeID;
        this.storeName = storeName;
        this.productsDTO = productsDTO;
        this.amounts = amounts;
    }

    public int getStoreID() {
        return storeID;
    }

    public String getStoreName() {
        return storeName;
    }

    public List<ProductClientDTO> getProductsDTO() {
        return productsDTO;
    }

    public Collection<Integer> getAmounts() {
        return amounts;
    }
}
