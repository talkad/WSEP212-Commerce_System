package Server.Domain.ShoppingManager;
import Server.Domain.CommonClasses.Response;

import Server.Domain.UserManager.PurchaseDTO;
import Server.Domain.UserManager.ShoppingCart;
import org.xeustechnologies.googleapi.spelling.SpellChecker;
import org.xeustechnologies.googleapi.spelling.SpellCorrection;
import org.xeustechnologies.googleapi.spelling.SpellRequest;
import org.xeustechnologies.googleapi.spelling.SpellResponse;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

        Store store = new Store(id, StoreName, ownerName, null, null);
        stores.put(id, store);

        return new Response<>(id, false, "Store with id " + id + " opened successfully");
    }

    public List<Product> searchByProductName(String productName){
        List<Product> searchRes = SearchEngine.getInstance().searchByProductName(productName);
        printSuggestedCorrection(searchRes, productName);
        return  searchRes;
    }

    public List<Product> searchByCategory(String category){
        List<Product> searchRes = SearchEngine.getInstance().searchByProductName(category);
        printSuggestedCorrection(searchRes, category);
        return searchRes;
    }

    public List<Product> searchByKeyWord(String keyword){
        List<Product> searchRes = SearchEngine.getInstance().searchByKeyWord(keyword);
        printSuggestedCorrection(searchRes, keyword);
        return searchRes;
    }

    public List<Product> filterByRating(double rating){ return SearchEngine.getInstance().filterByRating(rating);}

    public List<Product> filterByPriceRange(double lowRate, double highRate){ return SearchEngine.getInstance().filterByPriceRange(lowRate, highRate);}

    public List<Product> filterByStoreRating(double rating){ return SearchEngine.getInstance().filterByStoreRating(rating);}

    public List<Product> filterByCategory(String category){ return SearchEngine.getInstance().searchByCategory(category);}

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

    public Response<Boolean> addProductToStore(int storeID, ProductDTO productDTO, int amount){
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

    public Response<PurchaseDTO> purchase (ShoppingCart shoppingCart) {
        Store s;
        Map<Integer, Map<ProductDTO, Integer>> prods = new ConcurrentHashMap<>();
        for (Map.Entry<Integer, Map<ProductDTO, Integer>> entry : shoppingCart.getBaskets().entrySet()) {
            if (purchaseFromStore(entry.getKey(), entry.getValue()).isFailure()) {
                for (Map.Entry<Integer, Map<ProductDTO, Integer>> refundEntries : prods.entrySet()) {
                    s = getStoreById(refundEntries.getKey());
                    for (Map.Entry<ProductDTO, Integer> shopRefund : refundEntries.getValue().entrySet())
                        s.addProduct(shopRefund.getKey(), shopRefund.getValue());
                }
                return new Response<>(null, true, "Problem with purchase from store " + entry.getKey() + ".");
            }
            prods.put(entry.getKey(), entry.getValue());
        }
        return new Response<>(new PurchaseDTO(shoppingCart, shoppingCart.getTotalPrice(), LocalDate.now()), false, "Purchase can be made.");
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

    public List<Store> getContent() {
        return (List<Store>) stores.values();
    }

    public Response<Collection<PurchaseDTO>> getStorePurchaseHistory(int storeID) {
        Store store = stores.get(storeID);

        if(store == null)
            return new Response<>(null, true, "This store doesn't exists");

        return  new Response<>(store.getPurchaseHistory().stream().collect(Collectors.toList()), false, "success");
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
}
