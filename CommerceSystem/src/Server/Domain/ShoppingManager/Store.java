package Server.Domain.ShoppingManager;

import Server.Domain.CommonClasses.Rating;
import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.PurchaseDTO;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Store {
    private int storeID;
    private String name;
    private String ownerName;
    private Inventory inventory;
    private AtomicBoolean isActiveStore;
    private DiscountPolicy discountPolicy;
    private PurchasePolicy purchasePolicy;
    private AtomicReference<Double> rating;
    private AtomicInteger numRatings;

    private Collection<PurchaseDTO> purchaseHistory;
    private ReentrantReadWriteLock readWriteLock;


    public Store(int id, String name,String ownerName, DiscountPolicy discountPolicy, PurchasePolicy purchasePolicy){
        this.name = name;
        this.storeID = id;
        this.ownerName = ownerName;
        this.inventory = new Inventory();
        this.isActiveStore = new AtomicBoolean(true);
        this.discountPolicy = discountPolicy;
        this.purchasePolicy = purchasePolicy;
        this.purchaseHistory = Collections.synchronizedCollection(new LinkedList<>());
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
        return isActiveStore.get();
    }

    public DiscountPolicy getDiscountPolicy() {
        return discountPolicy;
    }

    public PurchasePolicy getPurchasePolicy() {
        return purchasePolicy;
    }

    // todo: add policies

     public Response<PurchaseDTO> purchase(Map<ProductDTO, Integer> shoppingBasket) {
        Response<Boolean> result = inventory.removeProducts(shoppingBasket);
        PurchaseDTO purchaseDTO = null;
        double price = 0;

        for(ProductDTO productDTO: shoppingBasket.keySet()){
            price += productDTO.getPrice() * shoppingBasket.get(productDTO);
        }

        if(result.isFailure()){
            return new Response<>(null, true, "Product deletion failed successfully");
        }
        readWriteLock.writeLock().lock();

        if(!result.isFailure()){
            purchaseDTO = new PurchaseDTO(shoppingBasket, price, LocalDate.now());

            purchaseHistory.add(purchaseDTO);
        }

        readWriteLock.writeLock().unlock();
        return new Response<>(purchaseDTO, false, "purchase occurred");
    }

    public Collection<PurchaseDTO> getPurchaseHistory() {
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

    public Response<Boolean> addProductReview(int productID, String review) {
        return inventory.addProductReview(productID, review);
    }

    public void setActiveStore(boolean activeStore) {
        boolean currentActive;

        do {
            currentActive = isActiveStore.get();
        }while (!isActiveStore.compareAndSet(currentActive, activeStore));
    }

}
