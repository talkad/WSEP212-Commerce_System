package TestComponent.AcceptanceTestings.Tests;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.DTOs.StoreClientDTO;
import Server.Domain.UserManager.DTOs.BasketClientDTO;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import Server.Domain.UserManager.DTOs.PurchaseClientDTO;
import org.junit.Assert;
import org.junit.Before;
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
            bridge.register(guestName, "misheo", "123456");

            bridge.login(guestName, "aviad", "123456"); // aviad is logged in before the tests starts

            // opening some stores for later use
            int storeID = bridge.openStore("aviad", "stam hanut").getResult();

            // adding products to the store
            ProductClientDTO product = new ProductClientDTO("kchichat perot", storeID, 20,
                    new LinkedList<String>(Arrays.asList("food", "tasty")),
                    new LinkedList<String>(Arrays.asList("kchicha")));

            bridge.addProductsToStore("aviad", product, 20);

            product = new ProductClientDTO("kchicha kchitatit", storeID, 20,
                    new LinkedList<String>(Arrays.asList("food", "tasty")),
                    new LinkedList<String>(Arrays.asList("kchicha")));

            bridge.addProductsToStore("aviad", product, 20);

            storeID = bridge.openStore("aviad", "lo yodea").getResult();
            product = new ProductClientDTO("matos krav", storeID, 200000,
                    new LinkedList<String>(Arrays.asList("aviation", "fly")),
                    new LinkedList<String>(Arrays.asList("matos")));

            bridge.addProductsToStore("aviad", product, 2);

            // misheo logs in and adds a product to his cart
            bridge.login(bridge.addGuest().getResult(), "misheo", "123456");

            // adding to cart a product which is in stock
            Response<List<ProductClientDTO>> searchResult = bridge.searchByProductName("kchichat perot");
            product = searchResult.getResult().get(0);
            Response<Boolean> addResult = bridge.addToCart("misheo", product.getStoreID(), product.getProductID());
            Assert.assertTrue(addResult.getResult());

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
    public void registerTestSuccess(){ // 2.3 good
        // registering with a unique username
        String guestName = bridge.addGuest().getResult();
        Response<Boolean> registerResponse = bridge.register(guestName, "issac", "123456");
        Assert.assertFalse(registerResponse.isFailure());

        // trying to log in with the newly registered user
        Response<String> loginResponse = bridge.login(guestName, "issac", "123456");
        Assert.assertFalse(loginResponse.isFailure());
    }

    @Test
    public void registeringWithExistingUsernameTest(){ // 2.3 bad
        // trying to register a new user with an already existing username
        String guestName = bridge.addGuest().getResult();
        Response<Boolean> registerResponse = bridge.register(guestName, "aviad", "654321");
        Assert.assertTrue(registerResponse.isFailure());
    }

    @Test
    public void loginTestSuccess(){ // 2.4 good
        String guestName = bridge.addGuest().getResult();
        // logging in to the pre-registered user
        Response<String> loginResponse = bridge.login(guestName, "jacob", "123456");
        Assert.assertFalse(loginResponse.isFailure());

        // trying to do an action only a logged in user can do. should pass
        Response<List<PurchaseClientDTO>> actionResponse =  bridge.getPurchaseHistory("jacob");
        Assert.assertFalse(actionResponse.isFailure());
    }

    @Test
    public void loginToNonExistentUserTest(){ // 2.4 bad
        // trying to login to a user which does not exit
        String guestName = bridge.addGuest().getResult();
        Response<String> loginResponse = bridge.login(guestName, "shlomi", "123456");
        Assert.assertTrue(loginResponse.isFailure());

        // trying to do an action only a logged in user can do. should fail
        Response<List<PurchaseClientDTO>> actionResponse =  bridge.getPurchaseHistory("shlomi");
        Assert.assertTrue(actionResponse.isFailure());
    }

    @Test
    public void searchStoreTestSuccess(){ // 2.5 good
        // searching for an existing store
        Response<List<StoreClientDTO>> searchResult = bridge.searchByStoreName("stam hanut");
        Assert.assertFalse(searchResult.isFailure());

        // checking if all the stores we got have that name
        boolean exists = true;
        for(StoreClientDTO store: searchResult.getResult()){
            if(!store.getStoreName().contains("stam hanut")){
                exists = false;
            }
        }

        Assert.assertTrue(exists);
    }

    @Test
    public void searchNonExistentStoreTest(){ // 2.5 good
        // now looking for an non-existing store
        Response<List<StoreClientDTO>> searchResult = bridge.searchByStoreName("mefo li");
        Assert.assertTrue(searchResult.getResult().isEmpty());
    }

    @Test
    public void searchProductByNameTestSuccess(){ // 2.6-a good
        // search by name - existing product
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductName("kchichat perot");
        Assert.assertFalse(searchResult.isFailure());

        // checking if all the products we got have that name
        boolean exists = true;
        for(ProductClientDTO product: searchResult.getResult()){
            if(!product.getName().contains("kchichat perot")){
                exists = false;
            }
        }

        Assert.assertTrue(exists);
    }

    @Test
    public void searchNonExistentProductByNameTest(){ // 2.6-a bad
        // search by name - non-existing product
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductName("tarnegolet");
        Assert.assertTrue(searchResult.getResult().isEmpty());
    }

    @Test
    public void searchProductByCategoryTestSuccess(){ //2.6-b good
        // search by category - existing product
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductCategory("aviation");
        Assert.assertFalse(searchResult.isFailure());

        // checking if all the products we got are in that category
        boolean exists = true;
        for(ProductClientDTO product: searchResult.getResult()){
            boolean current = false;
            for(String category: product.getCategories()) {
                if (category.contains("aviation")) { //
                    current = true;
                }
            }
            exists = exists && current;
        }

        Assert.assertTrue(exists);
    }

    @Test
    public void searchNonExistentProductByCategoryTest() { //2.6-b bad
        // search by category - non-existing product
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductCategory("spinners");
        Assert.assertTrue(searchResult.getResult().isEmpty());
    }

    @Test
    public void searchProductByKeywordTestSuccess(){ // 2.6-c good

        // search by keyword - existing product
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductKeyword("matos");
        Assert.assertFalse(searchResult.isFailure());

        // checking if all the products we got are related to the keyword
        boolean exists = true;
        for(ProductClientDTO product: searchResult.getResult()){
            boolean current = false;
            for(String keyword: product.getKeywords()) {
                if (keyword.contains("matos")) {
                    current = true;
                }
            }
            exists = exists && current;
        }

        Assert.assertTrue(exists);
    }

    @Test
    public void searchNonExistentProductByKeywordTest() { // 2.6-c bad
        // search by category - non-existing product
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductKeyword("nigmero li ha ra'ayonot");
        Assert.assertTrue(searchResult.getResult().isEmpty());
    }

    @Test
    public void addToCartTestSuccess(){ // 2.7 good
        String guestName = bridge.addGuest().getResult();

        // adding to cart a product which is in stock
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductName("kchichat perot");
        ProductClientDTO product = searchResult.getResult().get(0);
        Response<Boolean> addResult = bridge.addToCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertTrue(addResult.getResult());

        // checking if it's added to the cart
        Response<List<BasketClientDTO>> cartResult = bridge.getCartDetails(guestName);
        Set<ProductClientDTO> products = new HashSet<>();
        for(BasketClientDTO basketDTO: cartResult.getResult()){
            if(basketDTO.getStoreID() == product.getStoreID()){
                products = basketDTO.getProductsDTO();
                break;
            }
        }

        boolean exists = false;
        for(ProductClientDTO productDTO: products){
            if(productDTO.getProductID() == product.getProductID()){
                exists = true;
                break;
            }
        }

        Assert.assertTrue(exists);
    }

    @Test
    public void addToCartOutOfStockProductTest(){ // 2.7 bad
        // opening a store and adding a product to it
        int storeID = bridge.openStore("aviad", "lala lili").getResult();
        ProductClientDTO product = new ProductClientDTO("kchichat basar", storeID, 20,
                new LinkedList<String>(Arrays.asList("food", "tasty")),
                new LinkedList<String>(Arrays.asList("kchicha")));

        bridge.addProductsToStore("aviad", product, 1); // last in stock

        // searching the product up in the system
        String guestName = bridge.addGuest().getResult();
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductName("kchichat basar");
        product = searchResult.getResult().get(0);

        // the product is removed just before the user adds it to his cart
        bridge.removeProductsFromStore("aviad", storeID, product.getProductID(), 1);

        // trying to add an out of stock product
        Response<Boolean> addResult = bridge.addToCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertFalse(addResult.getResult());

        Response<List<BasketClientDTO>> cartResult = bridge.getCartDetails(guestName);
        Set<ProductClientDTO> products = new HashSet<>();
        for(BasketClientDTO basketDTO: cartResult.getResult()){
            if(basketDTO.getStoreID() == product.getStoreID()){
                products = basketDTO.getProductsDTO();
                break;
            }
        }

        boolean exists = false;
        if(products != null) {
            for (ProductClientDTO productDTO : products) {
                if (productDTO.getProductID() == product.getProductID()) {
                    exists = true;
                    break;
                }
            }
        }

        Assert.assertFalse(exists);
    }

    @Test
    public void getCartTest(){ // 2.8.1
        //TODO: if add, remove and update work then this should be working too
    }

    @Test
    public void removeProductTestSuccess(){ // 2.8.2 good
        // a guest is adding a product to his cart
        String guestName = bridge.addGuest().getResult();

        // adding to cart a product which is in stock
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductName("kchichat perot");
        ProductClientDTO product = searchResult.getResult().get(0);
        Response<Boolean> addResult = bridge.addToCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertTrue(addResult.getResult());

        // removing the product from the cart
        Response<Boolean> removeResult = bridge.removeFromCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertTrue(removeResult.getResult());

        // checking if the product is not there
        Response<List<BasketClientDTO>> cartResult = bridge.getCartDetails(guestName);
        Set<ProductClientDTO> products = new HashSet<>();
        for(BasketClientDTO basketDTO: cartResult.getResult()){
            if(basketDTO.getStoreID() == product.getStoreID()){
                products = basketDTO.getProductsDTO();
                break;
            }
        }

        if(products != null) { // if the basket is null then it still means it works
            boolean exists = false;
            for (ProductClientDTO productDTO :products) {
                if (productDTO.getProductID() == product.getProductID()) {
                    exists = true;
                    break;
                }
            }

            Assert.assertFalse(exists);
        }
    }

    @Test
    public void removeNonExistentProductTest() { // 2.8.2 bad
        // a guest is removing a product to his cart
        String guestName = bridge.addGuest().getResult();

        // searching the product in the system
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductName("kchicha kchitatit");
        ProductClientDTO product = searchResult.getResult().get(0);

        // trying to remove the product from the cart although it not there already. should fail
        Response<Boolean>removeResult = bridge.removeFromCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertFalse(removeResult.getResult());
    }

    @Test
    public void updateProductQuantityTestSuccess(){ // 2.8.3 good

        // searching to product the user bought
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductName("kchichat perot");
        ProductClientDTO product = searchResult.getResult().get(0);

        // updating the quantity to a valid amount (the new amount is in stock)
        Response<Boolean> updateResult = bridge.updateProductQuantity("misheo", product.getStoreID(), product.getProductID(), 5);
        Assert.assertTrue(updateResult.getResult());

        // checking if it was updated
        Response<List<BasketClientDTO>> cartResult = bridge.getCartDetails("misheo");
        Set<ProductClientDTO> products = new HashSet<>();
        for(BasketClientDTO basketDTO: cartResult.getResult()){
            if(basketDTO.getStoreID() == product.getStoreID()){
                products = basketDTO.getProductsDTO();
                break;
            }
        }

        boolean updated = false;
        for(ProductClientDTO productDTO: products){
            if(productDTO.getProductID() == product.getProductID()){
                if(cartResult.getResult().get(0).getAmounts().contains(5)) {
                    updated = true;
                    break;
                }
            }
        }

        Assert.assertTrue(updated);
    }

    @Test
    public void updateProductQuantityToNegativeAmountTest() { // 2.8.3 bad
        // searching to product the user bought
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductName("kchichat perot");
        ProductClientDTO product = searchResult.getResult().get(0);

        // not trying to update to a quantity which is invalid (negative amount)
        Response<Boolean> updateResult = bridge.updateProductQuantity("misheo", product.getStoreID(), product.getProductID(), -5);
        Assert.assertFalse(updateResult.getResult());

        // checking if it wasn't updated
        Response<List<BasketClientDTO>> cartResult = bridge.getCartDetails("misheo");
        Set<ProductClientDTO> products = new HashSet<>();
        for(BasketClientDTO basketDTO: cartResult.getResult()){
            if(basketDTO.getStoreID() == product.getStoreID()){
                products = basketDTO.getProductsDTO();
                break;
            }
        }

        boolean updated = false;
        for(ProductClientDTO productDTO: products){
            if (productDTO.getProductID() == product.getProductID()) {
                if(cartResult.getResult().get(0).getAmounts().contains(20)) {
                    updated = true;
                    break;
                }
            }
        }

        Assert.assertFalse(updated);
    }

    @Test
    public void directPurchaseTestSuccess(){ // 2.9 good
        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        // a guest is adding a product to his cart
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "aaa", "aaa");
        bridge.login(guestName, "aaa", "aaa");

        // adding to cart a product which is in stock
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductName("kchichat perot");
        ProductClientDTO product = searchResult.getResult().get(0);
        int productID = product.getProductID();
        int storeID = product.getStoreID();
        Response<Boolean> addResult = bridge.addToCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertTrue(addResult.getResult());

        // the user buying them
        Response<Boolean> purchaseResult = bridge.directPurchase(guestName, paymentDetails, supplyDetails);
        Assert.assertTrue(purchaseResult.getResult());

        // checking the cart is empty
        Response<List<BasketClientDTO>> cartResponse = bridge.getCartDetails("aaa");
        Assert.assertTrue(cartResponse.getResult().isEmpty());

        // checking that the history was updated
        Response<List<PurchaseClientDTO>> historyResponse = bridge.getPurchaseHistory("aaa");
        boolean exists = false;

        for(PurchaseClientDTO purchaseClientDTO: historyResponse.getResult()){
            for(ProductClientDTO productClientDTO: purchaseClientDTO.getBasket().getProductsDTO()){
                if(productClientDTO.getStoreID() == storeID && productClientDTO.getProductID() == productID){
                    exists = true;
                    break;
                }
            }
        }

        Assert.assertTrue(exists);
    }

    @Test
    public void directPurchaseOutOfStock(){ // 2.9 bad
        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        // opening a store and adding a product to it
        int storeID = bridge.openStore("aviad", "krusty crab").getResult();
        ProductClientDTO product = new ProductClientDTO("patty", storeID, 20,
                new LinkedList<String>(Arrays.asList("food", "tasty")),
                new LinkedList<String>(Arrays.asList("patty")));

        bridge.addProductsToStore("aviad", product, 1); // last in stock

        // searching the product up in the system
        String guestName = bridge.addGuest().getResult();
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductName("patty");
        product = searchResult.getResult().get(0);

        // adding the product to the cart
        Response<Boolean> addResult = bridge.addToCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertTrue(addResult.getResult());

        // the product is removed just before the user buys it
        bridge.removeProductsFromStore("aviad", storeID, product.getProductID(), 1);

        // the user trying to buy the product
        Response<Boolean> purchaseResult = bridge.directPurchase(guestName, paymentDetails, supplyDetails);
        Assert.assertFalse(purchaseResult.getResult());
    }

    // the external systems always respond with positive result

//    @Test
//    public void directPurchasePaymentFailure() { // 2.9 bad
//        PaymentDetails paymentDetails = new PaymentDetails("0", "-2", "2021", "Israel Israelovice", "262", "20444444");
//        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");
//
//        // a guest is adding a product to his cart
//        String guestName = bridge.addGuest().getResult();
//
//        // adding to cart a product which is in stock
//        Response<List<ProductDTO>> searchResult = bridge.searchByProductName("kchichat perot");
//        ProductDTO product = searchResult.getResult().get(0);
//        Response<Boolean> addResult = bridge.addToCart(guestName, product.getStoreID(), product.getProductID());
//        Assert.assertTrue(addResult.getResult());
//
//        // the user buying the product but pays with an invalid payment method
//        Response<Boolean> purchaseResult = bridge.directPurchase(guestName, paymentDetails, supplyDetails);
//        Assert.assertFalse(purchaseResult.getResult());
//    }

//    @Test
//    public void directPurchaseSupplyFailure() { // 2.9 bad
//        // a guest is adding a product to his cart
//        String guestName = bridge.addGuest().getResult();
//
//        // adding to cart a product which is in stock
//        Response<List<ProductDTO>> searchResult = bridge.searchByProductName("kchichat perot");
//        ProductDTO product = searchResult.getResult().get(0);
//        Response<Boolean> addResult = bridge.addToCart(guestName, product.getStoreID(), product.getProductID());
//        Assert.assertTrue(addResult.getResult());
//
//        // the user buying the product but gives an invalid address
//        Response<Boolean> purchaseResult = bridge.directPurchase(guestName, "4580-1234-5678-9010", "USA");
//        Assert.assertFalse(purchaseResult.getResult());
//    }

    @Test
    public void directPurchaseSuccessNotificationTest(){ // 9.1 good (2.9)
        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        notifier.addConnection("aviad", null);

        // a guest is adding a product to his cart
        String guestName = bridge.addGuest().getResult();

        // adding to cart a product which is in stock
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductName("kchichat perot");
        ProductClientDTO product = searchResult.getResult().get(0);
        Response<Boolean> addResult = bridge.addToCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertTrue(addResult.getResult());

        // the user buying them
        Response<Boolean> purchaseResult = bridge.directPurchase(guestName, paymentDetails, supplyDetails);
        Assert.assertTrue(purchaseResult.getResult());

        // the store owner received a notification regarding the purchase
        Assert.assertEquals(1, notifier.getMessages("aviad").size());
    }

    @Test
    public void directPurchaseFailureNotificationTest(){ // 9.1 bad (2.9)
        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        notifier.addConnection("aviad", null);
        // opening a store and adding a product to it
        int storeID = bridge.openStore("aviad", "krusty crab").getResult();
        ProductClientDTO product = new ProductClientDTO("patty", storeID, 20,
                new LinkedList<String>(Arrays.asList("food", "tasty")),
                new LinkedList<String>(Arrays.asList("patty")));

        bridge.addProductsToStore("aviad", product, 1); // last in stock

        // searching the product up in the system
        String guestName = bridge.addGuest().getResult();
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductName("patty");
        product = searchResult.getResult().get(0);

        // adding the product to the cart
        Response<Boolean> addResult = bridge.addToCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertTrue(addResult.getResult());

        // the product is removed just before the user buys it
        bridge.removeProductsFromStore("aviad", storeID, product.getProductID(), 1);

        // the user trying to buy the product
        Response<Boolean> purchaseResult = bridge.directPurchase(guestName, paymentDetails, supplyDetails);
        Assert.assertFalse(purchaseResult.getResult());

        // store owner doesn't recieve a notification because the purchase wasn't completed
        Assert.assertEquals(0, notifier.getMessages("aviad").size());
    }

    @Test
    public void directPurchaseSuccessStoredNotificationTest(){ // 9.1 good (2.9)
        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        notifier.addConnection("aviad", null);

        bridge.logout("aviad");

        // a guest is adding a product to his cart
        String guestName = bridge.addGuest().getResult();

        // adding to cart a product which is in stock
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductName("kchichat perot");
        ProductClientDTO product = searchResult.getResult().get(0);
        Response<Boolean> addResult = bridge.addToCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertTrue(addResult.getResult());

        // the user buying them
        Response<Boolean> purchaseResult = bridge.directPurchase(guestName, paymentDetails, supplyDetails);
        Assert.assertTrue(purchaseResult.getResult());

        bridge.login(bridge.addGuest().getResult(), "aviad", "123456");

        // the store owner received a notification regarding the purchase
        Assert.assertEquals(1, notifier.getMessages("aviad").size());
    }

    @Test
    public void directPurchaseFailureStoredNotificationTest(){ // 9.1 bad (2.9)
        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        notifier.addConnection("shalom", null);
        bridge.logout("shalom");


        // opening a store and adding a product to it
        int storeID = bridge.openStore("aviad", "krusty crab").getResult();
        ProductClientDTO product = new ProductClientDTO("patty", storeID, 20,
                new LinkedList<String>(Arrays.asList("food", "tasty")),
                new LinkedList<String>(Arrays.asList("patty")));

        bridge.addProductsToStore("aviad", product, 1); // last in stock

        // searching the product up in the system
        String guestName = bridge.addGuest().getResult();
        Response<List<ProductClientDTO>> searchResult = bridge.searchByProductName("patty");
        product = searchResult.getResult().get(0);

        // adding the product to the cart
        Response<Boolean> addResult = bridge.addToCart(guestName, product.getStoreID(), product.getProductID());
        Assert.assertTrue(addResult.getResult());

        // the product is removed just before the user buys it
        bridge.removeProductsFromStore("aviad", storeID, product.getProductID(), 1);

        // the user trying to buy the product
        Response<Boolean> purchaseResult = bridge.directPurchase(guestName, paymentDetails, supplyDetails);
        Assert.assertFalse(purchaseResult.getResult());

        bridge.login(bridge.addGuest().getResult(), "shalom", "123456");


        // store owner doesn't recieve a notification because the purchase wasn't completed
        Assert.assertEquals(0, notifier.getMessages("shalom").size());
    }
}
