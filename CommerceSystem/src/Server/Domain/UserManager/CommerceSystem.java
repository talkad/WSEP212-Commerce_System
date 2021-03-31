package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.*;
import Server.Service.IService;

import java.util.List;
import java.util.Map;


public class CommerceSystem implements IService {

    private static volatile CommerceSystem commerceSystem;


    public static CommerceSystem getInstance(){
        if(commerceSystem == null){
            synchronized (StoreController.class){
                if(commerceSystem == null)
                    commerceSystem = new CommerceSystem();
            }
        }
        return commerceSystem;
    }

    @Override
    public void init() {

    }

    @Override
    public Response<Boolean> register(String username, String pwd) {
        return null;
    }

    @Override
    public Response<Boolean> login(String username, String pwd) {
        return null;
    }

    @Override
    public List<Store> getContent() {
        return null;
    }

    @Override
    public List<Product> searchByProductName(String productName) {
        return null;
    }

    @Override
    public List<Product> searchByProductCategory(String category) {
        return null;
    }

    @Override
    public List<Product> searchByProductKeyword(String keyword) {
        return null;
    }

    @Override
    public Response<Boolean> addToCart(String username, Product product) {
        return null;
    }

    @Override
    public Response<Boolean> removeFromCart(String username, Product product) {
        return null;
    }

    @Override
    public Map<Integer, Map<Product, Integer>> getCartDetails(String username) {
        return null;
    }

    @Override
    public Response<Boolean> purchaseCartItems(String username, String creditCardDetails) {
        return null;
    }

    @Override
    public User getUserByName(String username) {
        return null;
    }

    @Override
    public Response<Boolean> logout(String userName) {
        return null;
    }

    @Override
    public Response<Boolean> openStore(String username, Store store) {
        return null;
    }

    @Override
    public Response<Boolean> addProductReview(String username, int productID, String review) {
        return null;
    }

    @Override
    public List<Purchase> getPurchaseHistory(String username) {
        return null;
    }

    @Override
    public Response<Boolean> addProductsToStore(String username, int storeID, Product product, int amount) {
        return null;
    }

    @Override
    public Response<Boolean> removeProductsFromStore(String username, int storeID, Product product, int amount) {
        return null;
    }

    @Override
    public Response<Boolean> updateProductPrice(String username, int storeID, int productID, int newPrice) {
        return null;
    }

    @Override
    public List<PurchasePolicy> getPurchasePolicy(String username, int storeID) {
        return null;
    }

    @Override
    public List<DiscountPolicy> getDiscountPolicy(String username, int storeID) {
        return null;
    }

    @Override
    public Response<Boolean> appointStoreOwner(String appointerName, String appointeeName, int storeID) {
        return null;
    }

    @Override
    public Response<Boolean> appointStoreManager(String appointerName, String appointeeName, int storeID) {
        return null;
    }

    @Override
    public Response<Boolean> fireStoreManager(String appointerName, String appointeeName, int storeID) {
        return null;
    }

    @Override
    public Response<UserDetails> getWorkersDetails(String username, int storeID) {
        return null;
    }

    @Override
    public Response<Purchase> getPurchaseDetails(String username, int storeID) {
        return null;
    }

    @Override
    public List<Purchase> getUserPurchaseHistory(String username) {
        return null;
    }

    @Override
    public List<Purchase> getStorePurchaseHistory(int storeID) {
        return null;
    }

}
