package TestComponent.UnitTesting.UserComponentTest;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.Review;
import Server.Domain.ShoppingManager.StoreController;
import Server.Domain.UserManager.*;
import Server.Service.CommerceService;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Assert;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//import static org.junit.jupiter.api.Assertions.*;

public class UserTests {


    private User guest;
    private User shaked;
    private User yaakov;
    private User almog;
    private UserController userController = UserController.getInstance();
    private int store1ID;
    private int store2ID;
    private ProductDTO beef;
    private ProductDTO eggs;


    @Before
    public void setUp(){
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest1 = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();
        String guest3 = userController.addGuest().getResult();

        guest = userController.getConnectedUsers().get(guestID);

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        //String shakedID = userController.login(guest1, "shaked", "jacob").getResult();
        shaked = userController.getConnectedUsers().get("shaked"); // prebuilt admin

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        yaakov = userController.getConnectedUsers().get(yaakovID);

        String almogID = userController.login(guest3, "almog", "lol2").getResult();
        almog = userController.getConnectedUsers().get(almogID);

        store1ID = userController.openStore("yaakov", "eggStore").getResult();
        store2ID = userController.openStore("almog", "meatStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");
        Collection<Review> review = new LinkedList<>();

        List<String> categories2 = new LinkedList<>();
        categories2.add("food");
        List<String> keyword2 = new LinkedList<>();
        keyword2.add("meat");

        yaakov.addProductsToStore(new ProductDTO("Medium eggs", store1ID, 2, categories1, keyword1, review), 5);
        almog.addProductsToStore(new ProductDTO("beef", store2ID, 5, categories2, keyword2, review), 5);

        Response<List<ProductDTO>> beefSearchResult = StoreController.getInstance().searchByProductName("beef");
        beef = beefSearchResult.getResult().get(0);

        Response<List<ProductDTO>> eggsSearchResult = StoreController.getInstance().searchByProductName("Medium eggs");
        eggs = eggsSearchResult.getResult().get(0);
    }

//    @After
//    public void cleanup(){
//        admins.remove("shaked");
//        testManagers = new ConcurrentHashMap<>();
//        testOwners = new ConcurrentHashMap<>();
//        shoppingCarts = new ConcurrentHashMap<>();
//        purchaseHistories = new ConcurrentHashMap<>();
//        appointments = new ConcurrentHashMap<>();
//        admins = new Vector<>();
//        store1 = CommerceService.getInstance().openStore("yaakov", "eggStore");
//        store2 = CommerceService.getInstance().openStore("almog", "meatStore");
//        guest = new User();
//        shaked = new User(new UserDTO("shaked", new ConcurrentHashMap<>(),
//                new Vector<>(), new ShoppingCart(), new PurchaseHistory(), new Appointment()));
//        yaakov = new User(new UserDTO("yaakov", new ConcurrentHashMap<>(),
//                new Vector<>(), new ShoppingCart(), new PurchaseHistory(), new Appointment()));
//        almog = new User(new UserDTO("almog", new ConcurrentHashMap<>(),
//                new Vector<>(), new ShoppingCart(), new PurchaseHistory(), new Appointment()));
//    }

    @Test
    public void openStoreAddToStoresOwned() {
        int storeID = almog.openStore("Rami Levi").getResult();
        Assert.assertTrue(almog.getStoresOwned().contains(storeID));
    }

    @Test
    public void registerSuccess() {
        Assert.assertFalse(guest.register().isFailure());
    }

    @Test
    public void registerFailureNotAGuest() {
        Assert.assertTrue(shaked.register().isFailure());
    }

    @Test
    public void logoutSuccess() {
        Assert.assertFalse(yaakov.logout().isFailure());
    }

    @Test
    public void logoutFailureNotRegistered() {
        Assert.assertTrue(guest.logout().isFailure());
    }

    @Test
    public void openStoreSuccess() {
        Assert.assertFalse(yaakov.openStore("yaakov's cool store").isFailure());
    }

    @Test
    public void openStoreFailureNotRegistered() {
        Assert.assertTrue(guest.openStore("guest's cool store").isFailure());
    }

    @Test
    public void getPurchaseHistoryContentsSuccess() {
        Assert.assertFalse(yaakov.getPurchaseHistoryContents().isFailure());
    }

    @Test
    public void getPurchaseHistoryContentsFailureNotRegistered() {
        Assert.assertTrue(guest.getPurchaseHistoryContents().isFailure());
    }

    @Test
    public void receiveStorePurchaseHistorySuccess() {
        Assert.assertFalse(shaked.getStorePurchaseHistory(store2ID).isFailure());
    }

    @Test
    public void receiveStorePurchaseHistoryFailureNotPermitted() {
        Assert.assertTrue(yaakov.getStorePurchaseHistory(store2ID).isFailure());
    }

    @Test
    public void receiveUserPurchaseHistorySuccess() {
        Assert.assertFalse(shaked.getUserPurchaseHistory("almog").isFailure());
    }

    @Test
    public void receiveUserPurchaseHistoryFailureNotPermitted() {
        Assert.assertTrue(yaakov.getUserPurchaseHistory("almog").isFailure());
    }

    @Test
    public void receiveWorkerInfoSuccess() {
        Assert.assertFalse(almog.getStoreWorkersDetails(store2ID).isFailure());
    }

    @Test
    public void receiveWorkerInfoFailureNotPermitted() {
        Assert.assertTrue(yaakov.getStoreWorkersDetails(store2ID).isFailure());
    }

    @Test
    public void receiveStoreHistorySuccess() {
        Assert.assertFalse(almog.getPurchaseDetails(store2ID).isFailure());
    }

    @Test
    public void receiveStoreHistoryFailureNotPermitted() {
        Assert.assertTrue(yaakov.getPurchaseDetails(store2ID).isFailure());
    }


}
