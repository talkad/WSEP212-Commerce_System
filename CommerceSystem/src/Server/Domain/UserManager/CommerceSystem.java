package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.*;
import Server.Service.IService;

import java.util.List;
import java.util.Map;


public class CommerceSystem implements IService {

    private UserController userController;
    private StoreController storeController;

    private CommerceSystem() {
        this.userController = UserController.getInstance();
        this.storeController = StoreController.getInstance();
    }

    private static class CreateSafeThreadSingleton
    {
        private static final CommerceSystem INSTANCE = new CommerceSystem();
    }

    public static CommerceSystem getInstance()
    {
        return CommerceSystem.CreateSafeThreadSingleton.INSTANCE;
    }

    @Override
    public void init() {

    }

    @Override
    public Response<Boolean> register(String prevName, String username, String pwd) {
        return userController.register(prevName, username, pwd);
    }

    @Override
    public Response<String> login(String prevName, String username, String pwd) {
        return userController.login(prevName, username, pwd);
    }

    @Override
    public List<Store> getContent() {
        return storeController.getContent();
    }

    @Override
    public List<Product> searchByProductName(String productName) {
        return storeController.searchByProductName(productName);
    }

    @Override
    public List<Product> searchByProductCategory(String category) {
        return storeController.searchByCategory(category);
    }

    @Override
    public List<Product> searchByProductKeyword(String keyword) {
        return storeController.searchByKeyWord(keyword);
    }

    @Override
    public Response<Boolean> addToCart(String username, Product product) {
        return userController.addToCart(username, product);
    }

    @Override
    public Response<Boolean> removeFromCart(String username, Product product) {
        return userController.removeProduct(username, product);
    }

    @Override
    public Map<Integer, Map<Product, Integer>> getCartDetails(String username) {
        return userController.getShoppingCartContents(username);
    }

    @Override
    public Response<Boolean> updateProductQuantity(String username, Product product, int amount) {
        return userController.updateProductQuantity(username, product, amount);
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
    public Response<String> logout(String userName) {
        return userController.logout(userName);
    }

    @Override
    public Response<Integer> openStore(String username, String storeName) {
        return userController.openStore(username, storeName);
    }

    @Override
    public Response<Boolean> addProductReview(String username, int productID, String review) {
        return userController.addProductReview(username, productID, review);
    }

    @Override
    public List<Purchase> getPurchaseHistory(String username) {
        return userController.getPurchaseHistoryContents(username);
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
        return storeController.getStorePurchaseHistory(storeID);
    }

}
