package TestComponent.UnitTesting.UserComponentTest;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.UserManager.*;
import Server.Service.CommerceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    private Map<String, User> registeredUsers;
    private Map<String, Map<Integer, List<Permissions>>> testManagers;
    private Map<String, List<Integer>> testOwners;
    private Map<String, ShoppingCart> shoppingCarts;
    private Map<String, PurchaseHistory> purchaseHistories;
    private Map<String, Appointment> appointments;
    private List<String> admins;
    private Response<Integer> store1;
    private Response<Integer> store2;
    private User guest;


    @BeforeEach
    public void setUp(){
        guest = new User();
        registeredUsers = new ConcurrentHashMap<>();
        testManagers = new ConcurrentHashMap<>();
        testOwners = new ConcurrentHashMap<>();
        shoppingCarts = new ConcurrentHashMap<>();
        purchaseHistories = new ConcurrentHashMap<>();
        appointments = new ConcurrentHashMap<>();
        admins = new LinkedList<>();

        registeredUsers.put("shaked", new User(new UserDTO("shaked", new ConcurrentHashMap<>(),
                new LinkedList<>(), new ShoppingCart(), new PurchaseHistory(), new Appointment())));
        registeredUsers.put("yaakov", new User(new UserDTO("yaakov", new ConcurrentHashMap<>(),
                new LinkedList<>(), new ShoppingCart(), new PurchaseHistory(), new Appointment())));
        registeredUsers.put("almog", new User(new UserDTO("almog", new ConcurrentHashMap<>(),
                new LinkedList<>(), new ShoppingCart(), new PurchaseHistory(), new Appointment())));

        admins.add("shaked");

        store1 = CommerceService.getInstance().openStore("yaakov", "eggStore");
        store2 = CommerceService.getInstance().openStore("almog", "meatStore");

        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("meat");
        Collection<String> review = new LinkedList<>();
        assertTrue(guest.addProductsToStore(new ProductDTO("beef", 0, 5, categories, keyword, review), 5).isFailure());
    }

    @Test
    public void openStore() {
        registeredUsers.get("almog").openStore("rami levi");
        assertTrue(registeredUsers.get("almog").getStoresOwned().contains(2));
    }

    @Test
    public void registerState() {
        assertFalse(guest.register().isFailure());
        assertTrue(registeredUsers.get("shaked").register().isFailure());
    }

    @Test
    public void logoutState() {
        assertTrue(guest.logout().isFailure());
        assertFalse(registeredUsers.get("yaakov").logout().isFailure());
    }

    @Test
    public void getPurchaseHistoryContentsState() {
        assertTrue(guest.getPurchaseHistoryContents().isFailure());
        assertFalse(registeredUsers.get("yaakov").getPurchaseHistoryContents().isFailure());
    }

    @Test
    public void addProductReviewState() {
        assertTrue(guest.addProductReview(0, 0, "good product").isFailure());//todo productid
        assertFalse(registeredUsers.get("almog").addProductReview(0, 0, "good product").isFailure());
    }

    @Test
    public void addProductsToStoreState() {//todo add to purchase history
        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");
        Collection<String> review = new LinkedList<>();
        review.add("good product");
        assertTrue(guest.addProductsToStore(new ProductDTO("bread", 0, 5, categories, keyword, review), 5).isFailure());
        assertFalse(registeredUsers.get("almog").addProductsToStore(new ProductDTO("bread", 1, 5, categories, keyword, review), 5).isFailure());
    }

//    @Test //todo kaki test need to redo
//    public void removeProductsFromStoreState() {//todo add to purchase history
//        List<String> categories = new LinkedList<>();
//        categories.add("food");
//        List<String> keyword = new LinkedList<>();
//        keyword.add("bread");
//        Collection<String> review = new LinkedList<>();
//        review.add("good product");
//        assertTrue(guest.removeProductsFromStore(-1, 5).isFailure());
//        assertFalse(registeredUsers.get("almog").addProductsToStore(new ProductDTO("bread", 1, 5, categories, keyword, review), 5).isFailure());
//    }
}
