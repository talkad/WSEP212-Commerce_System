package Domain.ShoppingManager;

public class Store {
    private int storeID;
    private String name;
    private Inventory inventory;
    private boolean isActiveStore;
    private DiscountPolicy discountPolicy;
    private PurchasePolicy purchasePolicy;

    public Store(int id, String name, Inventory inventory,
                 DiscountPolicy discountPolicy, PurchasePolicy purchasePolicy){
        this.name = name;
        this.storeID = id;
        this.inventory = inventory;
        this.isActiveStore = true;
        this.discountPolicy = discountPolicy;
        this.purchasePolicy = purchasePolicy;
    }

    public int getStoreID() {
        return storeID;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
