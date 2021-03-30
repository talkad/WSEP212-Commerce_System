package Domain.ShoppingManager;
import Domain.CommonClasses.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class StoreController {
    private static volatile StoreController storeController = null;
    private List<Store> stores;

    private StoreController(){
        stores = Collections.synchronizedList(new ArrayList<>());
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

    public Response<Boolean> addStore(int id, String name, DiscountPolicy discountPolicy, PurchasePolicy purchasePolicy){
        for(Store store : stores)
            if(store.getStoreID() == id)
                return new Response<>(false, true, "Store id already exists.");
        stores.add(new Store(id, name, discountPolicy, purchasePolicy));
        return new Response<>(true, false, "Store has been added successfully.");
    }

    public Response<Boolean> addStore(Store store){
        if(store == null)
            return new Response<>(false, true, "Invalid store");
        if(stores.contains(store))
            return new Response<>(false, true, "Store already exists.");
        stores.add(store);
        return new Response<>(true, false, "Store has been added successfully.");
    }

    public List<Product> searchByProductName(String productName){
        List<Product> productList = new LinkedList<>();
        if(productName != null) {
            for (Store store : stores)
                for (Product product : store.getInventory().getInventory())
                    if (product.getName().equals(productName))
                        productList.add(product);
        }

        return productList;
    }

    public List<Product> searchByCategory(String category){
        List<Product> productList = new LinkedList<>();
        if(category != null) {
            for (Store store : stores)
                for (Product product : store.getInventory().getInventory())
                    if (product.containsCategory(category))
                        productList.add(product);
        }

        return productList;
    }

    public List<Product> searchByKeyWord(String keyword){
        List<Product> productList = new LinkedList<>();
        if(keyword != null) {
            for (Store store : stores)
                for (Product product : store.getInventory().getInventory())
                    if (product.containsKeyword(keyword))
                        productList.add(product);
        }
        return productList;
    }

    public Store getStoreById(int storeId) {
        for(Store store : stores)
            if(store.getStoreID() == storeId)
                return store;
        return null;
    }
}
