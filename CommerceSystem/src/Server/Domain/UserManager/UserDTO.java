package Server.Domain.UserManager;

import java.util.List;
import java.util.Map;

public class UserDTO {
    private String name;
    private String password;
    private Map<Integer, List<Permissions>> storesManaged;
    private List<Integer> storesOwned;
    private ShoppingCart shoppingCart;
    private PurchaseHistory purchaseHistory;
    private Appointment appointments;

    public UserDTO(String name, String password, Map<Integer, List<Permissions>> storesManaged, List<Integer> storesOwned, ShoppingCart shoppingCart, PurchaseHistory purchaseHistory, Appointment appointments) {
        this.name = name;
        this.password = password;
        this.storesManaged = storesManaged;
        this.storesOwned = storesOwned;
        this.shoppingCart = shoppingCart;
        this.purchaseHistory = purchaseHistory;
        this.appointments = appointments;
    }

    public UserDTO(String name, Map<Integer, List<Permissions>> storesManaged, List<Integer> storesOwned, ShoppingCart shoppingCart, PurchaseHistory purchaseHistory, Appointment appointments) {
        this.name = name;
        this.storesManaged = storesManaged;
        this.storesOwned = storesOwned;
        this.shoppingCart = shoppingCart;
        this.purchaseHistory = purchaseHistory;
        this.appointments = appointments;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Map<Integer, List<Permissions>> getStoresManaged() {
        return storesManaged;
    }

    public List<Integer> getStoresOwned() {
        return storesOwned;
    }

    public ShoppingCart getShoppingCart() { return shoppingCart; }

    public PurchaseHistory getPurchaseHistory() {
        return purchaseHistory;
    }

    public Appointment getAppointments() {
        return appointments;
    }
}
