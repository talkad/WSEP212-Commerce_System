package TestComponent.IntegrationTestings;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.ShoppingManager.StoreController;
import Server.Domain.UserManager.UserController;
import Server.Service.CommerceService;
import org.junit.Assert;
import org.junit.Test;

public class PublisherTest {

    @Test
    public void notConnectedNotificationTestSuccess(){
        int productID = 4562781;
        ProductDTO productDTO;
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();
        String costumerName = UserController.getInstance().addGuest().getResult();

        // initial user registrations
        userController.register(initialUserName, "yoni", "pis");

        // login of users
        Response<String> login = userController.login(initialUserName, "yoni", "pis");
        String newUserName = login.getResult();

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
}
