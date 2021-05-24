package TestComponent.IntegrationTestings;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.ShoppingManager.StoreController;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import Server.Domain.UserManager.Publisher;
import Server.Domain.UserManager.UserController;
import Server.Service.CommerceService;
import TestComponent.IntegrationTestings.Mocks.MockNotifier;
import org.junit.Assert;
import org.junit.Test;

public class PublisherTest {

    @Test
    public void pendingNotificationPurchaseTest(){
        int productID = 4562781;
        ProductClientDTO productDTO;
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();
        String costumerName = UserController.getInstance().addGuest().getResult();

        // set the mock notifier
        Publisher.getInstance().setNotifier(new MockNotifier());

        // initial user registrations
        userController.register(initialUserName, "yoni", "pis");

        // login of users
        userController.login(initialUserName, "yoni", "pis");

        // opening the store
        Response<Integer> storeRes = userController.openStore("yoni", "eggStore");
        Store store = StoreController.getInstance().getStoreById(storeRes.getResult());
        productDTO = new ProductClientDTO("Eggs", productID,storeRes.getResult(),13.5, null, null, null, 0,0);
        store.addProduct(productDTO, 100);

        // logout
        userController.logout("yoni");

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        // costumer purchase its cart
        userController.addToCart(costumerName, storeRes.getResult(), productID);
        userController.purchase(costumerName, paymentDetails, supplyDetails);

        Assert.assertEquals(1, userController.getUserByName("yoni").getPendingMessages().size());
    }

    @Test
    public void pendingNotificationReviewTest(){
        int productID = 2345682;
        ProductClientDTO productDTO;
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();
        String costumerName = UserController.getInstance().addGuest().getResult();

        // set the mock notifier
        MockNotifier mock = new MockNotifier();
        mock.addConnection("yoni2", null);
        Publisher.getInstance().setNotifier(mock);

        // initial user registrations
        userController.register(initialUserName, "yoni2", "pis");

        // login of users
        userController.login(initialUserName, "yoni2", "pis");

        // opening the store
        Response<Integer> storeRes = userController.openStore("yoni2", "eggStore");
        Store store = StoreController.getInstance().getStoreById(storeRes.getResult());
        productDTO = new ProductClientDTO("Eggs", productID,storeRes.getResult(),13.5, null, null, null, 0,0);
        store.addProduct(productDTO, 100);

        // costumer login
        userController.register(costumerName, "tal", "pis");
        userController.login(costumerName, "tal", "pis");

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        // costumer purchase its cart
        userController.addToCart("tal", storeRes.getResult(), productID);
        userController.purchase("tal", paymentDetails, supplyDetails);

        // logout
        userController.logout("yoni2");

        // add review
        userController.addProductReview("tal", store.getStoreID(), productID, "The best eggs");

        Assert.assertEquals(1, userController.getUserByName("yoni2").getPendingMessages().size());
    }

    @Test
    public void notifyPurchaseTest(){
        int productID = 514523;
        ProductClientDTO productDTO;
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();
        String costumerName = UserController.getInstance().addGuest().getResult();

        // set the mock notifier
        MockNotifier mock = new MockNotifier();
        mock.addConnection("yoni3", null);
        Publisher.getInstance().setNotifier(mock);

        // initial user registrations
        userController.register(initialUserName, "yoni3", "pis");

        // login of users
        userController.login(initialUserName, "yoni3", "pis");

        // opening the store
        Response<Integer> storeRes = userController.openStore("yoni3", "eggStore");
        Store store = StoreController.getInstance().getStoreById(storeRes.getResult());
        productDTO = new ProductClientDTO("Eggs", productID,storeRes.getResult(),13.5, null, null, null, 0,0);
        store.addProduct(productDTO, 100);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        // costumer purchase its cart
        userController.addToCart(costumerName, storeRes.getResult(), productID);
        userController.purchase(costumerName, paymentDetails, supplyDetails);

        Assert.assertEquals(0, userController.getUserByName("yoni3").getPendingMessages().size());
        Assert.assertEquals(1, mock.getMessages("yoni3").size());
    }

    @Test
    public void notifyReviewTest(){
        int productID = 5687589;
        ProductClientDTO productDTO;
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();
        String costumerName = UserController.getInstance().addGuest().getResult();

        // set the mock notifier
        MockNotifier mock = new MockNotifier();
        mock.addConnection("yoni4", null);
        Publisher.getInstance().setNotifier(mock);

        // initial user registrations
        userController.register(initialUserName, "yoni4", "pis");

        // login of users
        userController.login(initialUserName, "yoni4", "pis");

        // opening the store
        Response<Integer> storeRes = userController.openStore("yoni4", "eggStore");
        Store store = StoreController.getInstance().getStoreById(storeRes.getResult());
        productDTO = new ProductClientDTO("Eggs", productID,storeRes.getResult(),13.5, null, null, null, 0,0);
        store.addProduct(productDTO, 100);

        // costumer login
        userController.register(costumerName, "tal", "pis");
        userController.login(costumerName, "tal", "pis");

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        // costumer purchase its cart
        userController.addToCart("tal", storeRes.getResult(), productID);
        userController.purchase("tal", paymentDetails, supplyDetails);

        // add review
        userController.addProductReview("tal", store.getStoreID(), productID, "The best eggs");

        Assert.assertEquals(0, userController.getUserByName("yoni4").getPendingMessages().size());
        Assert.assertEquals(2, mock.getMessages("yoni4").size());
    }
}
