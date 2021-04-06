package TestComponent.AcceptanceTestings.Tests;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.UserManager.PurchaseDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RegisteredCustomerTests extends ProjectAcceptanceTests{

    private static boolean initialized = false;

    @Before
    public void setUp(){
        if(!initialized) {
            super.setUp();

            String guestName = bridge.addGuest().getResult();
            bridge.register(guestName, "aviad", "123456");
            bridge.register(guestName, "shalom", "123456");

            bridge.login(guestName, "aviad", "123456");
            int storeID = bridge.openStore("aviad", "hacol la sefer").getResult();

            ProductDTO product = new ProductDTO("simania zoheret", storeID, 20,
                    new LinkedList<String>(Arrays.asList("bookmark")),
                    new LinkedList<String>(Arrays.asList("simania")),
                    null);

            bridge.addProductsToStore("aviad", product, 20);

            product = new ProductDTO("mavrik sfarim", storeID, 30,
                    new LinkedList<String>(Arrays.asList("polish")),
                    new LinkedList<String>(Arrays.asList("mavrik")),
                    null);

            bridge.addProductsToStore("aviad", product, 100);

            product = new ProductDTO("martiv sfarim", storeID, 30,
                    new LinkedList<String>(Arrays.asList("wet")),
                    new LinkedList<String>(Arrays.asList("martiv")),
                    null);

            bridge.addProductsToStore("aviad", product, 100);

            initialized = true;
        }
    }

    @Test
    public void logoutTest(){ // 3.1
        // logging in to an existing user.
        String guestName = bridge.addGuest().getResult();
        bridge.login(guestName, "shalom", "123456");

        // logging out
        Response<String> logoutResponse = bridge.logout("shalom");
        Assert.assertFalse(logoutResponse.isFailure());
        Assert.assertTrue(logoutResponse.getResult().contains("Guest")); // checking if the user became a guest

        // trying to log out from an already logged out user
        logoutResponse = bridge.logout("shalom");
        Assert.assertTrue(logoutResponse.isFailure());
    }

    @Test
    public void createStoreTest(){ // 3.2
        // opening a store from a logged in user
        Response<Integer> openStoreResponse = bridge.openStore("aviad", "hacol la even");
        Assert.assertFalse(openStoreResponse.isFailure());

        // looking the store up on the search just to be sure
        Response<List<Store>> searchResult = bridge.searchByStoreName("hacol la even");
        boolean exists = true;
        for(Store store: searchResult.getResult()){
            if(!store.getName().equals("hacol la even")){ // todo: equals or a substring matches?
                exists = false;
            }
        }

        Assert.assertTrue(exists);

        // trying to open a store from a user which is a guest. should fail
        String guestName = bridge.addGuest().getResult();
        openStoreResponse = bridge.openStore(guestName, "bug");
        Assert.assertTrue(openStoreResponse.isFailure());

        // making sure we can't find it
        searchResult = bridge.searchByStoreName("bug");
        exists = false;
        for(Store store: searchResult.getResult()){
            if(store.getName().equals("bug")){ // todo: equals or a substring matches?
                exists = true;
            }
        }

        Assert.assertFalse(exists);
    }

    @Test
    public void reviewProduct(){ // 3.3
        // logged in user adding a product to his cart and buying it
        String guestName = bridge.addGuest().getResult();
        bridge.login(guestName, "shalom", "123456");
        Response<List<ProductDTO>> searchResult = bridge.searchByProductName("simania zoheret");
        ProductDTO productDTO = searchResult.getResult().get(0);
        bridge.addToCart("shalom", productDTO.getStoreID(), productDTO.getProductID());
        bridge.directPurchase("shalom", "4580-1234-5678-9010", "natanya");

        // now he reviews it
        Response<Boolean> reviewResult = bridge.addProductReview("shalom", productDTO.getStoreID(),
                productDTO.getProductID(), "best simania i ever bought! solid 5/7");
        Assert.assertTrue(reviewResult.getResult());

        // checking if the review was added
        searchResult = bridge.searchByProductName("simania zoheret");
        productDTO = searchResult.getResult().get(0);
        Assert.assertTrue(productDTO.getReviews().contains("best simania i ever bought! solid 5/7"));


        // now the guest is trying to review a product he didn't buy. should fail
        searchResult = bridge.searchByProductName("martiv sfarim");
        productDTO = searchResult.getResult().get(0);
        reviewResult = bridge.addProductReview("shalom", productDTO.getStoreID(),
                productDTO.getProductID(), "meh martiv! 3/10");
        Assert.assertFalse(reviewResult.getResult());


        // logging out and trying to review a product. should fail.
        guestName = bridge.logout("shalom").getResult();
        reviewResult = bridge.addProductReview(guestName, productDTO.getStoreID(),
                productDTO.getProductID(), "meh mavrik! 3/10");
        Assert.assertFalse(reviewResult.getResult());
    }

    @Test
    public void getPurchaseHistory(){ // 3.7
        // logging in and buying a product
        String guestName = bridge.addGuest().getResult();
        bridge.login(guestName, "shalom", "123456");
        Response<List<ProductDTO>> searchResult = bridge.searchByProductName("mavrik sfarim");
        ProductDTO productDTO = searchResult.getResult().get(0);
        bridge.addToCart("shalom", productDTO.getStoreID(), productDTO.getProductID());
        bridge.directPurchase("shalom", "4580-1234-5678-9010", "natanya");

        // looking the purchase in the purchase history
        Response<List<PurchaseDTO>> historyResult = bridge.getPurchaseHistory("shalom");
        Assert.assertFalse(historyResult.isFailure());

        boolean exists = false;
        for(PurchaseDTO purchased: historyResult.getResult()){
            for(ProductDTO product: purchased.getBasket().keySet()){
                if(product.getProductID() == productDTO.getProductID() && product.getStoreID() == productDTO.getStoreID()) {
                    exists = true;
                }
            }
        }

        Assert.assertTrue(exists);


        // logging out and trying to view the purchase history as a guest. should fail
        guestName = bridge.logout("shalom").getResult();
        historyResult = bridge.getPurchaseHistory(guestName);
        Assert.assertTrue(historyResult.isFailure());
    }
}
