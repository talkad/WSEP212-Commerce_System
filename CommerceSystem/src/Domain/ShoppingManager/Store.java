package Domain.ShoppingManager;

public class Store {
    private int storeID;
    private Inventory inventory;

    public Store(int id, Inventory inventory){
        this.storeID = id;
        this.inventory = inventory;
    }

    public int getStoreID() {
        return storeID;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
