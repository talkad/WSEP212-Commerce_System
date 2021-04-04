package TestComponent.UnitTesting.UserComponentTest;

import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.Appointment;
import Server.Domain.UserManager.Permissions;
import Server.Domain.UserManager.PurchaseHistory;
import Server.Domain.UserManager.ShoppingCart;
import Server.Service.CommerceService;
import org.junit.jupiter.api.BeforeEach;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserTests {

    private Map<String, String> registeredUsers;
    private Map<String, Map<Integer, List<Permissions>>> testManagers;
    private Map<String, List<Integer>> testOwners;
    private Map<String, ShoppingCart> shoppingCarts;
    private Map<String, PurchaseHistory> purchaseHistories;
    private Map<String, Appointment> appointments;
    private List<String> admins;
    private Response<Integer> store1;
    private Response<Integer> store2;


    @BeforeEach
    public void setUp(){
        registeredUsers = new ConcurrentHashMap<>();
        testManagers = new ConcurrentHashMap<>();
        testOwners = new ConcurrentHashMap<>();
        shoppingCarts = new ConcurrentHashMap<>();
        purchaseHistories = new ConcurrentHashMap<>();
        appointments = new ConcurrentHashMap<>();
        admins = new LinkedList<>();

        registeredUsers.put("shaked", Integer.toString("cohen".hashCode()));
        registeredUsers.put("yaakov", Integer.toString("shemesh".hashCode()));
        registeredUsers.put("almog", Integer.toString("davidi".hashCode()));

        admins.add("shaked");

        store1 = CommerceService.getInstance().openStore("yaakov", "eggStore");
        store2 = CommerceService.getInstance().openStore("almog", "meatStore");
    }
}
