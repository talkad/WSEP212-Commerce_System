package Server.Domain.UserManager;

import java.util.List;

public class UserDTO {
    private String name;
    private String password;
    private List<String> storesManaged;
    private List<String> storesOwned;
    private ShoppingCart shoppingCart;
    private PurchaseHistory purchaseHistory;

    public UserDTO(String name, String password, List<String> storesManaged, List<String> storesOwned, ShoppingCart shoppingCart, PurchaseHistory purchaseHistory) {
        this.name = name;
        this.password = password;
        this.storesManaged = storesManaged;
        this.storesOwned = storesOwned;
        this.shoppingCart = shoppingCart;
        this.purchaseHistory = purchaseHistory;
    }

    public UserDTO(String name, List<String> storesManaged, List<String> storesOwned, ShoppingCart shoppingCart, PurchaseHistory purchaseHistory) {
        this.name = name;
        this.storesManaged = storesManaged;
        this.storesOwned = storesOwned;
        this.shoppingCart = shoppingCart;
        this.purchaseHistory = purchaseHistory;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getStoresManaged() {
        return storesManaged;
    }

    public List<String> getStoresOwned() {
        return storesOwned;
    }

    public ShoppingCart getShoppingCart() { return shoppingCart; }

    public PurchaseHistory getPurchaseHistory() {
        return purchaseHistory;
    }
}
