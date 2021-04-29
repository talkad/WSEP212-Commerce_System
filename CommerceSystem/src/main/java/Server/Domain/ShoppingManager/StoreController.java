package Server.Domain.ShoppingManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.PurchaseDTO;
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
        indexer = new AtomicInteger(0);
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
        int id = indexer.getAndIncrement();

        Store store = new Store(id, StoreName, ownerName, null, null);
        stores.put(id, store);

        return new Response<>(id, false, "Store with id " + id + " opened successfully");
    }

    public Response<List<ProductDTO>> searchByProductName(String productName){
        return SearchEngine.getInstance().searchByProductName(productName);
    }

    public Response<List<ProductDTO>> searchByCategory(String category){
        return SearchEngine.getInstance().searchByCategory(category);
    }

    public Response<List<ProductDTO>> searchByKeyWord(String keyword) {
        return SearchEngine.getInstance().searchByKeyWord(keyword);
    }

    public Response<List<ProductDTO>> filterByRating(double rating){ return SearchEngine.getInstance().filterByRating(rating);}

    public Response<List<ProductDTO>> filterByPriceRange(double lowRate, double highRate){ return SearchEngine.getInstance().filterByPriceRange(lowRate, highRate);}

    public Response<List<ProductDTO>> filterByStoreRating(double rating){ return SearchEngine.getInstance().filterByStoreRating(rating);}

    public Response<List<ProductDTO>> filterByCategory(String category){ return SearchEngine.getInstance().searchByCategory(category);}

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
        return stores.get(storeId);
    }

    /**
     * @pre only searchEngine can use this function (concurrency issues)
     * @return stores list
     */
    public Collection<Store> getStores(){
        return stores.values();
    }

    // activated when purchase aborted and all products need to be added to their inventories
    public void addProductsToInventories(ShoppingCart shoppingCart) {
        Map<ProductDTO, Integer> basket;

        for(Integer storeID : shoppingCart.getBaskets().keySet()){

            basket = shoppingCart.getBasket(storeID);

            for(ProductDTO productDTO : basket.keySet()){
                stores.get(storeID).addProduct(productDTO, basket.get(productDTO));
            }
        }
    }

    public Response<List<PurchaseDTO>> purchase(ShoppingCart shoppingCart) {
        Store s;
        Map<Integer, Map<ProductDTO, Integer>> prods = new ConcurrentHashMap<>();
        Map<Integer, PurchaseDTO> purchases = new HashMap<>();
        Response<PurchaseDTO> resPurchase;
        Map<Integer, Map<ProductDTO, Integer>> baskets = shoppingCart.getBaskets();

        if(baskets.isEmpty())
            return new Response<>(null, true, "You cannot purchase an empty cart");

        for (Map.Entry<Integer, Map<ProductDTO, Integer>> entry : baskets.entrySet()) {
            resPurchase = purchaseFromStore(entry.getKey(), entry.getValue());

            if (resPurchase.isFailure()) {
                for (Map.Entry<Integer, Map<ProductDTO, Integer>> refundEntries : prods.entrySet()) {
                    s = getStoreById(refundEntries.getKey());
                    for (Map.Entry<ProductDTO, Integer> shopRefund : refundEntries.getValue().entrySet())
                        s.addProduct(shopRefund.getKey(), shopRefund.getValue());
                }
                return new Response<>(null, true, "Problem with purchase from store " + entry.getKey() + " " + resPurchase.getErrMsg());
            }

            purchases.put(entry.getKey(), resPurchase.getResult());
            prods.put(entry.getKey(), entry.getValue());
        }

        for(Integer storeID: purchases.keySet()){
            stores.get(storeID).addPurchaseHistory(purchases.get(storeID));
        }

        return new Response<>(new LinkedList<>(purchases.values()), false, "Purchase can be made.");
    }

    private Response<PurchaseDTO> purchaseFromStore(int storeID, Map<ProductDTO, Integer> shoppingBasket){
        Response<PurchaseDTO> result;
        Store store;

       if(!stores.containsKey(storeID)) {
            result = new Response<>(null, true, "This store does not exists");
        }
        else{
            store = stores.get(storeID);
            result = store.purchase(shoppingBasket);
        }

        return result;
    }

    public Response<Collection<Store>> getContent() {
        return new Response<>(stores.values(), false, "all content");
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

    public Response<List<StoreDTO>> searchByStoreName(String storeName) {
        List<StoreDTO> storeList = new LinkedList<>();

        for(Store store: stores.values()){
            if(store.getName().equals(storeName))
                storeList.add(new StoreDTO(store.getStoreID(), store.getName(), store.getInventory().getProductsDTO()));
        }

        return new Response<>(storeList, false, "all stores with name " + storeName);
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
