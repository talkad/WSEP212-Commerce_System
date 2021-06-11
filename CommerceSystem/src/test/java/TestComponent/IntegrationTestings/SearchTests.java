package TestComponent.IntegrationTestings;

import Server.DAL.DALService;
import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.Review;
import Server.Domain.ShoppingManager.SearchEngine;
import Server.Domain.UserManager.*;
import Server.Domain.UserManager.DTOs.UserDTOTemp;
import Server.Service.CommerceService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * Test search features
 */

public class SearchTests {
    UserController userController;
    SearchEngine searchEngine;

    Map<String, User> registeredUsers;
    Response<Integer> store1;
    Response<Integer> store2;

    private List<String> admins;
    private User guest;

    @Before
    public void setUp(){

        CommerceSystem.getInstance().configInit("successconfigfile.json");
        DALService.getInstance().startDB();
        DALService.getInstance().resetDatabase();

        userController = UserController.getInstance();
        searchEngine = SearchEngine.getInstance();
        registeredUsers = new ConcurrentHashMap<>();

        guest = new User();
        registeredUsers = new ConcurrentHashMap<>();
        admins = new LinkedList<>();

        registeredUsers.put("shaked", new User(new UserDTOTemp("shaked", new ConcurrentHashMap<>(),
                new LinkedList<>(), new ShoppingCart(), new PurchaseHistory(), new Appointment(),new ConcurrentHashMap<>(), new PendingMessages())));
        registeredUsers.put("yaakov", new User(new UserDTOTemp("yaakov", new ConcurrentHashMap<>(),
                new LinkedList<>(), new ShoppingCart(), new PurchaseHistory(), new Appointment(), new ConcurrentHashMap<>(), new PendingMessages())));
        registeredUsers.put("almog", new User(new UserDTOTemp("almog", new ConcurrentHashMap<>(),
                new LinkedList<>(), new ShoppingCart(), new PurchaseHistory(), new Appointment(), new ConcurrentHashMap<>(),new PendingMessages())));

        admins.add("shaked");

        store1 = CommerceService.getInstance().openStore("yaakov", "eggStore");
        store2 = CommerceService.getInstance().openStore("almog", "meatStore");

        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("meat");
        Collection<Review> review = new LinkedList<>();
        guest.addProductsToStore(new ProductClientDTO("beef", 0, 5, categories, keyword), 5).isFailure();
    }


    @Test
    public void SearchByNameTestFound(){
        Response r1 = searchEngine.searchByKeyWord("beef");
        Assert.assertFalse(r1.isFailure());
    }

    @Test
    public void SearchByNameTestNotFound(){
        //empty list will be returned
        Response<List<ProductClientDTO>> r2 = searchEngine.searchByKeyWord("banana");
        Assert.assertTrue(r2.getResult().isEmpty());
    }

    @Test
    public void SearchByCategoryTestFound(){
        Response r1 = searchEngine.searchByCategory("food");
        Assert.assertFalse(r1.isFailure());
    }

    @Test
    public void SearchByCategoryTestNotFound(){
        //empty list will be returned
        Response<List<ProductClientDTO>> r2 = searchEngine.searchByCategory("electronics");
        Assert.assertTrue(r2.getResult().isEmpty());
    }
}
