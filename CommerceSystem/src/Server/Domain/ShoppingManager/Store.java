package Server.Domain.ShoppingManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.Purchase;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Store {
    private int storeID;
    private String name;
    private Inventory inventory;
    private boolean isActiveStore;
    private DiscountPolicy discountPolicy;
    private PurchasePolicy purchasePolicy;

    private List<Purchase> purchaseHistory;
    private ReentrantReadWriteLock readWriteLock;


    public Store(int id, String name, DiscountPolicy discountPolicy, PurchasePolicy purchasePolicy){
        this.name = name;
        this.storeID = id;
        this.inventory = new Inventory();
        this.isActiveStore = true;
        this.discountPolicy = discountPolicy;
        this.purchasePolicy = purchasePolicy;
        this.purchaseHistory = new LinkedList<>();
        this.readWriteLock = new ReentrantReadWriteLock();
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

    public Response<Boolean> purchase(Product product, int amount) {
        Response<Boolean> result = inventory.removeProducts(product, amount);

        readWriteLock.writeLock().lock();

        if(!result.isFailure()){
            purchaseHistory.add(new Purchase()); //TODO: Purchase isn't implemented
        }

        readWriteLock.writeLock().unlock();
        return result;
    }

    public List<Purchase> getPurchaseHistory() {
        List<Purchase> history;

        readWriteLock.readLock().lock();
        history = new LinkedList<>(purchaseHistory);
        readWriteLock.readLock().unlock();

        return history;
    }
}
