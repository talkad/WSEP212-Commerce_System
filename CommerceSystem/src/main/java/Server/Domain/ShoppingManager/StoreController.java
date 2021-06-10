package Server.Domain.ShoppingManager;

import Server.DAL.DALService;
import Server.DAL.StoreDTO;
import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.DTOs.StoreClientDTO;
import Server.Domain.UserManager.DTOs.PurchaseClientDTO;
import Server.Domain.UserManager.ShoppingCart;
import org.xeustechnologies.googleapi.spelling.SpellChecker;
import org.xeustechnologies.googleapi.spelling.SpellCorrection;
import org.xeustechnologies.googleapi.spelling.SpellRequest;
import org.xeustechnologies.googleapi.spelling.SpellResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class StoreController {
    private static volatile StoreController storeController = null;
    // map storeID to its corresponding store
    private Map<Integer, Store> stores;
    private AtomicInteger indexer;
    private SpellChecker spellChecker;
    private SpellRequest spellRequest;
    private SpellResponse spellRes;

    private StoreController(){
        stores = new ConcurrentHashMap<>();
        indexer = new AtomicInteger(DALService.getInstance().getNextAvailableStoreID());
        spellChecker = new SpellChecker();
        spellRequest = new SpellRequest();
    }

    public static StoreController getInstance(){
        if(storeController == null){
            synchronized (StoreController.class){
                if(storeController == null)
                    storeController = new StoreController();
            }
        }
        return storeController;
    }

    public Response<Integer> openStore(String StoreName, String ownerName){

        // todo - check store name

        int id = indexer.getAndIncrement();
        Store store = new Store(id, StoreName, ownerName);
        stores.put(id, store);

        return new Response<>(id, false, "Store with id " + id + " opened successfully");
    }

    public Response<List<ProductClientDTO>> searchByProductName(String productName){
        return SearchEngine.getInstance().searchByProductName(productName);
    }

    public Response<List<ProductClientDTO>> searchByCategory(String category){
        return SearchEngine.getInstance().searchByCategory(category);
    }

    public Response<List<ProductClientDTO>> searchByKeyWord(String keyword) {
        return SearchEngine.getInstance().searchByKeyWord(keyword);
    }

    public Response<List<ProductClientDTO>> filterByRating(double rating){ return SearchEngine.getInstance().filterByRating(rating);}

    public Response<List<ProductClientDTO>> filterByPriceRange(double lowRate, double highRate){ return SearchEngine.getInstance().filterByPriceRange(lowRate, highRate);}

    public Response<List<ProductClientDTO>> filterByStoreRating(double rating){ return SearchEngine.getInstance().filterByStoreRating(rating);}

    public Response<List<ProductClientDTO>> filterByCategory(String category){ return SearchEngine.getInstance().searchByCategory(category);}

    private void printSuggestedCorrection(Collection<Product> productsList, String faultWord){
        if (productsList.isEmpty()){
            spellRequest.setText(faultWord);
            spellRequest.setIgnoreDuplicates(true);
            spellRes = spellChecker.check(spellRequest);
            System.out.println("Did you refer to one of the following:");
            for(SpellCorrection spellCorrection : spellRes.getCorrections())
                System.out.println(spellCorrection.getValue());
        }
    }

    public Store getStoreById(int storeId) {
        if(stores.containsKey(storeId)) {
            return stores.get(storeId);
        }
        else {
            StoreDTO storeDTO = DALService.getInstance().getStore(storeId);
            if(storeDTO == null){
                return null;
            }
            Store store = new Store(storeDTO);
            stores.put(storeId, store);
            return store;
        }
    }

    public String getStoreName(int storeId) {
        if(stores.containsKey(storeId)) {
            return stores.get(storeId).getName();
        }
        else {
            StoreDTO storeDTO = DALService.getInstance().getStore(storeId);
            if(storeDTO == null){
                return null;
            }
            return storeDTO.getName();
        }
    }

//    /**
//     * @pre only searchEngine can use this function (concurrency issues)
//     * @return stores list
//     */
//    public Collection<Store> getStores(){
//        return stores.values();
//    }

    // activated when purchase aborted and all products need to be added to their inventories
    public void addProductsToInventories(ShoppingCart shoppingCart) {
        Map<Product, Integer> basket;

        for(Integer storeID : shoppingCart.getBaskets().keySet()){

            basket = shoppingCart.getBasket(storeID);

            for(Product product : basket.keySet()){
                stores.get(storeID).addProduct(product.getProductDTO(), basket.get(product));
            }
        }
    }

    // activated when purchase aborted and all products need to be added to their inventories
    public void addProductsToInventories(Product product, int storeID) {
        stores.get(storeID).addProduct(product.getProductDTO(), 1);
    }

    public Response<List<PurchaseClientDTO>> purchase(ShoppingCart shoppingCart) {
        Store s;
        Map<Integer, Map<ProductClientDTO, Integer>> prods = new ConcurrentHashMap<>();
        Map<Integer, PurchaseClientDTO> purchases = new HashMap<>();
        Response<PurchaseClientDTO> resPurchase;
        Map<Integer, Map<Product, Integer>> baskets = shoppingCart.getBaskets();

        if(baskets.isEmpty())
            return new Response<>(null, true, "You cannot purchase an empty cart");

        for (Map.Entry<Integer, Map<Product, Integer>> entry : baskets.entrySet()) {
            resPurchase = purchaseFromStore(entry.getKey(), parseProductsMap(entry.getValue()));

            if (resPurchase.isFailure()) {
                for (Map.Entry<Integer, Map<ProductClientDTO, Integer>> refundEntries : prods.entrySet()) {
                    s = getStoreById(refundEntries.getKey());
                    for (Map.Entry<ProductClientDTO, Integer> shopRefund : refundEntries.getValue().entrySet())
                        s.addProduct(shopRefund.getKey(), shopRefund.getValue());
                }
                return new Response<>(null, true, "Problem with purchase from store " + entry.getKey() + " " + resPurchase.getErrMsg());
            }

            purchases.put(entry.getKey(), resPurchase.getResult());
            prods.put(entry.getKey(), parseProductsMap(entry.getValue()));
        }

        for(Integer storeID: purchases.keySet()){
            stores.get(storeID).addPurchaseHistory(purchases.get(storeID));
        }

        return new Response<>(new LinkedList<>(purchases.values()), false, "Purchase can be made.");
    }

    private Map<ProductClientDTO, Integer> parseProductsMap(Map<Product, Integer> value) {
        Map<ProductClientDTO, Integer> products = new ConcurrentHashMap<>();

        for(Map.Entry<Product, Integer> entry : value.entrySet()){
            products.put(entry.getKey().getProductDTO(), entry.getValue());
        }

        return products;
    }

    public Response<PurchaseClientDTO> purchase(Product product) {
        Response<PurchaseClientDTO> res;
        Store store = stores.get(product.getStoreID());
        Map<ProductClientDTO, Integer> purchase = new HashMap<>();
        purchase.put(product.getProductDTO(), 1);

        if(store == null)
            return new Response<>(null, true, "The store doesn't exists");

        res = store.purchase(purchase);

        if(!res.isFailure()){
            store.addPurchaseHistory(res.getResult());
        }

        return res;
    }

    private Response<PurchaseClientDTO> purchaseFromStore(int storeID, Map<ProductClientDTO, Integer> shoppingBasket){
        Response<PurchaseClientDTO> result;
        Store store;

        if(!stores.containsKey(storeID)) {
            result = new Response<>(null, true, "This store does not exists");
        }
        else{
            store = this.getStoreById(storeID);
            result = store.purchase(shoppingBasket);
        }

        return result;
    }

    public Response<Collection<Store>> getContent() {
        return new Response<>(stores.values(), false, "all content");
    }

    public String getStoreOwnerName(int storeID){
        return this.getStoreById(storeID).getOwnerName();
    }

    public Response<Boolean> updateProductInfo(int storeID, int productID, double newPrice, String newName){
        Response<Boolean> result;
        Store store;

        if(!stores.containsKey(storeID)) {
            result = new Response<>(false, true, "This store does not exists");
        }
        else{
            store = stores.get(storeID);
            result = store.updateProductInfo(productID, newPrice, newName);
        }

        return result;
    }

    public Response<Product> getProduct(int storeID, int productID) {
        Store store = this.getStoreById(storeID);

        if(store == null){
            return new Response<>(null, true, "store doesn't exists");
        }
        else{
            return store.getProduct(productID);
        }
    }

    public Response<List<StoreClientDTO>> searchByStoreName(String storeName) {
        List<StoreClientDTO> storeList = new LinkedList<>();
        Collection<StoreDTO> storeDTOS = DALService.getInstance().getAllStores();

        for(StoreDTO store: storeDTOS){
            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            System.out.println(store.getName());
            Store domainStore = new Store(store);
            System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
            if(store.getName().equals(storeName))
                storeList.add(new StoreClientDTO(store.getStoreID(), store.getName(), domainStore.getInventory().getProductsDTO()));
        }

        return new Response<>(storeList, false, "all stores with name " + storeName);
    }

    public double getTotalSystemRevenue() {
        double totalRevenue = 0;

        Collection<StoreDTO> storeDTOS = DALService.getInstance().getAllStores();
        for(StoreDTO storeDTO: storeDTOS) {
            Store store = new Store(storeDTO);
            totalRevenue += store.getTotalRevenue();
        }

        return totalRevenue;
    }

    public Response<StoreClientDTO> getStore(int storeID){
        Store store = this.getStoreById(storeID);

        if(store == null)
            return new Response<>(null, true, "The store doesn't exists");

        return new Response<>(new StoreClientDTO(store.getStoreID(), store.getName(), store.getInventory().getProductsDTO()), true, "store found");
    }


//    public Response<Boolean> addStoreRating(int storeID, int productID, Rating rate) { todo next version
//        Response<Boolean> result;
//        Store store;
//
//        if(!stores.containsKey(storeID)) {
//            result = new Response<>(false, true, "This store does not exists");
//        }
//        else{
//            store = stores.get(storeID);
//            result = store.addProductRating(productID, rate);
//        }
//
//        return result;
//    }

}