package Server.Service;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DiscountPolicy;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.PurchasePolicy;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
import Server.Domain.UserManager.CommerceSystem;
import Server.Domain.UserManager.DTOs.BasketClientDTO;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import Server.Domain.UserManager.DTOs.PurchaseClientDTO;
import Server.Domain.UserManager.PermissionsEnum;
import Server.Domain.UserManager.User;
import Server.Domain.ShoppingManager.DTOs.StoreClientDTO;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * this class also serves as the real Bridge in implementing the acceptance testings
 */
public class CommerceService implements IService{

    private final CommerceSystem commerceSystem;
    private CommerceService()
    {
        commerceSystem = CommerceSystem.getInstance();
    }

    // Inner class to provide instance of class
    private static class CreateThreadSafeSingleton
    {
        private static final CommerceService INSTANCE = new CommerceService();
    }

    public static CommerceService getInstance()
    {
        return CreateThreadSafeSingleton.INSTANCE;
    }


    @Override
    public Response<Boolean> init() {
        return commerceSystem.init();
    }

    @Override
    public Response<Boolean> initState(String filename) {
        return commerceSystem.initState(filename);
    }

    @Override
    public Response<Boolean> configInit() {
        return commerceSystem.configInit();
    }

    @Override
    public Response<String> addGuest() {
        return commerceSystem.addGuest();
    }

    @Override
    public Response<String> removeGuest(String name) {
        return commerceSystem.removeGuest(name);
    }

    @Override
    public Response<Boolean> register(String prevName, String username, String pwd) {
        return commerceSystem.register(prevName, username, pwd);
    }

    @Override
    public Response<String> login(String prevName, String username, String pwd) {
        return commerceSystem.login(prevName, username, pwd);
    }

//    @Override
//    public Response<Collection<StoreDTO>> getContent() {
//        return commerceSystem.getContent();
//    }

    @Override
    public Response<List<StoreClientDTO>> searchByStoreName(String storeName) {
        return commerceSystem.searchByStoreName(storeName);
    }

    @Override
    public Response<List<ProductClientDTO>> searchByProductName(String productName) {
        return commerceSystem.searchByProductName(productName);
    }

    @Override
    public Response<List<ProductClientDTO>> searchByProductCategory(String category) {
        return commerceSystem.searchByProductCategory(category);
    }

    @Override
    public Response<List<ProductClientDTO>> searchByProductKeyword(String keyword) {
        return commerceSystem.searchByProductKeyword(keyword);
    }

    @Override
    public Response<Boolean> addToCart(String username, int storeID, int productID) {
        return CommerceSystem.getInstance().addToCart(username, storeID, productID);
    }

    @Override
    public Response<Boolean> removeFromCart(String username,  int storeID, int productID) {
        return CommerceSystem.getInstance().removeFromCart(username, storeID, productID);
    }

    @Override
    public Response<List<BasketClientDTO>> getCartDetails(String username) {
        return CommerceSystem.getInstance().getCartDetails(username);
    }

    @Override
    public Response<Boolean> updateProductQuantity(String username, int storeID, int productID, int amount){
        return CommerceSystem.getInstance().updateProductQuantity(username, storeID, productID, amount);
    }

    @Override
    public Response<Boolean> directPurchase(String username, PaymentDetails paymentDetails, SupplyDetails supplyDetails) {
        return commerceSystem.directPurchase(username, paymentDetails, supplyDetails);
    }

    @Override
    public Response<Boolean> bidOffer(String username, int productID, int storeID, double priceOffer) {
        return commerceSystem.bidOffer(username, productID, storeID, priceOffer);
    }

    @Override
    public Response<Boolean> bidManagerReply(String username, String offeringUsername, int productID, int storeID, double bidReply) {
        return commerceSystem.bidManagerReply(username,offeringUsername, productID, storeID, bidReply);
    }

    @Override
    public Response<Boolean> bidUserReply(String username, int productID, int storeID, PaymentDetails paymentDetails, SupplyDetails supplyDetails) {
        return commerceSystem.bidUserReply(username, productID, storeID, paymentDetails, supplyDetails);
    }

    @Override
    public Response<List<Integer>> getStoreOwned(String username) {
        return commerceSystem.getStoreOwned(username);
    }

    @Override
    public Response<StoreClientDTO> getStore(int storeID) {
        return commerceSystem.getStore(storeID);
    }

    @Override
    public User getUserByName(String username) {
        return commerceSystem.getUserByName(username);
    }

    @Override
    public Response<String> logout(String userName) {
        return commerceSystem.logout(userName);
    }

    @Override
    public Response<Integer> openStore(String username, String storeName) {
        return commerceSystem.openStore(username, storeName);
    }

    @Override
    public Response<Boolean> addProductReview(String username, int storeID, int productID, String review) {
        return commerceSystem.addProductReview(username, storeID, productID, review);
    }

    @Override
    public Response<List<PurchaseClientDTO>> getPurchaseHistory(String username) {
        return commerceSystem.getPurchaseHistory(username);
    }

    @Override
    public Response<Boolean> addProductsToStore(String username, ProductClientDTO productDTO, int amount) {
        return CommerceSystem.getInstance().addProductsToStore(username, productDTO, amount);
    }

    @Override
    public Response<Boolean> removeProductsFromStore(String username, int storeID, int productID, int amount) {
        return CommerceSystem.getInstance().removeProductsFromStore(username, storeID, productID, amount);
    }

    @Override
    public Response<Boolean> updateProductInfo(String username, int storeID, int productID, double newPrice, String newName) {
        return commerceSystem.updateProductInfo(username, storeID, productID, newPrice, newName);
    }

    @Override
    public Response<String> getPurchasePolicy(String username, int storeID) {
        return commerceSystem.getPurchasePolicy(username, storeID);
    }

    @Override
    public Response<String> getDiscountPolicy(String username, int storeID) {
        return commerceSystem.getDiscountPolicy(username, storeID);
    }

    @Override
    public Response<PurchasePolicy> getPurchasePolicyReal(String username, int storeID) {
        return commerceSystem.getPurchasePolicyReal(username, storeID);
    }

    @Override
    public Response<DiscountPolicy> getDiscountPolicyReal(String username, int storeID) {
        return commerceSystem.getDiscountPolicyReal(username, storeID);
    }

    @Override
    public Response<Boolean> addDiscountRule(String username, int storeID, DiscountRule discountRule){
        return commerceSystem.addDiscountRule(username, storeID, discountRule);
    }

    @Override
    public Response<Boolean> addPurchaseRule(String username, int storeID, PurchaseRule purchaseRule) {
        return commerceSystem.addPurchaseRule(username, storeID, purchaseRule);
    }

    @Override
    public Response<Boolean> removeDiscountRule(String username, int storeID, int discountRuleID)
    {
        return commerceSystem.removeDiscountRule(username, storeID, discountRuleID);
    }

    @Override
    public Response<Boolean> removePurchaseRule(String username, int storeID, int purchaseRuleID)
    {
        return commerceSystem.removePurchaseRule(username, storeID, purchaseRuleID);
    }

    @Override
    public Response<Boolean> appointStoreOwner(String appointerName, String appointeeName, int storeID) {
        return commerceSystem.appointStoreOwner(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<Boolean> removeOwnerAppointment(String appointerName, String appointeeName, int storeID) {
        return commerceSystem.removeOwnerAppointment(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<Boolean> appointStoreManager(String appointerName, String appointeeName, int storeID) {
        return commerceSystem.appointStoreManager(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<Boolean> addPermission(String permitting, int storeId, String permitted, PermissionsEnum permission) {
        return commerceSystem.addPermission(permitting, storeId, permitted, permission);
    }

    @Override
    public Response<Boolean> removePermission(String permitting, int storeId, String permitted, PermissionsEnum permission) {
        return commerceSystem.removePermission(permitting, storeId, permitted, permission);
    }

    @Override
    public Response<Boolean> removeManagerAppointment(String appointerName, String appointeeName, int storeID) {
        return commerceSystem.removeManagerAppointment(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<List<String>> getUserPermissions(String username, int storeID){
        return commerceSystem.getUserPermissions(username, storeID);
    }

    @Override
    public Response<Double> getTotalSystemRevenue(String username) {
        return commerceSystem.getTotalSystemRevenue(username);
    }

    @Override
    public Response<Double> getTotalStoreRevenue(String username, int storeID) {
        return commerceSystem.getTotalStoreRevenue(username, storeID);
    }

    @Override
    public Response<List<User>> getStoreWorkersDetails(String username, int storeID) {
        return commerceSystem.getStoreWorkersDetails(username, storeID);
    }

    @Override
    public Response<Collection<PurchaseClientDTO>> getPurchaseDetails(String username, int storeID) {
        return commerceSystem.getPurchaseDetails(username, storeID);
    }

    @Override
    public Response<List<PurchaseClientDTO>> getUserPurchaseHistory(String adminName, String username) {
        return commerceSystem.getUserPurchaseHistory(adminName, username);
    }

    @Override
    public Response<Collection<PurchaseClientDTO>> getStorePurchaseHistory(String adminName, int storeID) {
        return commerceSystem.getStorePurchaseHistory(adminName, storeID);
    }

    @Override
    public Response<List<String>> getDailyStatistics(String adminName, LocalDate date){
        return commerceSystem.getDailyStatistics(adminName, date);
    }

    @Override
    public Response<Boolean> isAdmin(String username) {
        return commerceSystem.isAdmin(username);
    }
}
