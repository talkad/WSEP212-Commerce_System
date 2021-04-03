package Server.Service;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DiscountPolicy;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.PurchasePolicy;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.UserManager.*;

import java.util.List;
import java.util.Map;


/**
 * this class also serves as the real Bridge in implementing the acceptance testings
 */
public class CommerceService implements IService{
    @Override
    public void init() {
        CommerceSystem.getInstance().init();
    }

    @Override
    public Response<Boolean> register(String prevName, String username, String pwd) {
        return CommerceSystem.getInstance().register(prevName, username, pwd);
    }

    @Override
    public Response<String> login(String prevName, String username, String pwd) {
        return CommerceSystem.getInstance().login(prevName, username, pwd);
    }

    @Override
    public List<Store> getContent() {
        return CommerceSystem.getInstance().getContent();
    }

    @Override
    public List<Product> searchByProductName(String productName) {
        return CommerceSystem.getInstance().searchByProductName(productName);
    }

    @Override
    public List<Product> searchByProductCategory(String category) {
        return CommerceSystem.getInstance().searchByProductCategory(category);
    }

    @Override
    public List<Product> searchByProductKeyword(String keyword) {
        return CommerceSystem.getInstance().searchByProductKeyword(keyword);
    }

    @Override
    public Response<Boolean> addToCart(String username, Product product) {
        return CommerceSystem.getInstance().addToCart(username, product);
    }

    @Override
    public Response<Boolean> removeFromCart(String username, Product product) {
        return CommerceSystem.getInstance().removeFromCart(username, product);
    }

    @Override
    public Map<Integer, Map<Product, Integer>> getCartDetails(String username) {
        return CommerceSystem.getInstance().getCartDetails(username);
    }

    @Override
    public Response<Boolean> updateProductQuantity(String username, Product product, int amount) {
        return CommerceSystem.getInstance().updateProductQuantity(username, product, amount);
    }

    @Override
    public Response<Boolean> purchaseCartItems(String username, String creditCardDetails) {
        return CommerceSystem.getInstance().purchaseCartItems(username, creditCardDetails);
    }

    @Override
    public User getUserByName(String username) {
        return CommerceSystem.getInstance().getUserByName(username);
    }

    @Override
    public Response<String> logout(String userName) {
        return CommerceSystem.getInstance().logout(userName);
    }

    @Override
    public Response<Integer> openStore(String username, String storeName) {
        return CommerceSystem.getInstance().openStore(username, storeName);
    }

    @Override
    public Response<Boolean> addProductReview(String username, int productID, String review) {
        return CommerceSystem.getInstance().addProductReview(username, productID, review);
    }

    @Override
    public List<Purchase> getPurchaseHistory(String username) {
        return CommerceSystem.getInstance().getPurchaseHistory(username);
    }

    @Override
    public Response<Boolean> addProductsToStore(String username, int storeID, Product product, int amount) {
        return CommerceSystem.getInstance().addProductsToStore(username, storeID, product, amount);
    }

    @Override
    public Response<Boolean> removeProductsFromStore(String username, int storeID, Product product, int amount) {
        return CommerceSystem.getInstance().removeProductsFromStore(username, storeID, product, amount);
    }

    @Override
    public Response<Boolean> updateProductInfo(String username, int storeID, int productID, double newPrice, String newName) {
        return CommerceSystem.getInstance().updateProductInfo(username, storeID, productID, newPrice, newName);
    }

    @Override
    public List<PurchasePolicy> getPurchasePolicy(String username, int storeID) {
        return CommerceSystem.getInstance().getPurchasePolicy(username, storeID);
    }

    @Override
    public List<DiscountPolicy> getDiscountPolicy(String username, int storeID) {
        return CommerceSystem.getInstance().getDiscountPolicy(username, storeID);
    }

    @Override
    public Response<Boolean> appointStoreOwner(String appointerName, String appointeeName, int storeID) {
        return CommerceSystem.getInstance().appointStoreOwner(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<Boolean> removeOwnerAppointment(String appointerName, String appointeeName, int storeID) {
        return CommerceSystem.getInstance().removeOwnerAppointment(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<Boolean> appointStoreManager(String appointerName, String appointeeName, int storeID) {
        return CommerceSystem.getInstance().appointStoreManager(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<Boolean> addPermission(String permitting, int storeId, String permitted, Permissions permission) {
        return CommerceSystem.getInstance().addPermission(permitting, storeId, permitted, permission);
    }

    @Override
    public Response<Boolean> removeManagerAppointment(String appointerName, String appointeeName, int storeID) {
        return CommerceSystem.getInstance().removeManagerAppointment(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<UserDetails> getWorkersDetails(String username, int storeID) {
        return CommerceSystem.getInstance().getWorkersDetails(username, storeID);
    }

    @Override
    public Response<Purchase> getPurchaseDetails(String username, int storeID) {
        return CommerceSystem.getInstance().getPurchaseDetails(username, storeID);
    }

    @Override
    public Response<List<Purchase>> getUserPurchaseHistory(String adminName, String username) {
        return CommerceSystem.getInstance().getUserPurchaseHistory(adminName, username);
    }

    @Override
    public Response<List<Purchase>> getStorePurchaseHistory(String adminName, int storeID) {
        return CommerceSystem.getInstance().getStorePurchaseHistory(adminName, storeID);
    }
}
