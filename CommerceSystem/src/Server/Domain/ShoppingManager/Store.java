package Server.Domain.ShoppingManager;

import Server.Domain.CommonClasses.Rating;
import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.Purchase;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Store {
    private int storeID;
    private String name;
    private String ownerName;
    private Inventory inventory;
    private boolean isActiveStore;
    private DiscountPolicy discountPolicy;
    private PurchasePolicy purchasePolicy;
    private AtomicReference<Double> rating;
    private AtomicInteger numRatings;

    private List<Purchase> purchaseHistory;
    private ReentrantReadWriteLock readWriteLock;


    public Store(int id, String name,String ownerName, DiscountPolicy discountPolicy, PurchasePolicy purchasePolicy){
        this.name = name;
        this.storeID = id;
        this.ownerName = ownerName;
        this.inventory = new Inventory();
        this.isActiveStore = true;
        this.discountPolicy = discountPolicy;
        this.purchasePolicy = purchasePolicy;
        this.purchaseHistory = new LinkedList<>();
        this.rating = new AtomicReference<>(0.0);
        this.numRatings = new AtomicInteger(0);
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

    public void addRating(Rating rate){
        int prevNum;
        Double currentRating;
        Double newRating;

        do {
            currentRating = rating.get();
            prevNum = numRatings.get();
            newRating = (prevNum * currentRating + rate.rate) / (prevNum + 1);

        }while (!rating.compareAndSet(currentRating, newRating));

        numRatings.getAndIncrement();
    }

    public double getRating() {
        return rating.get();
    }

    public String getOwnerName(){
        return ownerName;
    }
}
