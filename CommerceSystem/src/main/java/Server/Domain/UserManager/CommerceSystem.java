package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Log;
import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.*;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.DTOs.StoreClientDTO;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
import Server.Domain.UserManager.DTOs.BasketClientDTO;
import Server.Domain.UserManager.DTOs.PurchaseClientDTO;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import Server.Service.IService;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class CommerceSystem implements IService {

    private UserController userController;
    private StoreController storeController;
    public static Log log = new Log("Logs.txt");
//    public static Log logCrit = new Log("CriticaldatabaseLogs.txt"); // todo - add this line

    private CommerceSystem() {
        this.userController = UserController.getInstance();
        this.storeController = StoreController.getInstance();
    }

    private static class CreateSafeThreadSingleton {
        private static final CommerceSystem INSTANCE = new CommerceSystem();
    }

    public static CommerceSystem getInstance() {
        return CommerceSystem.CreateSafeThreadSingleton.INSTANCE;
    }

    @Override
    public void init() {
        userController.adminBoot();
//        initState();
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
    public Response<List<StoreClientDTO>> searchByStoreName(String storeName) {
        return storeController.searchByStoreName(storeName);
    }

    @Override
    public Response<List<ProductClientDTO>> searchByProductName(String productName) {
        return storeController.searchByProductName(productName);
    }

    @Override
    public Response<List<ProductClientDTO>> searchByProductCategory(String category) {
        return storeController.searchByCategory(category);
    }

    @Override
    public Response<List<ProductClientDTO>> searchByProductKeyword(String keyword) {
        return storeController.searchByKeyWord(keyword);
    }

    @Override
    public Response<Boolean> addToCart(String username, int storeID, int productID) {
        return userController.addToCart(username, storeID, productID);
    }

    @Override
    public Response<Boolean> removeFromCart(String username, int storeID, int productID) {
        return userController.removeProduct(username, storeID, productID);
    }

    @Override
    public Response<List<BasketClientDTO>> getCartDetails(String username) {
        return userController.getShoppingCartContents(username);
    }

    @Override
    public Response<Boolean> updateProductQuantity(String username, int storeID, int productID, int amount) {
        return userController.updateProductQuantity(username, storeID, productID, amount);
    }

    @Override
    public Response<Boolean> directPurchase(String username, PaymentDetails paymentDetails, SupplyDetails supplyDetails) {
        return userController.purchase(username, paymentDetails, supplyDetails);
    }

    @Override
    public Response<Boolean> bidOffer(String username, int productID, int storeID, double priceOffer) {
        return userController.bidOffer(username, productID, storeID, priceOffer);
    }

    @Override
    public Response<Boolean> bidManagerReply(String username, String offeringUsername, int productID, int storeID, double bidReply) {
        return userController.bidManagerReply(username, offeringUsername, productID, storeID, bidReply);
    }

    @Override
    public Response<Boolean> bidUserReply(String username, int productID, int storeID, PaymentDetails paymentDetails, SupplyDetails supplyDetails) {
        return userController.bidUserReply(username, productID, storeID, paymentDetails, supplyDetails);
    }

    @Override
    public Response<List<Integer>> getStoreOwned(String username) {
        return userController.getStoreOwned(username);
    }

    @Override
    public Response<StoreClientDTO> getStore(int storeID) {
        return storeController.getStore(storeID);
    }

    @Override
    public User getUserByName(String username) {
        return null;
    }

    @Override
    public Response<String> logout(String username) {
        return userController.logout(username);
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
    public Response<List<PurchaseClientDTO>> getPurchaseHistory(String username) {
        return userController.getPurchaseHistoryContents(username);
    }

    @Override
    public Response<Boolean> addProductsToStore(String username, ProductClientDTO productDTO, int amount) {
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
    public Response<PurchasePolicy> getPurchasePolicy(String username, int storeID) {
        return userController.getPurchasePolicy(username, storeID);
    }

    @Override
    public Response<DiscountPolicy> getDiscountPolicy(String username, int storeID) {
        return userController.getDiscountPolicy(username, storeID);
    }

    @Override
    public Response<Boolean> addDiscountRule(String username, int storeID, DiscountRule discountRule) {
        return userController.addDiscountRule(username, storeID, discountRule);
    }

    @Override
    public Response<Boolean> addPurchaseRule(String username, int storeID, PurchaseRule purchaseRule) {
        return userController.addPurchaseRule(username, storeID, purchaseRule);
    }

    @Override
    public Response<Boolean> removeDiscountRule(String username, int storeID, int discountRuleID) {
        return userController.removeDiscountRule(username, storeID, discountRuleID);
    }

    @Override
    public Response<Boolean> removePurchaseRule(String username, int storeID, int purchaseRuleID) {
        return userController.removePurchaseRule(username, storeID, purchaseRuleID);
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
    public Response<List<String>> getUserPermissions(String username, int storeID) {
        return userController.getUserPermissions(username, storeID);
    }

    @Override
    public Response<Double> getTotalSystemRevenue(String username) {
        return userController.getTotalSystemRevenue(username);
    }

    @Override
    public Response<Double> getTotalStoreRevenue(String username, int storeID) {
        return userController.getTotalStoreRevenue(username, storeID);
    }

    @Override
    public Response<Boolean> appointStoreManager(String appointerName, String appointeeName, int storeID) {
        return userController.appointManager(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<Boolean> addPermission(String permitting, int storeId, String permitted, PermissionsEnum permission) {
        return userController.addPermission(permitting, storeId, permitted, permission);
    }

    @Override
    public Response<Boolean> removePermission(String permitting, int storeId, String permitted, PermissionsEnum permission) {
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
    public Response<Collection<PurchaseClientDTO>> getPurchaseDetails(String username, int storeID) {
        return userController.getPurchaseDetails(username, storeID);
    }

    @Override
    public Response<List<PurchaseClientDTO>> getUserPurchaseHistory(String adminName, String username) {
        return userController.getUserPurchaseHistory(adminName, username);
    }

    @Override
    public Response<Collection<PurchaseClientDTO>> getStorePurchaseHistory(String adminName, int storeID) {
        return userController.getStorePurchaseHistory(adminName, storeID);
    }

    public void initState() {

        try {
            File file = new File(System.getProperty("user.dir") + "\\src\\main\\java\\Server\\Domain\\UserManager\\initfile");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            String str = new String(data, StandardCharsets.UTF_8);
            String[] funcs = str.split(";");
            String[] attributes;
            //int guestNum = 1;
            String currUser = addGuest().getResult();
            //int currStoreId;

            for (int i = 0; i < funcs.length; i++){
                if(funcs[i].startsWith("register")){
                    attributes = funcs[i].substring(9).split(", ");

                    register(currUser, attributes[0], attributes[1].substring(0, attributes[1].length() - 1));
                }
                else if(funcs[i].startsWith("login")){
                    attributes = funcs[i].substring(6).split(", ");
                    currUser = login(currUser, attributes[0], attributes[1].substring(0, attributes[1].length() - 1)).getResult();

                }
                else if(funcs[i].startsWith("openStore")){
                    attributes = funcs[i].substring(10).split(", ");
                    //currStoreId =
                    openStore(currUser, attributes[0].substring(0, attributes[0].length() - 1));

                }
                else if(funcs[i].startsWith("appointStoreManager")){
                    attributes = funcs[i].substring(20).split(", ");
                    appointStoreManager(currUser, attributes[0], Integer.parseInt(attributes[1].substring(0, attributes[1].length() - 1)));

                }
                else if(funcs[i].startsWith("logout")){
                    attributes = funcs[i].substring(7).split(", ");
                    currUser = logout(attributes[0].substring(0, attributes[0].length() - 1)).getResult();
                }
                else if(funcs[i].startsWith("addProductsToStore")){
                    attributes = funcs[i].substring(19).split(", ");
                    List<String> categories = stringToList(attributes, 3);
                    List<String> keywords = stringToList(attributes, 3 + categories.size());
                    int offset = 3 + categories.size() + keywords.size();
                    addProductsToStore(currUser, new ProductClientDTO(attributes[0], Integer.parseInt(attributes[1]), Double.parseDouble(attributes[2]), categories, keywords), Integer.parseInt(attributes[offset].substring(0, attributes[offset].length() - 1)));
                }
                else if(funcs[i].startsWith("addPermission")){
                    attributes = funcs[i].substring(14).split(", ");
                    addPermission(currUser, Integer.parseInt(attributes[0]), attributes[1], PermissionsEnum.valueOf(attributes[2].substring(0, attributes[2].length() - 1)));
                }
                else{
                    //todo send to 404 page
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> stringToList(String[] str, int index){
        List<String> lst = new Vector<>();
        str[index] = str[index].substring(1);
        for(int i = index; i < str.length; i++){
            if(str[i].endsWith("]")){
                lst.add(str[i].substring(0, str[i].length() - 1));
                break;
            }
            lst.add(str[i]);
        }
        return lst;
    }
}
