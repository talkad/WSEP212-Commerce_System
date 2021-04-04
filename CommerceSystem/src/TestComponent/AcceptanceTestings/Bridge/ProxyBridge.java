package TestComponent.AcceptanceTestings.Bridge;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DiscountPolicy;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.PurchasePolicy;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.UserManager.Purchase;
import Server.Domain.UserManager.User;
import Server.Domain.UserManager.UserDetails;
import Server.Service.IService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProxyBridge implements IService {
    private IService real;

    public ProxyBridge(){
        real = null;
    }

    public void serRealBridge(IService implementation){
        if (real == null){
            real = implementation;
        }
    }

    @Override
    public void init() {
        if (real != null){
            real.init();
        }
    }

    @Override
    public Response<Boolean> register(String prevName, String username, String pwd) {
        if (real != null){
            return real.register(prevName, username, pwd);
        }
        return new Response<>(true, false, null);
    }

    @Override
    public Response<String> login(String prevName, String username, String pwd) {
        if (real != null){
            return real.login(prevName, username, pwd);
        }
        return new Response<>("yossi", false, null);
    }

    @Override
    public List<Store> getContent() {
        if (real != null){
            return real.getContent();
        }
        return new LinkedList<>();
    }

    @Override
    public List<Product> searchByProductName(String productName) {
        if (real != null){
            return real.searchByProductName(productName);
        }
        return new LinkedList<>();
    }

    @Override
    public List<Product> searchByProductCategory(String category) {
        if (real != null){
            return real.searchByProductCategory(category);
        }
        return new LinkedList<>();
    }

    @Override
    public List<Product> searchByProductKeyword(String keyword) {
        if (real != null){
            return real.searchByProductKeyword(keyword);
        }
        return new LinkedList<>();
    }

    @Override
    public Response<Boolean> addToCart(String username, Product product) {
        if (real != null){
            return real.addToCart(username, product);
        }
        return new Response<>(true, false, null);
    }

    @Override
    public Response<Boolean> removeFromCart(String username, Product product) {
        if (real != null){
            return real.removeFromCart(username, product);
        }
        return new Response<>(true, false, null);
    }

    @Override
    public Map<Integer, Map<Product, Integer>> getCartDetails(String username) {
        if (real != null){
            return real.getCartDetails(username);
        }
        return new HashMap<>();
    }

    @Override
    public Response<Boolean> updateProductQuantity(String username, Product product, int amount) {
        if (real != null){
            return real.updateProductQuantity(username, product, amount);
        }
        return new Response<>(true, false, null);
    }

    @Override
    public Response<Boolean> purchaseCartItems(String username, String creditCardDetails) {
        if (real != null){
            return real.purchaseCartItems(username, creditCardDetails);
        }
        return new Response<>(true, false, null);
    }

    @Override
    public User getUserByName(String username) {
        if (real != null){
            return real.getUserByName(username);
        }
        return new User("yossi");
    }

    @Override
    public Response<String> logout(String userName) {
        if (real != null){
            return real.logout(userName);
        }
        return new Response<>("yossi", false, null);
    }

    @Override
    public Response<Integer> openStore(String username, String storeName) {
        if (real != null){
            return real.openStore(username, storeName);
        }
        return new Response<>(1, false, null);
    }

    @Override
    public Response<Boolean> addProductReview(String username, int productID, String review) {
        if (real != null){
            return real.addProductReview(username, productID, review);
        }
        return new Response<>(true, false, null);
    }

    @Override
    public List<Purchase> getPurchaseHistory(String username) {
        if (real != null){
            return real.getPurchaseHistory(username);
        }
        return new LinkedList<>();
    }

    @Override
    public Response<Boolean> addProductsToStore(String username, int storeID, Product product, int amount) {
        if (real != null){
            return real.addProductsToStore(username, storeID, product, amount);
        }
        return new Response<>(true, false, null);
    }

    @Override
    public Response<Boolean> removeProductsFromStore(String username, int storeID, Product product, int amount) {
        if (real != null){
            return real.removeProductsFromStore(username, storeID, product, amount);
        }
        return new Response<>(true, false, null);
    }

    @Override
    public Response<Boolean> updateProductPrice(String username, int storeID, int productID, int newPrice) {
        if (real != null){
            return real.updateProductPrice(username, storeID, productID, newPrice);
        }
        return new Response<>(true, false, null);
    }

    @Override
    public List<PurchasePolicy> getPurchasePolicy(String username, int storeID) {
        if (real != null){
            return real.getPurchasePolicy(username, storeID);
        }
        return new LinkedList<>();
    }

    @Override
    public List<DiscountPolicy> getDiscountPolicy(String username, int storeID) {
        if (real != null){
            return real.getDiscountPolicy(username, storeID);
        }
        return new LinkedList<>();
    }

    @Override
    public Response<Boolean> appointStoreOwner(String appointerName, String appointeeName, int storeID) {
        if (real != null){
            return real.appointStoreOwner(appointerName, appointeeName, storeID);
        }
        return new Response<>(true, false, null);
    }

    @Override
    public Response<Boolean> appointStoreManager(String appointerName, String appointeeName, int storeID) {
        if (real != null){
            return real.appointStoreManager(appointerName, appointeeName, storeID);
        }
        return new Response<>(true, false, null);
    }

    @Override
    public Response<Boolean> fireStoreManager(String appointerName, String appointeeName, int storeID) {
        if (real != null){
            return real.fireStoreManager(appointerName, appointeeName, storeID);
        }
        return new Response<>(true, false, null);
    }

    @Override
    public Response<UserDetails> getWorkersDetails(String username, int storeID) {
        if (real != null){
            return real.getWorkersDetails(username, storeID);
        }
        return new Response<>(new UserDetails(), false, null);
    }

    @Override
    public Response<Purchase> getPurchaseDetails(String username, int storeID) {
        if (real != null){
            return real.getPurchaseDetails(username, storeID);
        }
        return new Response<>(new Purchase(), false, null);
    }

    @Override
    public List<Purchase> getUserPurchaseHistory(String username) {
        if (real != null){
            return real.getUserPurchaseHistory(username);
        }
        return new LinkedList<>();
    }

    @Override
    public List<Purchase> getStorePurchaseHistory(int storeID) {
        if (real != null){
            return real.getStorePurchaseHistory(storeID);
        }
        return new LinkedList<>();
    }
}
