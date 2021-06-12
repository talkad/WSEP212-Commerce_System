package Server.DAL.DomainDTOs;

import Server.DAL.PairDTOs.IntPermsListPair;
import Server.Domain.UserManager.UserStateEnum;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

import java.util.List;
import java.util.Vector;

@Entity(value = "users")
public class UserDTO {

    @Property(value = "state")
    private UserStateEnum state;

    @Property(value = "storesOwned")
    private List<Integer> storesOwned;

    @Property(value = "storesManaged")
    private List<IntPermsListPair> storesManaged;

    @Id
    @Property(value = "name")
    private String name;

    @Property(value = "shoppingCart")
    private ShoppingCartDTO shoppingCart;

    @Property(value = "purchaseHistory")
    private PurchaseHistoryDTO purchaseHistory;

    @Property(value = "appointments")
    private AppointmentDTO appointments;

    //TODO verify value of key in map
    @Property(value = "offers")
    private List<OfferDTO> offers;

    @Property(value = "pendingMessages")
    private PendingMessagesDTO pendingMessages;

    public UserDTO() {
        // For Morphia
    }

    public UserDTO(UserStateEnum state, List<Integer> storesOwned, List<IntPermsListPair> storesManaged, String name, ShoppingCartDTO shoppingCart, PurchaseHistoryDTO purchaseHistory, AppointmentDTO appointments, List<OfferDTO> offers, PendingMessagesDTO pendingMessages) {
        this.state = state;
        this.storesOwned = storesOwned;
        this.storesManaged = storesManaged;
        this.name = name;
        this.shoppingCart = shoppingCart;
        this.purchaseHistory = purchaseHistory;
        this.appointments = appointments;
        this.offers = offers;
        this.pendingMessages = pendingMessages;
    }

    public UserStateEnum getState() {
        return state;
    }

    public void setState(UserStateEnum state) {
        this.state = state;
    }

    public List<Integer> getStoresOwned() {
        return storesOwned == null ? new Vector<>() : storesOwned;
    }

    public void setStoresOwned(List<Integer> storesOwned) {
        this.storesOwned = storesOwned;
    }

    public List<IntPermsListPair> getStoresManaged() {
        return storesManaged == null ? new Vector<>() : storesManaged;
    }

    public void setStoresManaged(List<IntPermsListPair> storesManaged) {
        this.storesManaged = storesManaged;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ShoppingCartDTO getShoppingCart() {
        return shoppingCart == null ? new ShoppingCartDTO() : shoppingCart;
    }

    public void setShoppingCart(ShoppingCartDTO shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public PurchaseHistoryDTO getPurchaseHistory() {
        return purchaseHistory == null ? new PurchaseHistoryDTO() : purchaseHistory;
    }

    public void setPurchaseHistory(PurchaseHistoryDTO purchaseHistory) {
        this.purchaseHistory = purchaseHistory;
    }

    public AppointmentDTO getAppointments() {
        return appointments == null ? new AppointmentDTO() : appointments;
    }

    public void setAppointments(AppointmentDTO appointments) {
        this.appointments = appointments;
    }

    public List<OfferDTO> getOffers() {
        return offers == null ? new Vector<>() : offers;
    }

    public void setOffers(List<OfferDTO> offers) {
        this.offers = offers;
    }

    public PendingMessagesDTO getPendingMessages() {
        return pendingMessages == null ? new PendingMessagesDTO() : pendingMessages;
    }

    public void setPendingMessages(PendingMessagesDTO pendingMessages) {
        this.pendingMessages = pendingMessages;
    }
}
