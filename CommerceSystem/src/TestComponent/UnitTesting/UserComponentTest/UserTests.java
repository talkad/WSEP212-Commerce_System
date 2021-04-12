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
    public void openStore() {
        int storeID = almog.openStore("Rami Levi").getResult();
        //System.out.println(almog.getStoresOwned().toString());
        //System.out.println(yaakov.getStoresOwned().toString());
        Assert.assertTrue(almog.getStoresOwned().contains(storeID));
    }

    @Test
    public void registerState() {
        Assert.assertFalse(guest.register().isFailure());
        Assert.assertTrue(shaked.register().isFailure());
    }

    @Test
    public void logoutState() {
        Assert.assertTrue(guest.logout().isFailure());
        Assert.assertFalse(yaakov.logout().isFailure());
    }

    @Test
    public void openStoreState() {
        Assert.assertTrue(guest.openStore("guest's cool store").isFailure());
        Assert.assertFalse(yaakov.openStore("yaakov's cool store").isFailure());
    }

    @Test
    public void getPurchaseHistoryContentsState() {
        Assert.assertTrue(guest.getPurchaseHistoryContents().isFailure());
        Assert.assertFalse(yaakov.getPurchaseHistoryContents().isFailure());
    }

    @Test
    public void addProductReviewState() {
        userController.addToCart(almog.getName(), beef.getStoreID(), beef.getProductID());
        userController.purchase(almog.getName(), "1234567890", "mercaz");

        Assert.assertTrue(guest.addProductReview(store2ID, beef.getProductID(), "good product").isFailure());//todo productid
        Assert.assertFalse(almog.addProductReview(store2ID, beef.getProductID(), "good product").isFailure());
    }

    @Test
    public void addProductsToStoreState() {//todo add to purchase history
        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");
        Collection<Review> review = new LinkedList<>();

        Assert.assertFalse(yaakov.addProductsToStore(new ProductDTO("XL eggs", store1ID, 5, categories, keyword, review), 5).isFailure());
        Assert.assertTrue(yaakov.addProductsToStore(new ProductDTO("steak", store2ID, 5, categories, keyword, review), 5).isFailure());
    }

    @Test
    public void removeProductsFromStoreState() {//todo add to purchase history
        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");
        Collection<Review> review = new LinkedList<>();
        yaakov.addProductsToStore(new ProductDTO("XXL eggs", store1ID, 10, categories, keyword, review), 5);
        Response<List<ProductDTO>> result = StoreController.getInstance().searchByProductName("XXL eggs");
        ProductDTO productDTO = result.getResult().get(0);

        Assert.assertTrue(almog.removeProductsFromStore(store1ID, productDTO.getProductID(), 5).isFailure());
        Assert.assertFalse(yaakov.removeProductsFromStore(store1ID, productDTO.getProductID(), 5).isFailure());
    }

    @Test
    public void editProductState() {
        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");
        Collection<Review> review = new LinkedList<>();
        almog.addProductsToStore(new ProductDTO("chicken", store2ID, 10, categories, keyword, review), 5);
        Response<List<ProductDTO>> result = StoreController.getInstance().searchByProductName("chicken");
        ProductDTO productDTO = result.getResult().get(0);

        Assert.assertTrue(yaakov.updateProductInfo(store2ID, productDTO.getProductID(), 15, "big chicken").isFailure());
        Assert.assertFalse(almog.updateProductInfo(store2ID, productDTO.getProductID(), 15, "big chicken").isFailure());
    }

    @Test
    public void receiveGeneralHistoryState() {
        Assert.assertTrue(yaakov.getUserPurchaseHistory("almog").isFailure());
        Assert.assertFalse(shaked.getUserPurchaseHistory("almog").isFailure());

        Assert.assertTrue(yaakov.getStorePurchaseHistory(store2ID).isFailure());
        Assert.assertFalse(shaked.getStorePurchaseHistory(store2ID).isFailure());
    }

    @Test
    public void appointOwnerState() {
        Assert.assertFalse(shaked.isOwner(store1ID));
        Assert.assertTrue(almog.appointOwner("shaked", store1ID).isFailure());
        Assert.assertFalse(shaked.isOwner(store1ID));
        Assert.assertFalse(yaakov.appointOwner("shaked", store1ID).isFailure());
        Assert.assertTrue(shaked.isOwner(store1ID));
    }

    @Test
    public void removeOwnerAppointmentState() {
        yaakov.appointOwner("shaked", store1ID);
        Assert.assertTrue(shaked.isOwner(store1ID));
        Assert.assertTrue(almog.removeAppointment("shaked", store1ID).isFailure());
        Assert.assertTrue(shaked.isOwner(store1ID));
        Assert.assertFalse(yaakov.removeAppointment("shaked", store1ID).isFailure());
    }

    @Test
    public void appointManagerState() {
        Assert.assertFalse(shaked.isManager(store1ID));
        Assert.assertTrue(almog.appointManager("shaked", store1ID).isFailure());
        Assert.assertFalse(shaked.isManager(store1ID));
        Assert.assertFalse(yaakov.appointManager("shaked", store1ID).isFailure());
        Assert.assertTrue(shaked.isManager(store1ID));
    }

    @Test
    public void removeManagerAppointmentState() {
        yaakov.appointManager("shaked", store1ID);
        Assert.assertTrue(shaked.isManager(store1ID));
        Assert.assertTrue(almog.removeAppointment("shaked", store1ID).isFailure());
        Assert.assertTrue(shaked.isManager(store1ID));
        Assert.assertFalse(yaakov.removeAppointment("shaked", store1ID).isFailure());
    }

    @Test
    public void editManagerPermissionState() {
        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");
        Collection<Review> review = new LinkedList<>();

        yaakov.appointManager("shaked", store1ID);
        Assert.assertTrue(shaked.addProductsToStore(new ProductDTO("egg carton", store1ID, 10, categories, keyword, review), 5).isFailure());
        Assert.assertTrue(almog.addPermission(store1ID, "shaked", Permissions.ADD_PRODUCT_TO_STORE).isFailure());
        Assert.assertTrue(shaked.addProductsToStore(new ProductDTO("egg carton", store1ID, 10, categories, keyword, review), 5).isFailure());
        Assert.assertFalse(yaakov.addPermission(store1ID, "shaked", Permissions.ADD_PRODUCT_TO_STORE).isFailure());
        Assert.assertFalse(shaked.addProductsToStore(new ProductDTO("egg carton", store1ID, 10, categories, keyword, review), 5).isFailure());
    }

    @Test
    public void receiveWorkerInfoState() {
        Assert.assertTrue(yaakov.getStoreWorkersDetails(store2ID).isFailure());
        Assert.assertFalse(almog.getStoreWorkersDetails(store2ID).isFailure());
    }

    @Test
    public void receiveStoreHistoryState() {
        Assert.assertTrue(yaakov.getPurchaseDetails(store2ID).isFailure());
        Assert.assertFalse(almog.getPurchaseDetails(store2ID).isFailure());
    }


}
