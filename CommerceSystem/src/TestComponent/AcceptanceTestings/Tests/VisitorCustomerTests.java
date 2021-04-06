package TestComponent.AcceptanceTestings.Tests;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.UserManager.PurchaseDTO;
import Server.Service.IService;
import TestComponent.AcceptanceTestings.Bridge.Driver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

public class VisitorCustomerTests extends ProjectAcceptanceTests{

    private static boolean initialized = false;
    //private static IService bridge = Driver.getBridge();

    @Before
    public void setUp(){
        if(!initialized) {

            super.setUp();
            //bridge.init();

            // pre-registered user in the system
            String guestName = bridge.addGuest().getResult();
            bridge.register(guestName, "aviad", "123456");
            bridge.register(guestName, "jacob", "123456");

            bridge.login(guestName, "aviad", "123456"); // aviad is logged in before the tests starts

            // opening some stores for later use
            int storeID = bridge.openStore("aviad", "stam hanut").getResult();

            // adding products to the store
            ProductDTO product = new ProductDTO("kchichat perot", storeID, 20,
                    new LinkedList<String>(Arrays.asList("food", "tasty")),
                    new LinkedList<String>(Arrays.asList("kchicha")),
                    null);

            bridge.addProductsToStore("aviad", product, 20);

            product = new ProductDTO("kchichat basar", storeID, 20,
                    new LinkedList<String>(Arrays.asList("food", "tasty")),
                    new LinkedList<String>(Arrays.asList("kchicha")),
                    null);

            bridge.addProductsToStore("aviad", product, 0); // out of stock product

            storeID = bridge.openStore("aviad", "lo yodea").getResult();
            product = new ProductDTO("matos krav", storeID, 200000,
                    new LinkedList<String>(Arrays.asList("aviation", "fly")),
                    new LinkedList<String>(Arrays.asList("matos")),
                    null);

            bridge.addProductsToStore("aviad", product, 2);

            this.initialized = true;
        }
    }

    @Test
    public void enteringSystemTest(){ // 2.1
        // what TODO?
    }

    @Test
    public void quittingSystemTest(){ // 2.2
        // what TODO?
    }

    @Test
    public void registerTest(){ // 2.3
        // registering with a unique username
        String guestName = bridge.addGuest().getResult();
        Response<Boolean> registerResponse = bridge.register(guestName, "issac", "123456");
        Assert.assertFalse(registerResponse.isFailure());

        // trying to log in with the newly registered user
        Response<String> loginResponse = bridge.login(guestName, "issac", "123456");
        Assert.assertFalse(loginResponse.isFailure());

        // now logging out and trying to register a new user with the same username
        Response<String> logoutResponse = bridge.logout(loginResponse.getResult());
        registerResponse = bridge.register(logoutResponse.getResult(), "issac", "654321");
        Assert.assertTrue(registerResponse.isFailure());
    }

    @Test
    public void loginTest(){ // 2.4
        // trying to login to a user which does not exit
        String guestName = bridge.addGuest().getResult();
        Response<String> loginResponse = bridge.login(guestName, "shlomi", "123456");
        Assert.assertTrue(loginResponse.isFailure());

        // trying to do an action only a logged in user can do. should fail
        Response<List<PurchaseDTO>> actionResponse =  bridge.getPurchaseHistory("shlomi");
        Assert.assertTrue(actionResponse.isFailure());

        // logging in to the pre-registered user
        loginResponse = bridge.login(guestName, "jacob", "123456");
        Assert.assertFalse(loginResponse.isFailure());

        // trying to do an action only a logged in user can do. should pass
        actionResponse =  bridge.getPurchaseHistory("jacob");
        Assert.assertFalse(actionResponse.isFailure());
    }

    @Test
    public void searchStoreTest(){ // 2.5
        // searching for an existing store
        Response<List<Store>> searchResult = bridge.searchByStoreName("stam hanut");
        Assert.assertFalse(searchResult.isFailure());

        // checking if all the stores we got have that name
        boolean exists = true;
        for(Store store: searchResult.getResult()){
            if(!store.getName().equals("stam hanut")){ // todo: equals or a substring matches?
                exists = false;
            }
        }

        Assert.assertTrue(exists);


        // now looking for an non-existing store
        searchResult = bridge.searchByStoreName("mefo li");
        Assert.assertTrue(searchResult.getResult().isEmpty());
    }

    @Test
    public void searchProductTest(){ // 2.6
        // search by name - existing product
        Response<List<ProductDTO>> searchResult = bridge.searchByProductName("kchichat perot");
        Assert.assertFalse(searchResult.isFailure());

        // checking if all the products we got have that name
        boolean exists = true;
        for(ProductDTO product: searchResult.getResult()){
            if(!product.getName().equals("kchichat perot")){ // todo: equals or a substring matches?
                exists = false;
            }
        }

        Assert.assertTrue(exists);

        // search by name - non-existing product
        searchResult = bridge.searchByProductName("tarnegolet");
        Assert.assertTrue(searchResult.getResult().isEmpty());


        // search by category - existing product
        searchResult = bridge.searchByProductCategory("aviation");
        Assert.assertFalse(searchResult.isFailure());

        // checking if all the products we got are in that category
        exists = true;
        for(ProductDTO product: searchResult.getResult()){
            boolean current = false;
            for(String category: product.getCategories()) {
                if (category.equals("aviation")) { // todo: equals or a substring matches?
                    current = true;
                }
            }
            exists = exists && current;
        }

        Assert.assertTrue(exists);

        // search by category - non-existing product
        searchResult = bridge.searchByProductCategory("spinners");
        Assert.assertTrue(searchResult.getResult().isEmpty());


        // search by keyword - existing product
        searchResult = bridge.searchByProductKeyword("matos");
        Assert.assertFalse(searchResult.isFailure());

        // checking if all the products we got are related to the keyword
        exists = true;
        for(ProductDTO product: searchResult.getResult()){
            boolean current = false;
            for(String keyword: product.getKeywords()) {
                if (keyword.equals("matos")) { // todo: equals or a substring matches?
                    current = true;
                }
            }
            exists = exists && current;
        }

        Assert.assertTrue(exists);

        // search by category - non-existing product
        searchResult = bridge.searchByProductKeyword("nigmero li ha ra'ayonot");
        Assert.assertTrue(searchResult.getResult().isEmpty());
    }

    @Test
    public void addToCartTest(){ // 2.7
        String guestName = bridge.addGuest().getResult();

        // adding to cart a product which is in stock
        Response<List<ProductDTO>> searchResult = bridge.searchByProductName("kchichat perot");
        ProductDTO product = searchResult.getResult().get(0);
        Response<Boolean> addResult = bridge.addToCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertTrue(addResult.getResult());

        // checking if it's added to the cart
        Response<Map<Integer, Map<ProductDTO, Integer>>> cartResult = bridge.getCartDetails(guestName);
        Map<ProductDTO, Integer> basket = cartResult.getResult().get(product.getStoreID());
        boolean exists = false;
        for(ProductDTO productDTO: basket.keySet()){
            if(productDTO.getProductID() == product.getProductID()){
                exists = true;
                break;
            }
        }

        Assert.assertTrue(exists);


//        // trying to add an out of stock product
//        searchResult = bridge.searchByProductName("kchichat basar");
//        product = searchResult.getResult().get(0);
//        addResult = bridge.addToCart(guestName, product.getStoreID(), product.getProductID());
//        Assert.assertFalse(addResult.getResult());
//
//        cartResult = bridge.getCartDetails(guestName);
//        basket = cartResult.getResult().get(product.getStoreID());
//        exists = false;
//        for(ProductDTO productDTO: basket.keySet()){
//            if (productDTO.getProductID() == product.getProductID()) {
//                exists = true;
//                break;
//            }
//        }
//
//        Assert.assertFalse(exists);
    }

    @Test
    public void getCartTest(){ // 2.8.1
        //TODO: if add, remove and update work then this should be working too
    }

    @Test
    public void removeProductTest(){ // 2.8.2
        // a guest is adding a product to his cart
        String guestName = bridge.addGuest().getResult();

        // adding to cart a product which is in stock
        Response<List<ProductDTO>> searchResult = bridge.searchByProductName("kchichat perot");
        ProductDTO product = searchResult.getResult().get(0);
        Response<Boolean> addResult = bridge.addToCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertTrue(addResult.getResult());

        // removing the product from the cart
        Response<Boolean> removeResult = bridge.removeFromCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertTrue(removeResult.getResult());

        // checking if the product is not there
        Response<Map<Integer, Map<ProductDTO, Integer>>> cartResult = bridge.getCartDetails(guestName);
        Map<ProductDTO, Integer> basket = cartResult.getResult().get(product.getStoreID());

        if(basket != null) { // if the basket is null then it still means it works
            boolean exists = false;
            for (ProductDTO productDTO : basket.keySet()) {
                if (productDTO.getProductID() == product.getProductID()) {
                    exists = true;
                    break;
                }
            }

            Assert.assertFalse(exists);
        }

        // now trying to remove the same product again
        removeResult = bridge.removeFromCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertFalse(removeResult.getResult());
    }

    @Test
    public void updateProductQuantityTest(){ // 2.8.3
        // a guest is adding a product to his cart
        String guestName = bridge.addGuest().getResult();

        // adding to cart a product which is in stock
        Response<List<ProductDTO>> searchResult = bridge.searchByProductName("kchichat perot");
        ProductDTO product = searchResult.getResult().get(0);
        Response<Boolean> addResult = bridge.addToCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertTrue(addResult.getResult());

        // updating the quantity to a valid amount (the new amount is in stock)
        Response<Boolean> updateResult = bridge.updateProductQuantity(guestName, product.getStoreID(), product.getProductID(), 5);
        Assert.assertTrue(updateResult.getResult());

        // checking if it was updated
        Response<Map<Integer, Map<ProductDTO, Integer>>> cartResult = bridge.getCartDetails(guestName);
        Map<ProductDTO, Integer> basket = cartResult.getResult().get(product.getStoreID());
        boolean updated = false;
        for(ProductDTO productDTO: basket.keySet()){
            if(productDTO.getProductID() == product.getProductID()){
                if(basket.get(productDTO) == 5) {
                    updated = true;
                    break;
                }
            }
        }

        Assert.assertTrue(updated);

        // not trying to update to a quantity which is invalid (negative amount)
        updateResult = bridge.updateProductQuantity(guestName, product.getStoreID(), product.getProductID(), -5);
        Assert.assertFalse(updateResult.getResult());

        cartResult = bridge.getCartDetails(guestName);
        basket = cartResult.getResult().get(product.getStoreID());
        updated = false;
        for(ProductDTO productDTO: basket.keySet()){
            if (productDTO.getProductID() == product.getProductID()) {
                if(basket.get(productDTO) == 20) {
                    updated = true;
                    break;
                }
            }
        }

        Assert.assertFalse(updated);
    }

    @Test
    public void directPurchaseTest(){ // 2.9
        // a guest is adding a product to his cart
        String guestName = bridge.addGuest().getResult();

        // adding to cart a product which is in stock
        Response<List<ProductDTO>> searchResult = bridge.searchByProductName("kchichat perot");
        ProductDTO product = searchResult.getResult().get(0);
        Response<Boolean> addResult = bridge.addToCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertTrue(addResult.getResult());

        // the user buying them
        Response<Boolean> purchaseResult = bridge.directPurchase(guestName, "4580-1234-5678-9010", "shinovar");
        Assert.assertTrue(purchaseResult.getResult());

        // since after buying the cart should be empty then buying again would fail
        purchaseResult = bridge.directPurchase(guestName, "4580-1234-5678-9010", "kholin");
        Assert.assertFalse(purchaseResult.getResult());
    }
}
