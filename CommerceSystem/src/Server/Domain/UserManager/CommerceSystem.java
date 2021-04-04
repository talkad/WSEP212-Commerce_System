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
        userController.adminBoot();
    }

    @Override
    public Response<String> addGuest() {
        return userController.addGuest();
    }

    @Override
    public Response<String> removeGuest(String name) {
        return userController.removeGuest(name);
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
    public Response<Boolean> addToCart(String username, int storeID, int productID) {
        return userController.addToCart(username, storeID, productID);
    }

    @Override
    public Response<Boolean> removeFromCart(String username,  int storeID, int productID) {
        return userController.removeProduct(username, storeID, productID);
    }

    @Override
    public Response<Map<Integer, Map<ProductDTO, Integer>>> getCartDetails(String username) {
        return userController.getShoppingCartContents(username);
    }

    @Override
    public Response<Boolean> updateProductQuantity(String username,  int storeID, int productID, int amount) {
        return userController.updateProductQuantity(username, storeID, productID, amount);
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
    public Response<Boolean> addProductReview(String username, int storeID, int productID, String review) {
        return userController.addProductReview(username, storeID, productID, review);
    }

    @Override
    public Response<List<Purchase>> getPurchaseHistory(String username) {
        return userController.getPurchaseHistoryContents(username);
    }

    @Override
    public Response<Boolean> addProductsToStore(String username, ProductDTO productDTO, int amount) {
        return userController.addProductsToStore(username, productDTO, amount);
    }

    @Override
    public Response<Boolean> removeProductsFromStore(String username, int storeID, int productID, int amount) {
        return userController.removeProductsFromStore(username, storeID, productID, amount);
    }

    @Override
    public Response<Boolean> updateProductInfo(String username, int storeID, int productID, double newPrice, String newName) {
        return userController.updateProductInfo(username, storeID, productID, newPrice, newName);
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
        return userController.appointOwner(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<Boolean> removeOwnerAppointment(String appointerName, String appointeeName, int storeID) {
        return userController.removeOwnerAppointment(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<Boolean> appointStoreManager(String appointerName, String appointeeName, int storeID) {
        return userController.appointManager(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<Boolean> addPermission(String permitting, int storeId, String permitted, Permissions permission) {
        return userController.addPermission(permitting, storeId, permitted, permission);
    }

    @Override
    public Response<Boolean> removePermission(String permitting, int storeId, String permitted, Permissions permission) {
        return userController.removePermission(permitting, storeId, permitted, permission);
    }

    @Override
    public Response<Boolean> removeManagerAppointment(String appointerName, String appointeeName, int storeID) {
        return userController.removeManagerAppointment(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<List<User>> getStoreWorkersDetails(String username, int storeID) {
        return userController.getStoreWorkersDetails(username, storeID);
    }

    @Override
    public Response<Purchase> getPurchaseDetails(String username, int storeID) {
        return userController.getPurchaseDetails(username, storeID);
    }

    @Override
    public Response<List<Purchase>> getUserPurchaseHistory(String adminName, String username) {
        return userController.getUserPurchaseHistory(adminName, username);
    }

    @Override
    public Response<Map<ProductDTO, Integer>> getStorePurchaseHistory(String adminName, int storeID) {
        return userController.getStorePurchaseHistory(adminName, storeID);
    }

}
