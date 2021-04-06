package TestComponent.UnitTesting.UserComponentTest;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.UserManager.*;
import Server.Service.CommerceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    //private Map<String, User> registeredUsers;
    private Map<String, Map<Integer, List<Permissions>>> testManagers = new ConcurrentHashMap<>();
    private Map<String, List<Integer>> testOwners = new ConcurrentHashMap<>();
    private Map<String, ShoppingCart> shoppingCarts = new ConcurrentHashMap<>();
    private Map<String, PurchaseHistory> purchaseHistories = new ConcurrentHashMap<>();
    private Map<String, Appointment> appointments = new ConcurrentHashMap<>();
    private List<String> admins = new Vector<>();
    private Response<Integer> store1 = CommerceService.getInstance().openStore("yaakov", "eggStore");
    private Response<Integer> store2 = CommerceService.getInstance().openStore("almog", "meatStore");
    private User guest = new User();
    private User shaked = new User(new UserDTO("shaked", new ConcurrentHashMap<>(),
            new Vector<>(), new ShoppingCart(), new PurchaseHistory(), new Appointment()));
    private User yaakov = new User(new UserDTO("yaakov", new ConcurrentHashMap<>(),
            new Vector<>(), new ShoppingCart(), new PurchaseHistory(), new Appointment()));
    private User almog = new User(new UserDTO("almog", new ConcurrentHashMap<>(),
            new Vector<>(), new ShoppingCart(), new PurchaseHistory(), new Appointment()));
//    yaakov.addStoresOwned(0);
//    almog.addStoresOwned(1);


//    @BeforeEach
//    public void setUp(){
//        //guest = new User();
////        registeredUsers = new ConcurrentHashMap<>();
////        testManagers = new ConcurrentHashMap<>();
////        testOwners = new ConcurrentHashMap<>();
////        shoppingCarts = new ConcurrentHashMap<>();
////        purchaseHistories = new ConcurrentHashMap<>();
////        appointments = new ConcurrentHashMap<>();
////        admins = new LinkedList<>();
////        shaked = new User(new UserDTO("shaked", new ConcurrentHashMap<>(),
////                new Vector<>(), new ShoppingCart(), new PurchaseHistory(), new Appointment()));
////        //registeredUsers.put("shaked", shaked);
////        yaakov = new User(new UserDTO("yaakov", new ConcurrentHashMap<>(),
////                new Vector<>(), new ShoppingCart(), new PurchaseHistory(), new Appointment()));
////        //registeredUsers.put("yaakov", yaakov);
////        almog = new User(new UserDTO("almog", new ConcurrentHashMap<>(),
////                new Vector<>(), new ShoppingCart(), new PurchaseHistory(), new Appointment()));
//        //registeredUsers.put("almog", almog);
//
//        admins.add("shaked");
//
////        store1 = CommerceService.getInstance().openStore("yaakov", "eggStore");
////        store2 = CommerceService.getInstance().openStore("almog", "meatStore");
//
//
//        List<String> categories1 = new LinkedList<>();
//        categories1.add("food");
//        List<String> keyword1 = new LinkedList<>();
//        keyword1.add("egg");
//        Collection<String> review = new LinkedList<>();
//
//        List<String> categories2 = new LinkedList<>();
//        categories1.add("food");
//        List<String> keyword2 = new LinkedList<>();
//        keyword1.add("meat");
//        yaakov.addStoresOwned(0);
//        almog.addStoresOwned(1);
//        yaakov.addProductsToStore(new ProductDTO("Medium eggs", 0, 2, categories1, keyword1, review), 5);
//        almog.addProductsToStore(new ProductDTO("beef", 1, 5, categories2, keyword2, review), 5);
//    }
//
//    @AfterEach
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
        almog.openStore("Rami Levi");
        System.out.println(almog.getStoresOwned().toString());
        System.out.println(yaakov.getStoresOwned().toString());
        assertTrue(almog.getStoresOwned().contains(2));
    }

    @Test
    public void registerState() {
        assertFalse(guest.register().isFailure());
        assertTrue(shaked.register().isFailure());
    }

    @Test
    public void logoutState() {
        assertTrue(guest.logout().isFailure());
        assertFalse(yaakov.logout().isFailure());
    }

    @Test
    public void getPurchaseHistoryContentsState() {
        assertTrue(guest.getPurchaseHistoryContents().isFailure());
        assertFalse(yaakov.getPurchaseHistoryContents().isFailure());
    }

//    @Test
//    public void addProductReviewState() {
//        assertTrue(guest.addProductReview(0, 0, "good product").isFailure());//todo productid
//        assertFalse(registeredUsers.get("almog").addProductReview(0, 0, "good product").isFailure());
//    }

    @Test
    public void addProductsToStoreState() {//todo add to purchase history
        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");
        Collection<String> review = new LinkedList<>();
        assertFalse(yaakov.addProductsToStore(new ProductDTO("XL eggs", 0, 5, categories, keyword, review), 5).isFailure());
        assertTrue(yaakov.addProductsToStore(new ProductDTO("steak", 1, 5, categories, keyword, review), 5).isFailure());
    }

    @Test
    public void removeProductsFromStoreState() {//todo add to purchase history
        assertTrue(yaakov.removeProductsFromStore(1, -1, 5).isFailure());
        assertFalse(almog.removeProductsFromStore(1, -1, 5).isFailure());
    }
}
