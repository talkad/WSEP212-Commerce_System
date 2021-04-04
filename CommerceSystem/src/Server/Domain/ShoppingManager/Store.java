package Server.Domain.ShoppingManager;

import Server.Domain.CommonClasses.Rating;
import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.PurchaseDTO;
import Server.Domain.UserManager.ShoppingBasket;
import Server.Domain.UserManager.ShoppingCart;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    private List<PurchaseDTO> purchaseHistory;
    private ReentrantReadWriteLock readWriteLock;


    public Store(int id, String name,String ownerName, DiscountPolicy discountPolicy, PurchasePolicy purchasePolicy){
        this.name = name;
        this.storeID = id;
        this.ownerName = ownerName;
        this.inventory = new Inventory();
        this.isActiveStore = true;
        this.discountPolicy = discountPolicy;
        this.purchasePolicy = purchasePolicy;
        this.purchaseHistory = new ConcurrentHashMap<>();
        this.rating = new AtomicReference<>(0.0);
        this.numRatings = new AtomicInteger(0);
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    public void addProduct(ProductDTO productDTO, int amount){
        inventory.addProducts(productDTO, amount);
    }

    public Response<Boolean> removeProduct(int productID, int amount){
        return inventory.removeProducts(productID, amount);
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

     public Response<Boolean> purchase(Map<ProductDTO, Integer> shoppingBasket) { // TODO : TAL FIX PURCHASE
        Response<Boolean> result = inventory.removeProducts(productID, amount);

        readWriteLock.writeLock().lock();

        if(!result.isFailure()){
            purchaseHistory.put(getProduct(productID).getResult().getProductDTO(), amount);
        }

        readWriteLock.writeLock().unlock();
        return result;
    }

    public List<PurchaseDTO> getPurchaseHistory() {
        return purchaseHistory;
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

    public Response<Boolean> updateProductInfo(int productID, double newPrice, String newName) {
        return inventory.updateProductInfo(productID, newPrice, newName);
    }

    public Response<Product> getProduct(int productID) {
        return inventory.getProduct(productID);
    }

    public void setActiveStore(boolean activeStore) { isActiveStore = activeStore; }
}
