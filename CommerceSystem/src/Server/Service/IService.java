package Server.Service;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DiscountPolicy;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.PurchasePolicy;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.UserManager.Purchase;
import Server.Domain.UserManager.User;
import Server.Domain.UserManager.UserDetails;

import java.util.List;
import java.util.Map;

public interface IService {

    /**
     * System requirements - 1
     */
    void init(); // 1.1


    /**
     * Guest requirements - 2
     */
    // 2.1 ?

    // 2.2 ?

    Response<Boolean> register(String username, String pwd); // 2.3

    Response<Boolean> login(String username, String pwd); // 2.4

    List<Store>  getContent(); // 2.5

    List<Product> searchByProductName(String productName); // 2.6 - a

    List<Product> searchByProductCategory(String category); // 2.6 - b

    List<Product> searchByProductKeyword(String keyword); // 2.6 - c

    // filtering of the results by some defined criteria - JONATAN?

    Response<Boolean> addToCart(String username, Product product); // 2.7

    Response<Boolean> removeFromCart(String username, Product product); // 2.8 - a

    Map<Integer, Map<Product, Integer>> getCartDetails(String username); // 2.8 - b

    Response<Boolean> purchaseCartItems(String username, String creditCardDetails); // 2.9

    User getUserByName(String username); // for tests purposes


    /**
     * Registered requirements - 3
     */
    Response<Boolean> logout(String userName); // 3.1

    Response<Boolean> openStore(String username, String storeName); // 3.2

    Response<Boolean> addProductReview(String username, int productID, String review); // 3.3

    List<Purchase> getPurchaseHistory(String username); // 3.7


    /**
     * Store Owner requirements - 4
     *
     * Store Manager requirements - 5
     * can do all of the functions that store owner do, depends on its permissions
     */
    Response<Boolean> addProductsToStore(String username, int storeID, Product product, int amount); // 4.1 - a

    Response<Boolean> removeProductsFromStore(String username, int storeID, Product product, int amount); // 4.1 - b

    Response<Boolean> updateProductPrice(String username, int storeID, int productID, int newPrice); // 4.1 - c

    List<PurchasePolicy> getPurchasePolicy(String username, int storeID); // 4.2 - a

    List<DiscountPolicy> getDiscountPolicy(String username, int storeID); // 4.2 - b

    // add policy and edit it ???? 4.2 - c

    Response<Boolean> appointStoreOwner(String appointerName, String appointeeName, int storeID); // 4.3

    Response<Boolean> appointStoreManager(String appointerName, String appointeeName, int storeID); // 4.5

    Response<Boolean> fireStoreManager(String appointerName, String appointeeName, int storeID); // 4.7

    Response<UserDetails> getWorkersDetails(String username, int storeID); // 4.9

    Response<Purchase> getPurchaseDetails(String username, int storeID); // 4.11


    /**
     * System Manager requirements - 6
     */
    List<Purchase> getUserPurchaseHistory(String username); // 6.4 - a

    List<Purchase> getStorePurchaseHistory(int storeID); // 6.4 - b
}
