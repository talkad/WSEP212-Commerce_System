package TestComponent.AcceptanceTestings.Tests;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.UserManager.PurchaseDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

public class SystemManagerTests extends ProjectAcceptanceTests{

    int storeID;

    @BeforeClass
    public void setUp(){
        super.setUp();

        // logging in with an admin
        String guestName = this.bridge.addGuest().getResult();
        this.bridge.login(this.bridge.addGuest().getResult(), "shaked",
                Integer.toString("jacob".hashCode()));

        // opening a store and adding products to it
        this.bridge.register(guestName, "shemesh", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "shemesh", "123456");
        this.storeID = this.bridge.openStore("shemesh", "ha imperia shel shemesh").getResult();

        ProductDTO product = new ProductDTO("pastrama hodu", storeID, 10,
                new LinkedList<String>(Arrays.asList("food", "yummy")),
                new LinkedList<String>(Arrays.asList("pastrama")),
                null);

        this.bridge.addProductsToStore("shemesh", product, 100);

        product = new ProductDTO("mitz petel", storeID, 5,
                new LinkedList<String>(Arrays.asList("beverage", "yummy")),
                new LinkedList<String>(Arrays.asList("mitz")),
                null);

        this.bridge.addProductsToStore("shemesh", product, 200);

        // a user buying products
        this.bridge.register(guestName, "avi", "123456");

        this.bridge.login(this.bridge.addGuest().getResult(), "avi", "123456");

        // user adding products to cart and buying them
        ProductDTO productToAdd = this.bridge.searchByProductName("pastrama hodu").getResult().get(0);
        this.bridge.addToCart("avi", productToAdd.getStoreID(), productToAdd.getProductID());

        productToAdd = this.bridge.searchByProductName("mitz petel").getResult().get(0);
        this.bridge.addToCart("avi", productToAdd.getStoreID(), productToAdd.getProductID());

        this.bridge.directPurchase("avi", "4580-1234-5678-9010", "narnia");
    }

    @Test
    public void getUserPurchaseHistoryTest(){ // 6.4 - a
        // getting the purchase history of a user as an admin
        Response<List<PurchaseDTO>> userPurchaseHistoryResult = this.bridge.getUserPurchaseHistory("shaked", "avi");
        Assert.assertFalse(userPurchaseHistoryResult.isFailure());
        Assert.assertFalse(userPurchaseHistoryResult.getResult().isEmpty());

        // getting an empty purchase history of a user as an admin
        userPurchaseHistoryResult = this.bridge.getUserPurchaseHistory("shaked", "shemesh");
        Assert.assertFalse(userPurchaseHistoryResult.isFailure());
        Assert.assertTrue(userPurchaseHistoryResult.getResult().isEmpty());

        // trying to get the purchase history of a user while not being an admin. should fail
        userPurchaseHistoryResult = this.bridge.getUserPurchaseHistory("shemesh", "avi");
        Assert.assertTrue(userPurchaseHistoryResult.isFailure());
    }

    @Test
    public void getStorePurchaseHistory(){ // 6.4 - b
        // getting the purchase history of a store as an admin
        Response<Collection<PurchaseDTO>> storeHistoryResult = this.bridge.getStorePurchaseHistory("shaken",
                this.storeID);
        Assert.assertFalse(storeHistoryResult.isFailure());
        Assert.assertFalse(storeHistoryResult.getResult().isEmpty());

        // trying to get the purchase history of a store while not being an admin. should fail
        storeHistoryResult = this.bridge.getStorePurchaseHistory("shemesh", this.storeID);
        Assert.assertTrue(storeHistoryResult.isFailure());
    }
}
