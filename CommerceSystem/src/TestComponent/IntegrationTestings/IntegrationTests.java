package TestComponent.IntegrationTestings;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.Review;
import Server.Domain.ShoppingManager.SearchEngine;
import Server.Domain.UserManager.*;
import Server.Service.CommerceService;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * These tests will mainly check interactions of out system with external systems
 * like payment, delivery etc..
 * as to version 1 these classes always success but that will change in future versions
 */
public class IntegrationTests {

    UserController userController;
    SearchEngine searchEngine;

    Map<String, User> registeredUsers;
    Response<Integer> store1;
    Response<Integer> store2;

    private List<String> admins;
    private User guest;

    @Before
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
        Collection<Review> review = new LinkedList<>();
        guest.addProductsToStore(new ProductDTO("beef", 0, 5, categories, keyword, review), 5).isFailure();
    }

    /** External Systems **/
    @Test
    public void externalPaymentTest(){
        //manually check our system with external payment system
        PaymentSystemAdapter payment = PaymentSystemAdapter.getInstance();
        boolean b = payment.canPay(10,"4580-1111-1111-1111");
        Assert.assertTrue(b);
    }

    @Test
    public void externalDeliveryTest(){
        //manually check our system with external payment system
        Map<Integer,Map<ProductDTO, Integer>> toDeliver = new ConcurrentHashMap<>();
        ProductDTO productDTO = new ProductDTO("chocolate", 12, 12, 33, new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), 33, 33);
        Map<ProductDTO,Integer> map1 = new ConcurrentHashMap<>();
        map1.put(productDTO, 1);
        toDeliver.put(2, map1);
        ProductSupplyAdapter supplier = ProductSupplyAdapter.getInstance();
        boolean b = supplier.canDeliver("Israel, Beer Sheba",toDeliver);
        Assert.assertTrue(b);
//
    }

    /** Search features **/
    @Test
    public void SearchByNameTest(){
        Response r1 = searchEngine.searchByKeyWord("beef");
        Assert.assertFalse(r1.isFailure());

        //empty list will be returned
        Response<List<ProductDTO>> r2 = searchEngine.searchByKeyWord("banana");
        Assert.assertTrue(r2.getResult().isEmpty());
    }

    @Test
    public void SearchByCategoryTest(){
        Response r1 = searchEngine.searchByCategory("food");
        Assert.assertFalse(r1.isFailure());

        //empty list will be returned
        Response<List<ProductDTO>> r2 = searchEngine.searchByCategory("electronics");
        Assert.assertTrue(r2.getResult().isEmpty());
    }
}
