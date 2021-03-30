package Domain.ShoppingManager;

import Domain.CommonClasses.Response;

public class Store {
    private int storeID;
    private String name;
    private Inventory inventory;
    private boolean isActiveStore;
    private DiscountPolicy discountPolicy;
    private PurchasePolicy purchasePolicy;

    public Store(int id, String name, DiscountPolicy discountPolicy, PurchasePolicy purchasePolicy){
        this.name = name;
        this.storeID = id;
        this.inventory = new Inventory();
        this.isActiveStore = true;
        this.discountPolicy = discountPolicy;
        this.purchasePolicy = purchasePolicy;
    }

    public void addProduct(Product product, int amount){
        inventory.addProducts(product, amount);
    }

    public Response<Boolean> removeProduct(Product product, int amount){
        return inventory.removeProducts(product, amount);
    }

    public String getName() {
        return name;
    }

    public int getStoreID() {
        return storeID;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean isActiveStore() {
        return isActiveStore;
    }

    public DiscountPolicy getDiscountPolicy() {
        return discountPolicy;
    }

    public PurchasePolicy getPurchasePolicy() {
        return purchasePolicy;
    }
}
