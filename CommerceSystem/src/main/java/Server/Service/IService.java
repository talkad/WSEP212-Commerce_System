package Server.Service;

import Server.DAL.UserDTO;
import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.*;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.DTOs.StoreClientDTO;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
import Server.Domain.UserManager.DTOs.BasketClientDTO;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import Server.Domain.UserManager.PermissionsEnum;
import Server.Domain.UserManager.DTOs.PurchaseClientDTO;
import Server.Domain.UserManager.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * this class also serves as the Bridge in implementing the acceptance testings
 */
public interface IService {

    /**
     * System requirements - 1
     */
    Response<Boolean> init(); // 1.1

    Response<Boolean> initState(String filename);

    Response<Boolean> configInit();

        /**
         * Guest requirements - 2
         */
    Response<String> addGuest(); // 2.1

    Response<String> removeGuest(String name); // 2.2

    Response<Boolean> register(String identifier, String username, String pwd); // 2.3

    Response<String> login(String identifier, String username, String pwd); // 2.4

    Response<List<StoreClientDTO>> searchByStoreName(String storeName); // 2.5 - b

    Response<List<ProductClientDTO>> searchByProductName(String productName); // 2.6 - a

    Response<List<ProductClientDTO>> searchByProductCategory(String category); // 2.6 - b

    Response<List<ProductClientDTO>> searchByProductKeyword(String keyword); // 2.6 - c

    Response<Boolean> addToCart(String username, int storeID, int productID); // 2.7

    Response<Boolean> removeFromCart(String username,  int storeID, int productID); // 2.8 - a

    Response<List<BasketClientDTO>> getCartDetails(String username); // 2.8 - b

    Response<Boolean> updateProductQuantity(String username,  int storeID, int productID, int amount); // 2.8 - c

    Response<Boolean> directPurchase(String username, PaymentDetails paymentDetails, SupplyDetails supplyDetails); // 2.9

    Response<Boolean> bidOffer(String username, int productID, int storeID, double priceOffer);

    Response<Boolean> bidUserReply(String username, int productID, int storeID, PaymentDetails paymentDetails, SupplyDetails supplyDetails);

    Response<List<Integer>> getStoreOwned(String username);

    Response<StoreClientDTO> getStore(int storeID);

    User getUserByName(String username); // for tests purposes


    /**
     * Registered requirements - 3
     */
    Response<String> logout(String userName); // 3.1

    Response<Integer> openStore(String username, String storeName); // 3.2

    Response<Boolean> addProductReview(String username, int storeID, int productID, String review); // 3.3

    Response<List<PurchaseClientDTO>> getPurchaseHistory(String username); // 3.7


    /**
     * Store Owner requirements - 4
     *
     * Store Manager requirements - 5
     * can do all of the functions that store owner do, depends on its permissions
     */
    Response<Boolean> addProductsToStore(String username, ProductClientDTO productDTO, int amount); // 4.1 - a

    Response<Boolean> removeProductsFromStore(String username, int storeID, int productID, int amount); // 4.1 - b

    Response<Boolean> updateProductInfo(String username, int storeID, int productID, double newPrice, String newName); // 4.1 - c

    Response<String> getPurchasePolicy(String username, int storeID); // 4.2 - a

    Response<String> getDiscountPolicy(String username, int storeID); // 4.2 - b

    // for tests
    Response<PurchasePolicy> getPurchasePolicyReal(String username, int storeID); // 4.2 - a

    Response<DiscountPolicy> getDiscountPolicyReal(String username, int storeID); // 4.2 - b

    Response<Boolean> addDiscountRule(String username, int storeID, DiscountRule discountRule); // 4.2 - c

    Response<Boolean> addPurchaseRule(String username, int storeID, PurchaseRule purchaseRule); // 4.2 - d

    Response<Boolean> removeDiscountRule(String username, int storeID, int discountRuleID); // 4.2 - e

    Response<Boolean> removePurchaseRule(String username, int storeID, int purchaseRuleID); // 4.2

    Response<Boolean> appointStoreOwner(String appointerName, String appointeeName, int storeID); // 4.3

    Response<Boolean> removeOwnerAppointment(String appointerName, String appointeeName, int storeID);  // 4.4

    Response<Boolean> appointStoreManager(String appointerName, String appointeeName, int storeID); // 4.5

    Response<Boolean> addPermission(String permitting, int storeID, String permitted, PermissionsEnum permission); // 4.6

    Response<Boolean> removePermission(String permitting, int storeID, String permitted, PermissionsEnum permission); // 4.6

    Response<Boolean> removeManagerAppointment(String appointerName, String appointeeName, int storeID); // 4.7

    Response<List<UserDTO>> getStoreWorkersDetails(String username, int storeID); // 4.9

    Response<Collection<PurchaseClientDTO>> getPurchaseDetails(String username, int storeID); // 4.11

    Response<List<String>> getUserPermissions(String username, int storeID); // for client

    Response<Double> getTotalStoreRevenue(String username, int storeID);

    Response<Boolean> bidManagerReply(String username, String offeringUsername, int productID, int storeID, double bidReply);


    /**
     * System Manager requirements - 6
     */
    Response<List<PurchaseClientDTO>> getUserPurchaseHistory(String adminName, String username); // 6.4 - a

    Response<Collection<PurchaseClientDTO>> getStorePurchaseHistory(String adminName, int storeID); // 6.4 - b

    Response<Double> getTotalSystemRevenue(String username);

    Response<List<String>> getDailyStatistics(String adminName, LocalDate date);

    Response<Boolean> isAdmin(String username);
}
