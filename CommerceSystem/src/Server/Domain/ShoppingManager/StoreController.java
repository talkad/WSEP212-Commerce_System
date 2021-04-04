package Server.Domain.ShoppingManager;
import Server.Domain.CommonClasses.Rating;
import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.Purchase;
import Server.Domain.UserManager.ShoppingBasket;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class StoreController {
    private static volatile StoreController storeController = null;
    // map storeID to its corresponding store
    private Map<Integer, Store> stores;
    private AtomicInteger indexer;


    private StoreController(){
        stores = new ConcurrentHashMap<>();
        indexer = new AtomicInteger(0);
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
        int id = indexer.getAndIncrement();

        // TODO: should be a way to add and edit policies
        Store store = new Store(id, StoreName, ownerName, null, null);
        stores.put(id, store);

        return new Response<>(id, false, "Store with id " + id + " opened successfully");
    }

    public List<Product> searchByProductName(String productName){
        return SearchEngine.getInstance().searchByProductName(productName);
    }

    public List<Product> searchByCategory(String category){
        return SearchEngine.getInstance().searchByCategory(category);
    }

    public List<Product> searchByKeyWord(String keyword){
        return SearchEngine.getInstance().searchByKeyWord(keyword);
    }

    public List<Product> filterByRating(double rating){ return SearchEngine.getInstance().filterByRating(rating);}

    public List<Product> filterByPriceRange(double lowRate, double highRate){ return SearchEngine.getInstance().filterByPriceRange(lowRate, highRate);}

    public List<Product> filterByStoreRating(double rating){ return SearchEngine.getInstance().filterByStoreRating(rating);}

    public List<Product> filterByCategory(String category){ return SearchEngine.getInstance().searchByCategory(category);}


    public Store getStoreById(int storeId) {
        return stores.get(storeId);
    }

    /**
     * @pre only searchEngine can use this function (concurrency issues)
     * @return stores list
     */
    public Collection<Store> getStores(){
        return stores.values();
    }

    public Response<Boolean> addProductToStore(ProductDTO productDTO, int amount){
        Response<Boolean> result;
        Store store;

        if(amount < 0){
            result = new Response<>(false, true, "The amount cannot be negative");
        }
        else if(!stores.containsKey(productDTO.getStoreID())) {
            result = new Response<>(false, true, "This store does not exists");
        }
        else{
            store = stores.get(productDTO.getStoreID());
            store.addProduct(productDTO, amount);

            result = new Response<>(false, true, "The product added successfully");
        }

        return result;
    }

    public Response<Boolean> removeProductFromStore(int storeID, int productID, int amount){
        Response<Boolean> result;
        Store store;

        if(amount < 0){
            result = new Response<>(false, true, "The amount cannot be negative");
        }
        else if(!stores.containsKey(storeID)) {
            result = new Response<>(false, true, "This store does not exists");
        }
        else{
            store = stores.get(storeID);
            result = store.removeProduct(productID, amount);
        }

        return result;
    }

    public Response<Boolean> purchaseFromStore(int storeID, Product product, int amount){
        Response<Boolean> result;
        Store store;

        if(amount < 0){
            result = new Response<>(false, true, "The amount cannot be negative");
        }
        else if(!stores.containsKey(storeID)) {
            result = new Response<>(false, true, "This store does not exists");
        }
        else{
            store = stores.get(storeID);
            result = store.purchase(product, amount);
        }

        return result;
    }

    public List<Store> getContent() {
        return (List<Store>) stores.values();
    }

    public Response<Map<ProductDTO, Integer>> getStorePurchaseHistory(int storeID) {
        Store store = stores.get(storeID);

        if(store == null)
            return new Response<>(null, true, "This store doesn't exists");

        return  new Response<>(store.getPurchaseHistory(), false, "success");
    }

    public String getStoreOwnerName(int storeID){
        return stores.get(storeID).getOwnerName();
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
        Store store = stores.get(storeID);

        if(store == null){
            return new Response<>(null, true, "store doesn't exists");
        }
        else{
            return store.getProduct(productID);
        }
    }

    public Response<Boolean> addProductReview(int storeID, int productID, String review) {
        Response<Boolean> result;
        Store store;

        if(!stores.containsKey(storeID)) {
            result = new Response<>(false, true, "This store does not exists");
        }
        else{
            store = stores.get(storeID);
            result = store.addProductReview(productID, review);
        }

        return result;
    }

//    public Response<Boolean> addProductRating(int storeID, int productID, Rating rate) { todo next version
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
