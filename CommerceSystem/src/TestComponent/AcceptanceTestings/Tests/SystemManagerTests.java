package TestComponent.AcceptanceTestings.Tests;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.UserManager.PurchaseDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class SystemManagerTests extends ProjectAcceptanceTests{

    int storeID;
    private static boolean initialized = false;

    @Before
    public void setUp(){
        if(!initialized)
            super.setUp();

        // logging in with an admin
        String guestName = bridge.addGuest().getResult();
        bridge.login(bridge.addGuest().getResult(), "shaked",
                Integer.toString("jacob".hashCode()));

        // opening a store and adding products to it
        bridge.register(guestName, "shemesh", "123456");
        bridge.login(bridge.addGuest().getResult(), "shemesh", "123456");
        this.storeID = bridge.openStore("shemesh", "ha imperia shel shemesh").getResult();

        ProductDTO product = new ProductDTO("pastrama hodu", storeID, 10,
                new LinkedList<String>(Arrays.asList("food", "yummy")),
                new LinkedList<String>(Arrays.asList("pastrama")),
                null);

        bridge.addProductsToStore("shemesh", product, 100);

        product = new ProductDTO("mitz petel", storeID, 5,
                new LinkedList<String>(Arrays.asList("beverage", "yummy")),
                new LinkedList<String>(Arrays.asList("mitz")),
                null);

        bridge.addProductsToStore("shemesh", product, 200);

        // a user buying products
        bridge.register(guestName, "avi", "123456");

        bridge.login(bridge.addGuest().getResult(), "avi", "123456");

        // user adding products to cart and buying them
        ProductDTO productToAdd = bridge.searchByProductName("pastrama hodu").getResult().get(0);
        bridge.addToCart("avi", productToAdd.getStoreID(), productToAdd.getProductID());

        productToAdd = bridge.searchByProductName("mitz petel").getResult().get(0);
        bridge.addToCart("avi", productToAdd.getStoreID(), productToAdd.getProductID());

        bridge.directPurchase("avi", "4580-1234-5678-9010", "narnia");

        initialized = true;
        }

    @Test
    public void getUserPurchaseHistoryTest(){ // 6.4 - a
        // getting the purchase history of a user as an admin
        Response<List<PurchaseDTO>> userPurchaseHistoryResult = bridge.getUserPurchaseHistory("shaked", "avi");
        Assert.assertFalse(userPurchaseHistoryResult.isFailure());
        Assert.assertFalse(userPurchaseHistoryResult.getResult().isEmpty());

        // getting an empty purchase history of a user as an admin
        userPurchaseHistoryResult = bridge.getUserPurchaseHistory("shaked", "shemesh");
        Assert.assertFalse(userPurchaseHistoryResult.isFailure());
        Assert.assertTrue(userPurchaseHistoryResult.getResult().isEmpty());
//
        // trying to get the purchase history of a user while not being an admin. should fail
        userPurchaseHistoryResult = bridge.getUserPurchaseHistory("shemesh", "avi");
        Assert.assertTrue(userPurchaseHistoryResult.isFailure());
    }

    @Test
    public void getStorePurchaseHistory(){ // 6.4 - b
        // getting the purchase history of a store as an admin
        Response<Collection<PurchaseDTO>> storeHistoryResult = bridge.getStorePurchaseHistory("shaked",
                this.storeID);
        Assert.assertFalse(storeHistoryResult.isFailure());
        Assert.assertFalse(storeHistoryResult.getResult().isEmpty());

        // trying to get the purchase history of a store while not being an admin. should fail
        storeHistoryResult = bridge.getStorePurchaseHistory("shemesh", this.storeID);
        Assert.assertTrue(storeHistoryResult.isFailure());
    }
}
