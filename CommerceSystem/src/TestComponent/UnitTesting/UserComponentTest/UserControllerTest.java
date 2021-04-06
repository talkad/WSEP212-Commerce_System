package TestComponent.UnitTesting.UserComponentTest;


import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.CommerceSystem;
import Server.Domain.UserManager.User;
import Server.Domain.UserManager.UserController;
import Server.Domain.UserManager.UserDAO;
import Server.Domain.UserManager.Permissions;
import Server.Service.CommerceService;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private CommerceService commerceService;
    private UserController userController;
    private UserDAO userDAO;
    private String initialUserName;

    @BeforeEach
    public void setUp(){
        commerceService = CommerceService.getInstance();
        commerceService.init();
        userController = UserController.getInstance();
        userDAO = UserDAO.getInstance();
        initialUserName = commerceService.addGuest().getResult();
    }

    @Test
    public void removeGuestTest(){
        userController.addGuest();
        assertTrue(userController.getConnectedUsers().containsKey("Guest2"));
        userController.removeGuest("Guest2");
        assertFalse(userController.getConnectedUsers().containsKey("Guest2"));
    }

    @Test
    public void addGuestTest(){
        String guestName = userController.addGuest().getResult();
        assertTrue(userController.getConnectedUsers().containsKey(guestName));
    }

    @Test
    public void registerTest(){
        assertFalse(userDAO.userExists("Jacob2").getResult());
        assertFalse(userController.register(initialUserName, "Jacob2", "12345").isFailure());
        assertTrue(userDAO.userExists("Jacob2").getResult());
        assertTrue(userController.register(initialUserName, "Jacob2", "12345").isFailure());
    }

    @Test
    public void loginTest(){
        userController.register(initialUserName, "Jacob3", "12345");
        assertFalse(userController.getConnectedUsers().containsKey("Jacob3"));
        // login for non-existent user
        assertFalse(userDAO.userExists("Alice").getResult());
        assertTrue(userController.login(initialUserName, "Alice", "abcd").isFailure());
        // login for existing user w/ incorrect password
        assertTrue(userController.login(initialUserName, "Jacob3", "incorrect").isFailure());
        assertFalse(userController.getConnectedUsers().containsKey("Jacob3"));
        // login for existing user w/ correct password
        Response<String> response = userController.login(initialUserName, "Jacob3", "12345");
        assertFalse(response.isFailure());
        assertEquals("Jacob3", response.getResult());
        assertTrue(userController.getConnectedUsers().containsKey("Jacob3"));
    }

    @Test
    public void logoutTest(){
        userController.register(initialUserName, "Jacob", "24680");
        Response<String> loginResult = userController.login(initialUserName, "Jacob", "24680");
        String newUserName = loginResult.getResult();
        assertEquals("Jacob", newUserName);

        assertTrue(userController.getConnectedUsers().containsKey("Jacob"));

        Response<String> logoutResult = userController.logout(newUserName);
        String logoutName = logoutResult.getResult();
        assertNotEquals("Jacob", logoutName);

        assertFalse(userController.getConnectedUsers().containsKey("Jacob"));
    }

    @Test
    public void appointOwnerTest(){
        String guest = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal", "kadosh");
        userController.register(initialUserName, "yoni", "pis");

        Response<String> login = userController.login(initialUserName, "yoni", "pis");
        String newUserName = login.getResult();
        userController.login(guest, "tal", "kadosh");

        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore");
        int storeID = storeRes.getResult();

        User tal = userController.getConnectedUsers().get("tal");
        assertFalse(tal.isOwner(storeID));

        userController.appointOwner(newUserName, "tal", storeID);

        tal = userController.getConnectedUsers().get("tal");
        assertTrue(tal.isOwner(storeID));
    }

    @Test
    public void appointManagerTest(){
        String guest = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal", "kadosh");
        userController.register(initialUserName, "yoni", "pis");

        Response<String> login = userController.login(initialUserName, "yoni", "pis");
        String newUserName = login.getResult();
        userController.login(guest, "tal", "kadosh");

        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore");
        int storeID = storeRes.getResult();

        User tal = userController.getConnectedUsers().get("tal");
        assertFalse(tal.isManager(storeID));

        userController.appointManager(newUserName, "tal", storeID);

        tal = userController.getConnectedUsers().get("tal");
        assertTrue(tal.isManager(storeID));
    }

    @Test
    public void removeOwnerTest(){
        String guest = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal9", "kadosh");
        userController.register(initialUserName, "yoni9", "pis");

        Response<String> login = userController.login(initialUserName, "yoni9", "pis");
        String newUserName = login.getResult();
        userController.login(guest, "tal9", "kadosh");

        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore9");
        int storeID = storeRes.getResult();

        User tal = userController.getConnectedUsers().get("tal9");
        assertFalse(tal.isOwner(storeID));

        userController.appointOwner(newUserName, "tal9", storeID);

        tal = userController.getConnectedUsers().get("tal9");
        assertTrue(tal.isOwner(storeID));

        userController.removeOwnerAppointment(newUserName, "tal9", storeID);

        tal = userController.getConnectedUsers().get("tal9");
        assertFalse(tal.isOwner(storeID));

    }

    @Test
    public void removeManagerTest(){
        String guest = commerceService.addGuest().getResult();
        userController.register(initialUserName, "tal8", "kadosh");
        userController.register(initialUserName, "yoni8", "pis");

        Response<String> login = userController.login(initialUserName, "yoni8", "pis");
        String newUserName = login.getResult();
        userController.login(guest, "tal8", "kadosh");

        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore8");
        int storeID = storeRes.getResult();

        User tal = userController.getConnectedUsers().get("tal8");
        assertFalse(tal.isManager(storeID));

        userController.appointManager(newUserName, "tal8", storeID);

        tal = userController.getConnectedUsers().get("tal8");
        assertTrue(tal.isManager(storeID));

        userController.removeManagerAppointment(newUserName, "tal8", storeID);

        tal = userController.getConnectedUsers().get("tal8");
        assertFalse(tal.isManager(storeID));
    }

    @Test
    public void cascadingRemovalTest() {
        String guest = commerceService.addGuest().getResult();
        String guest2 = commerceService.addGuest().getResult();
        String guest3 = commerceService.addGuest().getResult();
        String guest4 = commerceService.addGuest().getResult();
        String guest5 = commerceService.addGuest().getResult();

        userController.register(initialUserName, "tal7", "kadosh");
        userController.register(initialUserName, "yoni7", "pis");
        userController.register(initialUserName, "jacob7", "lol");
        userController.register(initialUserName, "shaked67", "lol");
        userController.register(initialUserName, "aviad7", "lol");
        userController.register(initialUserName, "almog7", "lol");

        Response<String> yoniLogin = userController.login(initialUserName, "yoni7", "pis");
        String yoniUserName = yoniLogin.getResult();
        Response<String> talLogin = userController.login(guest, "tal7", "kadosh");
        String talUserName = talLogin.getResult();
        Response<String> jacobLogin = userController.login(guest2, "jacob7", "lol");
        String jacobUserName = jacobLogin.getResult();
        userController.login(guest3, "shaked67", "lol");
        userController.login(guest4, "aviad7", "lol");
        userController.login(guest5, "almog7", "lol");

        Response<Integer> storeRes = userController.openStore(yoniUserName, "eggStore7");
        int storeID = storeRes.getResult();

        userController.appointOwner(yoniUserName, "tal7", storeID);
        userController.appointManager(talUserName, "jacob7", storeID);
        userController.addPermission(talUserName, storeID, "jacob7", Permissions.APPOINT_MANAGER);
        userController.appointOwner(talUserName, "aviad7", storeID);
        userController.appointOwner(talUserName, "almog7", storeID);
        userController.appointManager(jacobUserName, "shaked67", storeID);

        assertTrue(userController.getConnectedUsers().get("yoni7").isOwner(storeID));
        assertTrue(userController.getConnectedUsers().get("tal7").isOwner(storeID));
        assertTrue(userController.getConnectedUsers().get("jacob7").isManager(storeID));
        assertTrue(userController.getConnectedUsers().get("aviad7").isOwner(storeID));
        assertTrue(userController.getConnectedUsers().get("almog7").isOwner(storeID));
        assertTrue(userController.getConnectedUsers().get("shaked67").isManager(storeID));

        userController.removeOwnerAppointment(yoniUserName, "tal7", storeID);

        assertTrue(userController.getConnectedUsers().get("yoni7").isOwner(storeID));
        assertFalse(userController.getConnectedUsers().get("tal7").isOwner(storeID));
        assertFalse(userController.getConnectedUsers().get("jacob7").isManager(storeID));
        assertFalse(userController.getConnectedUsers().get("aviad7").isOwner(storeID));
        assertFalse(userController.getConnectedUsers().get("almog7").isOwner(storeID));
        assertFalse(userController.getConnectedUsers().get("shaked67").isManager(storeID));
    }

    @Test
    public void addPermissionTest(){
        String guest = commerceService.addGuest().getResult();

        userController.register(initialUserName, "tal6", "kadosh");
        userController.register(initialUserName, "yoni6", "pis");

        Response<String> yoniLogin = userController.login(initialUserName, "yoni6", "pis");
        String yoniUserName = yoniLogin.getResult();
        userController.login(guest, "tal6", "kadosh");

        Response<Integer> storeRes = userController.openStore(yoniUserName, "eggStore6");
        int storeID = storeRes.getResult();

        userController.appointManager(yoniUserName, "tal6", storeID);

        assertFalse(userController.getConnectedUsers().get("tal6").getStoresManaged().get(storeID).contains(Permissions.ADD_PRODUCT_TO_STORE));

        userController.addPermission(yoniUserName, storeID, "tal6", Permissions.ADD_PRODUCT_TO_STORE);

        assertTrue(userController.getConnectedUsers().get("tal6").getStoresManaged().get(storeID).contains(Permissions.ADD_PRODUCT_TO_STORE));

    }

    @Test
    public void removePermissionTest(){
        String guest = commerceService.addGuest().getResult();

        userController.register(initialUserName, "tal5", "kadosh");
        userController.register(initialUserName, "yoni5", "pis");

        Response<String> yoniLogin = userController.login(initialUserName, "yoni5", "pis");
        String yoniUserName = yoniLogin.getResult();
        userController.login(guest, "tal5", "kadosh");

        Response<Integer> storeRes = userController.openStore(yoniUserName, "eggStore5");
        int storeID = storeRes.getResult();

        userController.appointManager(yoniUserName, "tal5", storeID);
        userController.addPermission(yoniUserName, storeID, "tal5", Permissions.ADD_PRODUCT_TO_STORE);

        assertTrue(userController.getConnectedUsers().get("tal5").getStoresManaged().get(storeID).contains(Permissions.ADD_PRODUCT_TO_STORE));

        userController.removePermission(yoniUserName, storeID, "tal5", Permissions.ADD_PRODUCT_TO_STORE);

        assertFalse(userController.getConnectedUsers().get("tal5").getStoresManaged().get(storeID).contains(Permissions.ADD_PRODUCT_TO_STORE));
    }

    @Test
    public void getStoreWorkersDetailsTest(){
        String guest = commerceService.addGuest().getResult();
        String guest2 = commerceService.addGuest().getResult();
        String guest3 = commerceService.addGuest().getResult();
        String guest4 = commerceService.addGuest().getResult();
        String guest5 = commerceService.addGuest().getResult();

        userController.register(initialUserName, "tal4", "kadosh");
        userController.register(initialUserName, "yoni4", "pis");
        userController.register(initialUserName, "jacob4", "lol");
        userController.register(initialUserName, "shaked64", "lol");
        userController.register(initialUserName, "aviad4", "lol");
        userController.register(initialUserName, "almog4", "lol");

        Response<String> yoniLogin = userController.login(initialUserName, "yoni4", "pis");
        String yoniUserName = yoniLogin.getResult();
        Response<String> talLogin = userController.login(guest, "tal4", "kadosh");
        String talUserName = talLogin.getResult();
        Response<String> jacobLogin = userController.login(guest2, "jacob4", "lol");
        String jacobUserName = jacobLogin.getResult();
        userController.login(guest3, "shaked64", "lol");
        userController.login(guest4, "aviad4", "lol");
        userController.login(guest5, "almog4", "lol");

        Response<Integer> storeRes = userController.openStore(yoniUserName, "eggStore");
        int storeID = storeRes.getResult();

        userController.appointOwner(yoniUserName, "tal4", storeID);
        userController.appointManager(talUserName, "jacob4", storeID);
        userController.addPermission(talUserName, storeID, "jacob4", Permissions.APPOINT_MANAGER);
        userController.appointOwner(talUserName, "aviad4", storeID);
        userController.appointOwner(talUserName, "almog4", storeID);
        userController.appointManager(jacobUserName, "shaked64", storeID);

        Response<List<User>> result = userController.getStoreWorkersDetails(yoniUserName, storeID);
        assertFalse(result.isFailure());
        List<User> actualUsers = result.getResult();

        List<String> users = new Vector<>();
        users.add("yoni4");
        users.add("tal4");
        users.add("jacob4");
        users.add("aviad4");
        users.add("almog4");
        users.add("shaked64");

        assertEquals(6, users.size());
        for(User user : actualUsers){
            assertTrue(users.contains(user.getName()));
        }
    }
}
/*CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        commerceService.addGuest();
        // Guest1
        commerceService.register("Guest1", "tal", "kadosh");
        System.out.println(commerceService.logout("Guest1").isFailure());
        System.out.println(commerceService.login("Guest1", "tal", "kadosh").getResult());
        commerceService.logout("tal");
        //Guest2
        System.out.println(commerceService.register("Guest2", "yoni", "pis").isFailure());
        System.out.println(commerceService.login("Guest2", "yoni", "pis").isFailure());

        Response<Integer> res = commerceService.openStore("yoni", "eggStore");
        commerceService.appointStoreOwner("yoni", "tal", res.getResult());
        commerceService.logout("yoni");
        //Guest3
        System.out.println(commerceService.register("Guest3", "jacob", "lol").isFailure());
        System.out.println(commerceService.register("Guest3", "jacob2", "lol").isFailure());
        System.out.println(commerceService.register("Guest3", "jacob3", "lol").isFailure());
        System.out.println(commerceService.register("Guest3", "jacob4", "lol").isFailure());
        System.out.println(commerceService.login("Guest3", "tal", "kadosh").isFailure());
        commerceService.appointStoreOwner("tal", "jacob", res.getResult());
        commerceService.appointStoreOwner("tal", "jacob2", res.getResult());
        commerceService.appointStoreManager("tal", "jacob3", res.getResult());
        commerceService.addPermission("tal", res.getResult(), "jacob3", Permissions.ADD_PRODUCT_TO_STORE);
        commerceService.removePermission("tal", res.getResult(), "jacob3", Permissions.ADD_PRODUCT_TO_STORE);
        commerceService.addPermission("tal", res.getResult(), "jacob3", Permissions.APPOINT_MANAGER);
        commerceService.removePermission("tal", res.getResult(), "jacob3", Permissions.APPOINT_MANAGER);

        commerceService.logout("tal");
        //Guest4
        System.out.println(commerceService.login("Guest4", "jacob3", "lol").isFailure());
        commerceService.appointStoreManager("jacob3", "jacob4", res.getResult());
        commerceService.logout("jacob3");
        //Guest5
        System.out.println(commerceService.login("Guest5", "yoni", "pis").isFailure());
        //commerceService.removeOwnerAppointment("yoni", "tal", res.getResult());
        for(User user : commerceService.getStoreWorkersDetails("yoni", res.getResult()).getResult()){
            System.out.println(user.getName());
        }
        commerceService.logout("yoni");
        //Guest6


        System.out.println(UserDAO.getInstance().getAppointments("yoni", res.getResult()).getResult().toString());
        System.out.println(UserDAO.getInstance().getAppointments("tal", res.getResult()).getResult().toString());
        System.out.println(UserDAO.getInstance().getAppointments("jacob3", res.getResult()).getResult().toString());

        System.out.println(UserDAO.getInstance().getUser("jacob3").getStoresManaged().get(res.getResult()));
        System.out.println(UserDAO.getInstance().getUser("jacob4").getStoresManaged().get(res.getResult()));*/