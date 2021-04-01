package Server.Domain.ShoppingManager;
import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.Purchase;

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

    public Response<Integer> openStore(String StoreName){
        int id = indexer.getAndIncrement();

        // should be a way to add and edit policies
        Store store = new Store(id, StoreName, null, null);
        stores.put(id, store);

        return new Response<>(id, false, "Store with id "+id+" opened successfully");
    }

//    public Response<Boolean> addStore(int storeID, String name, DiscountPolicy discountPolicy, PurchasePolicy purchasePolicy){
//        if(stores.containsKey(storeID))
//            return new Response<>(false, true, "Store id already exists.");
//
//        stores.put(storeID, new Store(storeID, name, discountPolicy, purchasePolicy));
//        return new Response<>(true, false, "Store has been added successfully.");
//    }
//
//    public Response<Boolean> addStore(Store store){
//        if(store == null)
//            return new Response<>(false, true, "Invalid store");
//        if(stores.containsKey(store.getStoreID()))
//            return new Response<>(false, true, "Store already exists.");
//        stores.put(store.getStoreID(), store);
//        return new Response<>(true, false, "Store has been added successfully.");
//    }

    public List<Product> searchByProductName(String productName){
        return SearchEngine.getInstance().searchByProductName(productName);
    }

    public List<Product> searchByCategory(String category){
        return SearchEngine.getInstance().searchByCategory(category);
    }

    public List<Product> searchByKeyWord(String keyword){
        return SearchEngine.getInstance().searchByKeyWord(keyword);
    }

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

    public Response<Boolean> addProductToStore(int storeID, Product product, int amount){
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
            store.addProduct(product, amount);

            result = new Response<>(false, true, "The product added successfully");
        }

        return result;
    }

    public Response<Boolean> removeProductFromStore(int storeID, Product product, int amount){
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
            result = store.removeProduct(product, amount);
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

    public List<Purchase> getStorePurchaseHistory(int storeID) {
        Store store = stores.get(storeID);

        if(store == null)
            return null;

        return store.getPurchaseHistory();
    }
}
