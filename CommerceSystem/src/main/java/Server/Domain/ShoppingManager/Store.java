package Server.Domain.ShoppingManager;

import Server.DAL.DomainDTOs.StoreDTO;
import Server.Domain.CommonClasses.RatingEnum;
import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
import Server.Domain.UserManager.DTOs.BasketClientDTO;
import Server.Domain.UserManager.DTOs.PurchaseClientDTO;
import Server.Domain.UserManager.PurchaseHistory;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;
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

    private PurchaseHistory purchaseHistory;
    private ReentrantReadWriteLock readWriteLock;


    public Store(int id, String name, String ownerName){
        this.name = name;
        this.storeID = id;
        this.ownerName = ownerName;
        this.inventory = new Inventory();
        this.isActiveStore = new AtomicBoolean(true);
        this.discountPolicy = new DiscountPolicy();
        this.purchasePolicy = new PurchasePolicy();
        this.purchaseHistory = new PurchaseHistory();
        this.rating = new AtomicReference<>(0.0);
        this.numRatings = new AtomicInteger(0);
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    public Store(StoreDTO storeDTO){
        this.name = storeDTO.getName();
        this.storeID = storeDTO.getStoreID();
        this.ownerName = storeDTO.getOwnerName();
        this.inventory = new Inventory(storeDTO.getInventory());
        this.isActiveStore = new AtomicBoolean(storeDTO.isActiveStore());
        this.discountPolicy = new DiscountPolicy(storeDTO.getDiscountPolicy());
        this.purchasePolicy = new PurchasePolicy(storeDTO.getPurchasePolicy());
        this.purchaseHistory = new PurchaseHistory(storeDTO.getPurchaseHistory());
        this.rating = new AtomicReference<>(storeDTO.getRating());
        this.numRatings = new AtomicInteger(storeDTO.getNumRatings());
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    public StoreDTO toDTO(){
        return new StoreDTO(this.storeID,
                            this.name,
                            this.ownerName,
                            this.inventory.toDTO(),
                            this.isActiveStore.get(),
                            this.discountPolicy.toDTO(),
                            this.purchasePolicy.toDTO(),
                            this.rating.get(),
                            this.numRatings.get(),
                            this.purchaseHistory.toDTO());
    }

    public Response<Integer> addProduct(ProductClientDTO productDTO, int amount){
        if(amount <= 0){
            return new Response<>(-1, true, "The product amount cannot be negative or zero");
        }

        if(productDTO == null){
            return new Response<>(-1, true, "Cannot add NULL product");
        }

        if(productDTO.getPrice() <= 0){
            return new Response<>(-1, true, "Price cannot be negative");
        }

        int id = inventory.addProducts(productDTO, amount);
        return new Response<>(id, false, "The product added successfully to store");
    }

    public Response<Boolean> removeProduct(int productID, int amount){
        if(amount <= 0){
            return new Response<>(false, true, "The product amount cannot be negative or zero");
        }

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

     public Response<PurchaseClientDTO> purchase(Map<ProductClientDTO, Integer> shoppingBasket) {
        Response<Boolean> validatePurchase = purchasePolicy.isValidPurchase(shoppingBasket);
        if(validatePurchase.isFailure())
            return new Response<>(null, true, validatePurchase.getErrMsg());

        Response<Boolean> result = inventory.removeProducts(shoppingBasket);
        PurchaseClientDTO purchaseDTO;

//        double price = 0;
//
//        for(ProductDTO productDTO: shoppingBasket.keySet()){
//            price += productDTO.getPrice() * shoppingBasket.get(productDTO);
//        }

        double priceAfterDiscount = discountPolicy.calcDiscount(shoppingBasket);

        if(result.isFailure()){
            return new Response<>(null, true, "The product is sold out");
        }

        purchaseDTO = new PurchaseClientDTO(new BasketClientDTO(storeID ,name , new Vector<>(shoppingBasket.keySet()), shoppingBasket.values()), priceAfterDiscount, LocalDate.now());

         return new Response<>(purchaseDTO, false, "Store: Purchase occurred");
    }

    public Response<Collection<PurchaseClientDTO>> getPurchaseHistory() {
        return new Response<>(purchaseHistory.getPurchases(), false, "OK");
    }

    public void addRating(RatingEnum rate){
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

    public Response<Boolean> addProductReview(int productID, Review review) {
        return inventory.addProductReview(productID, review);
    }

    public void setActiveStore(boolean activeStore) {
        boolean currentActive;

        do {
            currentActive = isActiveStore.get();
        }while (!isActiveStore.compareAndSet(currentActive, activeStore));
    }

    public void addPurchaseHistory(PurchaseClientDTO purchaseDTO) {
        readWriteLock.writeLock().lock();
        purchaseHistory.addSinglePurchase(purchaseDTO);
        readWriteLock.writeLock().unlock();
    }

    public Response<Boolean> addDiscountRule(DiscountRule discountRule) {
        return discountPolicy.addDiscountRule(discountRule);
    }

    public Response<Boolean> removeDiscountRule(int ruleID){
        return discountPolicy.removeDiscountRule(ruleID);
    }

    public Response<Boolean> addPurchaseRule(PurchaseRule purchaseRule) {
        return purchasePolicy.addPurchaseRule(purchaseRule);
    }

    public Response<Boolean> removePurchaseRule(int ruleID){
        return purchasePolicy.removePurchaseRule(ruleID);
    }

    public double getTotalRevenue(){
        LocalDate yesterday = LocalDate.now().minus(Period.ofDays(1));
        double totalRevenue = 0;

        for(PurchaseClientDTO purchase: purchaseHistory.getPurchases()){
            if(LocalDate.parse(purchase.getPurchaseDate()).isAfter(yesterday))
                totalRevenue += purchase.getTotalPrice();
        }

        return totalRevenue;
    }


}
