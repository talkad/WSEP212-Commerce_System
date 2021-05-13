package Server.Service;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.*;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import Server.Domain.UserManager.Permissions;
import Server.Domain.UserManager.PurchaseDTO;
import Server.Domain.UserManager.User;

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
    void init(); // 1.1

    /**
     * Guest requirements - 2
     */
    Response<String> addGuest(); // 2.1

    Response<String> removeGuest(String name); // 2.2

    Response<Boolean> register(String identifier, String username, String pwd); // 2.3

    Response<String> login(String identifier, String username, String pwd); // 2.4

//    Response<Collection<Store>>  getContent(); // 2.5 - a

    Response<List<StoreDTO>> searchByStoreName(String storeName); // 2.5 - b

    Response<List<ProductDTO>> searchByProductName(String productName); // 2.6 - a

    Response<List<ProductDTO>> searchByProductCategory(String category); // 2.6 - b

    Response<List<ProductDTO>> searchByProductKeyword(String keyword); // 2.6 - c

    Response<Boolean> addToCart(String username, int storeID, int productID); // 2.7

    Response<Boolean> removeFromCart(String username,  int storeID, int productID); // 2.8 - a

    Response<Map<Integer, Map<ProductDTO, Integer>>> getCartDetails(String username); // 2.8 - b

    Response<Boolean> updateProductQuantity(String username,  int storeID, int productID, int amount); // 2.8 - c

    Response<Boolean> directPurchase(String username, PaymentDetails paymentDetails, SupplyDetails supplyDetails); // 2.9

    User getUserByName(String username); // for tests purposes

    /**
     * Registered requirements - 3
     */
    Response<String> logout(String userName); // 3.1

    Response<Integer> openStore(String username, String storeName); // 3.2

    Response<Boolean> addProductReview(String username, int storeID, int productID, String review); // 3.3

    Response<List<PurchaseDTO>> getPurchaseHistory(String username); // 3.7


    /**
     * Store Owner requirements - 4
     *
     * Store Manager requirements - 5
     * can do all of the functions that store owner do, depends on its permissions
     */
    Response<Boolean> addProductsToStore(String username, ProductDTO productDTO, int amount); // 4.1 - a

    Response<Boolean> removeProductsFromStore(String username, int storeID, int productID, int amount); // 4.1 - b

    Response<Boolean> updateProductInfo(String username, int storeID, int productID, double newPrice, String newName); // 4.1 - c

    Response<PurchasePolicy> getPurchasePolicy(String username, int storeID); // 4.2 - a

    Response<DiscountPolicy> getDiscountPolicy(String username, int storeID); // 4.2 - b

    Response<Boolean> addDiscountRule(String username, int storeID, DiscountRule discountRule); // 4.2 - c

    Response<Boolean> addPurchaseRule(String username, int storeID, PurchaseRule purchaseRule); // 4.2 - d

    Response<Boolean> removeDiscountRule(String username, int storeID, int discountRuleID); // 4.2 - e

    Response<Boolean> removePurchaseRule(String username, int storeID, int purchaseRuleID); // 4.2


    // add policy and edit it ???? 4.2 - c

    Response<Boolean> appointStoreOwner(String appointerName, String appointeeName, int storeID); // 4.3

    Response<Boolean> removeOwnerAppointment(String appointerName, String appointeeName, int storeID);  // 4.4

    Response<Boolean> appointStoreManager(String appointerName, String appointeeName, int storeID); // 4.5

    Response<Boolean> addPermission(String permitting, int storeId, String permitted, Permissions permission); // 4.6

    Response<Boolean> removePermission(String permitting, int storeId, String permitted, Permissions permission); // 4.6

    Response<Boolean> removeManagerAppointment(String appointerName, String appointeeName, int storeID); // 4.7

    Response<List<User>> getStoreWorkersDetails(String username, int storeID); // 4.9

    Response<Collection<PurchaseDTO>> getPurchaseDetails(String username, int storeID); // 4.11

    Response<List<Permissions>> getUserPermissions(String username, int storeID); // for client




    /**
     * System Manager requirements - 6
     */
    Response<List<PurchaseDTO>> getUserPurchaseHistory(String adminName, String username); // 6.4 - a

    Response<Collection<PurchaseDTO>> getStorePurchaseHistory(String adminName, int storeID); // 6.4 - b
}
