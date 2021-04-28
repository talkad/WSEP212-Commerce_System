package TestComponent.IntegrationTestings;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.ShoppingManager.StoreController;
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
        ProductDTO productDTO;
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
        productDTO = new ProductDTO("Eggs", productID,storeRes.getResult(),13.5, null, null, null, 0,0);
        store.addProduct(productDTO, 100);

        // logout
        userController.logout("yoni");

        // costumer purchase its cart
        userController.addToCart(costumerName, storeRes.getResult(), productID);
        userController.purchase(costumerName, "4580-1111-1111-1111", "Israel, Jaljulia");

        Assert.assertEquals(1, userController.getUserByName("yoni").getPendingMessages().size());
    }

    @Test
    public void pendingNotificationReviewTest(){
        int productID = 2345682;
        ProductDTO productDTO;
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();
        String costumerName = UserController.getInstance().addGuest().getResult();

        // set the mock notifier
        Publisher.getInstance().setNotifier(new MockNotifier());

        // initial user registrations
        userController.register(initialUserName, "yoni2", "pis");

        // login of users
        userController.login(initialUserName, "yoni2", "pis");

        // opening the store
        Response<Integer> storeRes = userController.openStore("yoni2", "eggStore");
        Store store = StoreController.getInstance().getStoreById(storeRes.getResult());
        productDTO = new ProductDTO("Eggs", productID,storeRes.getResult(),13.5, null, null, null, 0,0);
        store.addProduct(productDTO, 100);

        // costumer purchase its cart
        userController.addToCart(costumerName, storeRes.getResult(), productID);
        userController.purchase(costumerName, "4580-1111-1111-1111", "Israel, Jaljulia");

        // logout
        userController.logout("yoni2");

        // add review
        userController.addProductReview(costumerName, store.getStoreID(), productID, "The best eggs");

        Assert.assertEquals(1, userController.getUserByName("yoni2").getPendingMessages().size());
    }

    @Test
    public void notifyPurchaseTest(){
        int productID = 514523;
        ProductDTO productDTO;
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();
        String costumerName = UserController.getInstance().addGuest().getResult();

        // set the mock notifier
        MockNotifier mock = new MockNotifier();
        Publisher.getInstance().setNotifier(mock);

        // initial user registrations
        userController.register(initialUserName, "yoni3", "pis");

        // login of users
        userController.login(initialUserName, "yoni3", "pis");

        // opening the store
        Response<Integer> storeRes = userController.openStore("yoni3", "eggStore");
        Store store = StoreController.getInstance().getStoreById(storeRes.getResult());
        productDTO = new ProductDTO("Eggs", productID,storeRes.getResult(),13.5, null, null, null, 0,0);
        store.addProduct(productDTO, 100);

        // costumer purchase its cart
        userController.addToCart(costumerName, storeRes.getResult(), productID);
        userController.purchase(costumerName, "4580-1111-1111-1111", "Israel, Jaljulia");

        Assert.assertEquals(0, userController.getUserByName("yoni3").getPendingMessages().size());
        Assert.assertEquals(1, mock.getMessages("yoni3").size());
    }

    @Test
    public void notifyReviewTest(){
        int productID = 5687589;
        ProductDTO productDTO;
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();
        String costumerName = UserController.getInstance().addGuest().getResult();

        // set the mock notifier
        MockNotifier mock = new MockNotifier();
        Publisher.getInstance().setNotifier(mock);

        // initial user registrations
        userController.register(initialUserName, "yoni4", "pis");

        // login of users
        userController.login(initialUserName, "yoni4", "pis");

        // opening the store
        Response<Integer> storeRes = userController.openStore("yoni4", "eggStore");
        Store store = StoreController.getInstance().getStoreById(storeRes.getResult());
        productDTO = new ProductDTO("Eggs", productID,storeRes.getResult(),13.5, null, null, null, 0,0);
        store.addProduct(productDTO, 100);

        // costumer purchase its cart
        userController.addToCart(costumerName, storeRes.getResult(), productID);
        userController.purchase(costumerName, "4580-1111-1111-1111", "Israel, Jaljulia");

        // add review
        userController.addProductReview(costumerName, store.getStoreID(), productID, "The best eggs");

        Assert.assertEquals(0, userController.getUserByName("yoni4").getPendingMessages().size());
        Assert.assertEquals(2, mock.getMessages("yoni4").size());
    }
}
