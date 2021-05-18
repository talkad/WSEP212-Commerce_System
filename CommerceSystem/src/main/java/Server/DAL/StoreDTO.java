package Server.DAL;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;

@Entity(value = "stores")
public class StoreDTO {

    @Id
    @Property(value = "storeID")
    private int storeID;

    @Property(value = "name")
    private String name;

    @Property(value = "ownerName")
    private String ownerName;

    @Property(value = "inventory")
    private InventoryDTO inventory;

    @Property(value = "isActiveStore")
    private boolean isActiveStore;

    @Property(value = "discountPolicy")
    private DiscountPolicyDTO discountPolicy;

    @Property(value = "purchasePolicy")
    private PurchasePolicyDTO purchasePolicy;

    @Property(value = "rating")
    private double rating;

    @Property(value = "numRatings")
    private int numRatings;

    @Property(value = "purchaseHistory")
    private PurchaseHistoryDTO purchaseHistory;

    public StoreDTO(){
        // For Morphia
    }

    public StoreDTO(int storeID, String name, String ownerName, InventoryDTO inventory, boolean isActiveStore, DiscountPolicyDTO discountPolicy, PurchasePolicyDTO purchasePolicy, double rating, int numRatings, PurchaseHistoryDTO purchaseHistory) {
        this.storeID = storeID;
        this.name = name;
        this.ownerName = ownerName;
        this.inventory = inventory;
        this.isActiveStore = isActiveStore;
        this.discountPolicy = discountPolicy;
        this.purchasePolicy = purchasePolicy;
        this.rating = rating;
        this.numRatings = numRatings;
        this.purchaseHistory = purchaseHistory;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public InventoryDTO getInventory() {
        return inventory;
    }

    public void setInventory(InventoryDTO inventory) {
        this.inventory = inventory;
    }

    public boolean isActiveStore() {
        return isActiveStore;
    }

    public void setActiveStore(boolean activeStore) {
        isActiveStore = activeStore;
    }

    public DiscountPolicyDTO getDiscountPolicy() {
        return discountPolicy;
    }

    public void setDiscountPolicy(DiscountPolicyDTO discountPolicy) {
        this.discountPolicy = discountPolicy;
    }

    public PurchasePolicyDTO getPurchasePolicy() {
        return purchasePolicy;
    }

    public void setPurchasePolicy(PurchasePolicyDTO purchasePolicy) {
        this.purchasePolicy = purchasePolicy;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public PurchaseHistoryDTO getPurchaseHistory() {
        return purchaseHistory;
    }

    public void setPurchaseHistory(PurchaseHistoryDTO purchaseHistory) {
        this.purchaseHistory = purchaseHistory;
    }
}
