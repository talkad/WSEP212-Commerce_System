package Server.Domain.UserManager;

import Server.DAL.DALControllers.DALService;
import Server.DAL.DALControllers.DALTestService;
import Server.DAL.DomainDTOs.UserDTO;
import Server.Domain.CommonClasses.Log;
import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.*;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.DTOs.StoreClientDTO;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
import Server.Domain.UserManager.DTOs.BasketClientDTO;
import Server.Domain.UserManager.DTOs.PurchaseClientDTO;
import Server.Domain.UserManager.ExternalSystemsAdapters.ExternalSystemsConnection;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import Server.Service.IService;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;


public class CommerceSystem implements IService {
    
    public static Log log = new Log("Logs.txt");
    public static Log logCrit = new Log("CriticaldatabaseLogs.txt");


    private CommerceSystem() {

    }

    private static class CreateSafeThreadSingleton {
        private static final CommerceSystem INSTANCE = new CommerceSystem();
    }

    public static CommerceSystem getInstance() {
        return CommerceSystem.CreateSafeThreadSingleton.INSTANCE;
    }

    @Override
    public Response<Boolean> init() {
        Response<Boolean> responseConfig;
        Response<Boolean> responseInit;

        responseConfig = configInit("configfile.json");
        if(responseConfig.isFailure())
            return new Response<>(false, true, "initialization failed due to error in config (CRITICAL)");

        // Start threads in DAL, responsible for saving data in DB and cleaning cache
        DALService.getInstance().startDB();

        // for testing
        DALService.getInstance().resetDatabase(); //todo - remove that

        if(DALService.getInstance().getStore(0) != null){
            System.out.println(" -------------- System initialization already occurred in the past -------------- ");
            return new Response<>(true, false, "initialization complete");
        }

        responseInit = initState("initfileforpresentation");
        if(responseInit.isFailure())
            return new Response<>(false, true, "initialization failed due to error in init (CRITICAL)");

        return new Response<>(true, false, "initialization complete");
    }

    @Override
    public Response<String> addGuest() {
        return UserController.getInstance().addGuest();
    }

    @Override
    public Response<String> removeGuest(String name) {
        return UserController.getInstance().removeGuest(name);
    }

    @Override
    public Response<Boolean> register(String prevName, String username, String pwd) {
        return UserController.getInstance().register(prevName, username, pwd);
    }

    @Override
    public Response<String> login(String prevName, String username, String pwd) {
        return UserController.getInstance().login(prevName, username, pwd);
    }

    @Override
    public Response<List<StoreClientDTO>> searchByStoreName(String storeName) {
        return StoreController.getInstance().searchByStoreName(storeName);
    }

    @Override
    public Response<List<ProductClientDTO>> searchByProductName(String productName) {
        return StoreController.getInstance().searchByProductName(productName);
    }

    @Override
    public Response<List<ProductClientDTO>> searchByProductCategory(String category) {
        return StoreController.getInstance().searchByCategory(category);
    }

    @Override
    public Response<List<ProductClientDTO>> searchByProductKeyword(String keyword) {
        return StoreController.getInstance().searchByKeyWord(keyword);
    }

    @Override
    public Response<Boolean> addToCart(String username, int storeID, int productID) {
        return UserController.getInstance().addToCart(username, storeID, productID);
    }

    @Override
    public Response<Boolean> removeFromCart(String username, int storeID, int productID) {
        return UserController.getInstance().removeProduct(username, storeID, productID);
    }

    @Override
    public Response<List<BasketClientDTO>> getCartDetails(String username) {
        return UserController.getInstance().getShoppingCartContents(username);
    }

    @Override
    public Response<Boolean> updateProductQuantity(String username, int storeID, int productID, int amount) {
        return UserController.getInstance().updateProductQuantity(username, storeID, productID, amount);
    }

    @Override
    public Response<Boolean> directPurchase(String username, PaymentDetails paymentDetails, SupplyDetails supplyDetails) {
        return UserController.getInstance().purchase(username, paymentDetails, supplyDetails);
    }

    @Override
    public Response<Boolean> bidOffer(String username, int productID, int storeID, double priceOffer) {
        return UserController.getInstance().bidOffer(username, productID, storeID, priceOffer);
    }

    @Override
    public Response<Boolean> bidManagerReply(String username, String offeringUsername, int productID, int storeID, double bidReply) {
        return UserController.getInstance().bidManagerReply(username, offeringUsername, productID, storeID, bidReply);
    }

    @Override
    public Response<Boolean> bidUserReply(String username, int productID, int storeID, PaymentDetails paymentDetails, SupplyDetails supplyDetails) {
        return UserController.getInstance().bidUserReply(username, productID, storeID, paymentDetails, supplyDetails);
    }

    @Override
    public Response<List<Integer>> getStoreOwned(String username) {
        return UserController.getInstance().getStoreOwned(username);
    }

    @Override
    public Response<List<String>> getMyStores(String username) {
        return UserController.getInstance().getMyStores(username);
    }

    @Override
    public Response<StoreClientDTO> getStore(int storeID) {
        return StoreController.getInstance().getStore(storeID);
    }

    @Override
    public User getUserByName(String username) {
        return UserController.getInstance().getUserByName(username);
    }

    @Override
    public Response<String> logout(String username) {
        return UserController.getInstance().logout(username);
    }

    @Override
    public Response<Integer> openStore(String username, String storeName) {
        return UserController.getInstance().openStore(username, storeName);
    }

    @Override
    public Response<Boolean> addProductReview(String username, int storeID, int productID, String review) {
        return UserController.getInstance().addProductReview(username, storeID, productID, review);
    }

    @Override
    public Response<List<PurchaseClientDTO>> getPurchaseHistory(String username) {
        return UserController.getInstance().getPurchaseHistoryContents(username);
    }

    @Override
    public Response<Boolean> addProductsToStore(String username, ProductClientDTO productDTO, int amount) {
        return UserController.getInstance().addProductsToStore(username, productDTO, amount);
    }

    @Override
    public Response<Boolean> removeProductsFromStore(String username, int storeID, int productID, int amount) {
        return UserController.getInstance().removeProductsFromStore(username, storeID, productID, amount);
    }

    @Override
    public Response<Boolean> updateProductInfo(String username, int storeID, int productID, double newPrice, String newName) {
        return UserController.getInstance().updateProductInfo(username, storeID, productID, newPrice, newName);
    }

    @Override
    public Response<String> getPurchasePolicy(String username, int storeID) {
        return UserController.getInstance().getPurchasePolicy(username, storeID);
    }

    @Override
    public Response<String> getDiscountPolicy(String username, int storeID) {
        return UserController.getInstance().getDiscountPolicy(username, storeID);
    }

    @Override
    public Response<PurchasePolicy> getPurchasePolicyReal(String username, int storeID) {
        return UserController.getInstance().getPurchasePolicyReal(username, storeID);
    }

    @Override
    public Response<DiscountPolicy> getDiscountPolicyReal(String username, int storeID) {
        return UserController.getInstance().getDiscountPolicyReal(username, storeID);
    }

    @Override
    public Response<Boolean> addDiscountRule(String username, int storeID, DiscountRule discountRule) {
        return UserController.getInstance().addDiscountRule(username, storeID, discountRule);
    }

    @Override
    public Response<Boolean> addPurchaseRule(String username, int storeID, PurchaseRule purchaseRule) {
        return UserController.getInstance().addPurchaseRule(username, storeID, purchaseRule);
    }

    @Override
    public Response<Boolean> removeDiscountRule(String username, int storeID, int discountRuleID) {
        return UserController.getInstance().removeDiscountRule(username, storeID, discountRuleID);
    }

    @Override
    public Response<Boolean> removePurchaseRule(String username, int storeID, int purchaseRuleID) {
        return UserController.getInstance().removePurchaseRule(username, storeID, purchaseRuleID);
    }

    @Override
    public Response<Boolean> appointStoreOwner(String appointerName, String appointeeName, int storeID) {
        return UserController.getInstance().appointOwner(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<Boolean> removeOwnerAppointment(String appointerName, String appointeeName, int storeID) {
        return UserController.getInstance().removeOwnerAppointment(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<List<String>> getUserPermissions(String username, int storeID) {
        return UserController.getInstance().getUserPermissions(username, storeID);
    }

    @Override
    public Response<Double> getTotalSystemRevenue(String username) {
        return UserController.getInstance().getTotalSystemRevenue(username);
    }

    @Override
    public Response<List<String>> getDailyStatistics(String adminName, LocalDate date) {
        return UserController.getInstance().getDailyStatistics(adminName, date);
    }

    @Override
    public Response<Boolean> isAdmin(String username) {
        return UserController.getInstance().isAdmin(username);
    }

    @Override
    public Response<Double> getTotalStoreRevenue(String username, int storeID) {
        return UserController.getInstance().getTotalStoreRevenue(username, storeID);
    }

    @Override
    public Response<Boolean> appointStoreManager(String appointerName, String appointeeName, int storeID) {
        return UserController.getInstance().appointManager(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<Boolean> addPermission(String permitting, int storeId, String permitted, PermissionsEnum permission) {
        return UserController.getInstance().addPermission(permitting, storeId, permitted, permission);
    }

    @Override
    public Response<Boolean> removePermission(String permitting, int storeId, String permitted, PermissionsEnum permission) {
        return UserController.getInstance().removePermission(permitting, storeId, permitted, permission);
    }

    @Override
    public Response<Boolean> removeManagerAppointment(String appointerName, String appointeeName, int storeID) {
        return UserController.getInstance().removeManagerAppointment(appointerName, appointeeName, storeID);
    }

    @Override
    public Response<List<UserDTO>> getStoreWorkersDetails(String username, int storeID) {
        return UserController.getInstance().getStoreWorkersDetails(username, storeID);
    }

    @Override
    public Response<Collection<PurchaseClientDTO>> getPurchaseDetails(String username, int storeID) {
        return UserController.getInstance().getPurchaseDetails(username, storeID);
    }

    @Override
    public Response<List<PurchaseClientDTO>> getUserPurchaseHistory(String adminName, String username) {
        return UserController.getInstance().getUserPurchaseHistory(adminName, username);
    }

    @Override
    public Response<Collection<PurchaseClientDTO>> getStorePurchaseHistory(String adminName, int storeID) {
        return UserController.getInstance().getStorePurchaseHistory(adminName, storeID);
    }

    public Response<Boolean> configInit(String filename){
        Gson gson = new Gson();
        try {
            Path pathToFile = Paths.get(System.getProperty("user.dir") + "\\src\\main\\java\\Server\\Domain\\UserManager\\initFiles\\" + filename);
            byte [] jsonBytes = Files.readAllBytes(pathToFile);
            String jsonString = new String(jsonBytes);

            Properties data = gson.fromJson(jsonString, Properties.class);

            // check connection to remote db
            String dbloc = data.getProperty("dbloc");
            String dbName = data.getProperty("dbname");

            DALService conDB = DALService.getInstance();
            conDB.setURL(dbloc);
            conDB.setName(dbName);

            DALTestService testconDB = DALTestService.getInstance();
            testconDB.setURL(dbloc);
            testconDB.setName(dbName);

            if(!conDB.checkConnection())
                return new Response<>(false, true, "DB Connection failed (CRITICAL)");

            // initiate admin
            String adminUsername = data.getProperty("adminuser");
            String adminPassword = data.getProperty("adminpass");

            UserController.getInstance().adminBoot(adminUsername, adminPassword);

            // check connection to external systems
            String extsysloc = data.getProperty("extsysloc");

            ExternalSystemsConnection con = ExternalSystemsConnection.getInstance();
            con.setSysLoc(extsysloc);
            boolean extSysRes = con.checkConnection();
            if(extSysRes)
                con.closeConnection();
            else
                return new Response<>(false, true, "External System connection failed. (CRITICAL)");

            return new Response<>(true,false,"System configured successfully.");
        }
        catch(Exception e) {
            return new Response<>(false, true, "Error with config file. (CRITICAL)");
        }
    }


    public Response<Boolean> initState(String filename) {
        try {
            File file;
            if(filename != null){
                file = new File(System.getProperty("user.dir") + "\\src\\main\\java\\Server\\Domain\\UserManager\\initFiles\\" + filename);
            }
            else {
                file = new File(System.getProperty("user.dir") + "\\src\\main\\java\\Server\\Domain\\UserManager\\initFiles\\initfile");
            }
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            String str = new String(data, StandardCharsets.UTF_8);
            if(str.length() == 0){
                return new Response<>(true, false, "Successfully initialized the system with the initialization file");
            }
            String[] funcs = str.split(";");
            String[] attributes;
            String currUser = addGuest().getResult();

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
                    openStore(attributes[0], attributes[1].substring(0, attributes[1].length() - 1));

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
                    return new Response<>(false, true, "CRITICAL Error: Bad initfile");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Response<>(false, true, "init: unexpected syntax");
        }
        return new Response<>(true, false, "Successfully initialized the system with the initialization file");
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
