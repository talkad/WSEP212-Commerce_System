package TestComponent.IntegrationTestings;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.SearchEngine;
import Server.Domain.UserManager.*;
import Server.Service.CommerceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrationTests {

    UserController userController;
    SearchEngine searchEngine;

    Map<String, User> registeredUsers;
    Response<Integer> store1;
    Response<Integer> store2;

    private List<String> admins;
    private User guest;

    @BeforeEach
    public void setUp(){
        userController = UserController.getInstance();
        searchEngine = SearchEngine.getInstance();
        registeredUsers = new ConcurrentHashMap<>();

        guest = new User();
        registeredUsers = new ConcurrentHashMap<>();
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
        guest.addProductsToStore(new ProductDTO("beef", 0, 5, categories, keyword, review), 5).isFailure();
    }

    /** External Systems **/
    @Test
    public void externalPaymentTest(){
        //manually
        PaymentSystemAdapter payment = PaymentSystemAdapter.getInstance();
        payment.pay(10,"123456");

        //using handler
        PurchaseController pc = PurchaseController.getInstance();
        ShoppingCart shoppingCart = new ShoppingCart();
        Response r = pc.handlePayment("123456",shoppingCart,"Shoham");
        assertFalse(r.isFailure());
    }

    @Test
    public void externalDeliveryTest(){
        //manually
        Map<Integer,Map<ProductDTO, Integer>> toDeliver = new ConcurrentHashMap<>();
        ProductDTO productDTO = new ProductDTO("chocolate", 12, 12, 33, new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), 33, 33);
        Map<ProductDTO,Integer> map1 = new ConcurrentHashMap<>();
        map1.put(productDTO, 1);
        toDeliver.put(2, map1);
        ProductSupplyAdapter supplier = ProductSupplyAdapter.getInstance();
        supplier.deliver("Beer Sheba",toDeliver);

        //using handler
        PurchaseController pc = PurchaseController.getInstance();
        ShoppingCart shoppingCart = new ShoppingCart();
        Response r = pc.handlePayment("654321",shoppingCart,"Beer Sheba");
        assertFalse(r.isFailure());
    }

    /** Search features **/
    @Test
    public void SearchByNameTest(){
        Response r1 = searchEngine.searchByKeyWord("beef");
        assertFalse(r1.isFailure());

        //empty list will be returned
        Response<List<ProductDTO>> r2 = searchEngine.searchByKeyWord("banana");
        assertTrue(r2.getResult().isEmpty());
    }

    @Test
    public void SearchByCategoryTest(){
        Response r1 = searchEngine.searchByCategory("food");
        assertFalse(r1.isFailure());

        //empty list will be returned
        Response<List<ProductDTO>> r2 = searchEngine.searchByCategory("electronics");
        assertTrue(r2.getResult().isEmpty());
    }
}
