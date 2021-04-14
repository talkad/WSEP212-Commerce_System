package TestComponent.IntegrationTestings;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.ShoppingManager.StoreController;
import Server.Domain.UserManager.UserController;
import org.junit.Test;
import org.junit.Assert;
import java.util.Map;


public class PurchaseTests {

    @Test
    public void purchaseTest(){
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 65482;
        int storeID = StoreController.getInstance().openStore("Apple", "Bill Gates").getResult();
        ProductDTO productDTO = new ProductDTO("IPhone", productID, storeID, 5000, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);

        store.addProduct(productDTO, 5);
        UserController.getInstance().addToCart(guestName, storeID, productID);

        response = UserController.getInstance().purchase(guestName, "4580-1111-1111-1111", "Israel, Jaljulia");
        Assert.assertTrue(response.getResult());
    }

    @Test
    public void purchaseIllegalBankAccountTest(){
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 6812;
        int storeID = StoreController.getInstance().openStore("Rami levi", "Rami levi").getResult();
        ProductDTO productDTO = new ProductDTO("IPhone", productID, storeID, 5000, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);

        store.addProduct(productDTO, 5);
        UserController.getInstance().addToCart(guestName, storeID, productID);

        response = UserController.getInstance().purchase(guestName, "1111-1111-1111-1111", "Israel, Jaljulia");
        Assert.assertTrue(response.isFailure());
    }

    @Test
    public void purchaseIllegalLocationTest(){
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 19835;
        int storeID = StoreController.getInstance().openStore("ABU fahrizade", "Abu jihad").getResult();
        ProductDTO productDTO = new ProductDTO("sheep", productID, storeID, 120, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);

        store.addProduct(productDTO, 5);
        UserController.getInstance().addToCart(guestName, storeID, productID);

        response = UserController.getInstance().purchase(guestName, "4580-1111-1111-1111", "Iran, Teheran");
        Assert.assertTrue(response.isFailure());
    }

    @Test
    public void purchaseProductOutOfStockTest(){ // use case 2.9
        Response<Boolean> response;
        String guestName1 = UserController.getInstance().addGuest().getResult();
        String guestName2 = UserController.getInstance().addGuest().getResult();
        int productID = 424568;
        int storeID = StoreController.getInstance().openStore("KFC", "Ned Stark").getResult();
        ProductDTO productDTO = new ProductDTO("sword", productID, storeID, 120, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);

        store.addProduct(productDTO, 1);
        UserController.getInstance().addToCart(guestName1, storeID, 482163215); // product doesn't exists

        response = UserController.getInstance().purchase(guestName1, "4580-1111-1111-1111", "Iran, Teheran");
        Assert.assertTrue(response.isFailure());

        UserController.getInstance().addToCart(guestName1, storeID, productID);
        UserController.getInstance().addToCart(guestName2, storeID, productID);
        UserController.getInstance().purchase(guestName2, "4580-1111-1111-1111", "Israel, Winterfell");

        response = UserController.getInstance().purchase(guestName1, "4580-1111-1111-1111", "Iran, Teheran");
        Assert.assertTrue(response.isFailure()); // product out of stock
    }


    @Test
    public void badPurchaseInventoryDoesntChangedTest(){
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 138435;
        int storeID = StoreController.getInstance().openStore("Spongebob", "mr. krab").getResult();
        ProductDTO productDTO = new ProductDTO("jellyfish", productID, storeID, 120, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);

        store.addProduct(productDTO, 5);

        for(int i = 0; i < 10; i++)
            UserController.getInstance().addToCart(guestName, storeID, productID);

        response = UserController.getInstance().purchase(guestName, "4580-1111-1111-1111", "Iran, Teheran");
        Assert.assertTrue(response.isFailure());

        Assert.assertEquals(5, store.getInventory().getProductAmount(productID));
    }

    @Test
    public void badPurchaseUserHistoryDoesntChangedTest(){
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 12568634;
        int storeID = StoreController.getInstance().openStore("Evil Inc.", "Dufenshmirtz").getResult();
        ProductDTO productDTO = new ProductDTO("IHateTestInator", productID, storeID, 99999.99, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);

        UserController.getInstance().register(guestName, "faruk", "doda lola");
        UserController.getInstance().login(guestName, "faruk", "doda lola");

        store.addProduct(productDTO, 5);

        for(int i = 0; i < 10; i++)
            UserController.getInstance().addToCart(guestName, storeID, productID);

        UserController.getInstance().purchase("faruk", "4580-1111-1111-1111", "Israel");

        Assert.assertEquals(0, UserController.getInstance().getPurchaseHistoryContents("faruk").getResult().size());
        Assert.assertEquals(0, store.getPurchaseHistory().getResult().size());
    }

    @Test
    public void badPurchaseCartDoesntChangedTest(){
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID1 = 846512;
        int productID2 = 3151684;
        int storeID = StoreController.getInstance().openStore("Halil", "Mahmud").getResult();
        ProductDTO productDTO1 = new ProductDTO("hunus", productID1, storeID, 20, null, null, null, 0, 0);
        ProductDTO productDTO2 = new ProductDTO("tehini", productID2, storeID, 20, null, null, null, 0, 0);

        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addProduct(productDTO1, 1);
        store.addProduct(productDTO2, 1);

        UserController.getInstance().addToCart(guestName, storeID, productID1);
        UserController.getInstance().addToCart(guestName, storeID, productID2);
        UserController.getInstance().addToCart(guestName, storeID, productID2);

        Map<Integer, Map<ProductDTO, Integer>> cart = UserController.getInstance().getShoppingCartContents(guestName).getResult();

        response = UserController.getInstance().purchase(guestName, "4580-1111-1111-1111", "Iran, Teheran");
        Assert.assertTrue(response.isFailure());

        // check that cart didnt change
        Assert.assertEquals(1, cart.keySet().size());
        Assert.assertEquals(2, cart.get(storeID).keySet().size());
        Assert.assertTrue(cart.get(storeID).containsValue(1) && cart.get(storeID).containsValue(2));
    }

    @Test
    public void badPurchaseNoExternalConnectionsTest(){
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 7537864;
        int storeID = StoreController.getInstance().openStore("Avatar", "Tzuko").getResult();
        ProductDTO productDTO = new ProductDTO("scar", productID, storeID, 15, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);

        store.addProduct(productDTO, 5);

        for(int i = 0; i < 10; i++)
            UserController.getInstance().addToCart(guestName, storeID, productID);

        response = UserController.getInstance().purchase(guestName, "4580-1111-1111-1111", "Israel, Eilat");
        Assert.assertTrue(response.getErrMsg().contains("doesn't created external connection"));
    }

    // todo: purchase policy test when it will be implemented
}
