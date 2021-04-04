package Server.Domain.ShoppingManager;
import Server.Domain.CommonClasses.Rating;
import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.Purchase;
import Server.Domain.UserManager.ShoppingBasket;
import Server.Domain.UserManager.Purchase;
import Server.Domain.UserManager.PurchaseDTO;
import Server.Domain.UserManager.ShoppingBasket;
import Server.Domain.UserManager.ShoppingCart;
import org.xeustechnologies.googleapi.spelling.SpellChecker;
import org.xeustechnologies.googleapi.spelling.SpellCorrection;
import org.xeustechnologies.googleapi.spelling.SpellRequest;
import org.xeustechnologies.googleapi.spelling.SpellResponse;

import javax.swing.text.StyledEditorKit;
import java.time.LocalDate;
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

    public Response<PurchaseDTO> purchase (ShoppingCart shoppingCart){
        for(Map.Entry<Integer, Map <ProductDTO, Integer>> entry : shoppingCart.getBaskets().entrySet())
            if(purchaseFromStore(entry.getKey(), entry.getValue()).isFailure())
                return new Response<>(null, true, "Problem with purchase from store " + entry.getKey() + ".");
        // TODO ALL PAYMENT PROCESS
       return new Response<>(new PurchaseDTO(shoppingCart, shoppingCart.getTotalPrice(), LocalDate.now()), false, "Purchase has been successfully made.");
    }

    public Response<Boolean> purchaseFromStore(int storeID, Map<ProductDTO, Integer> shoppingBasket){
        Response<Boolean> result;
        Store store;

       if(!stores.containsKey(storeID)) {
            result = new Response<>(false, true, "This store does not exists");
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

    public Response<List<PurchaseDTO>> getStorePurchaseHistory(int storeID) {
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

    public Response<List<Store>> searchByStoreName(String storeName) {
        List<Store> storeList = new LinkedList<>();

        for(Store store: stores.values()){
            if(store.getName().equals(storeName))
                storeList.add(store);
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
