package TestComponent.UnitTesting.UserComponentTest;


import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.CommerceSystem;
import Server.Domain.UserManager.User;
import Server.Domain.UserManager.UserController;
import Server.Domain.UserManager.UserDAO;
import Server.Service.CommerceService;
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
        assertFalse(userController.getConnectedUsers().containsKey("Guest2"));
        assertEquals("Guest2", userController.addGuest().getResult());
        assertTrue(userController.getConnectedUsers().containsKey("Guest2"));
    }

    @Test
    public void registerTest(){
        assertFalse(userDAO.userExists("Jacob").getResult());
        userController.register(initialUserName, "Jacob", "12345");
        assertTrue(userDAO.userExists("Jacob").getResult());
    }

    @Test
    public void loginTest(){
        userController.register(initialUserName, "Jacob", "12345");
        // login for non-existent user
        assertFalse(userDAO.userExists("Alice").getResult());
        assertTrue(userController.login(initialUserName, "Alice", "abcd").isFailure());
        // login for existing user w/ incorrect password
        assertTrue(userController.login(initialUserName, "Jacob", "incorrect").isFailure());
        // login for existing user w/ correct password
        Response<String> response = userController.login(initialUserName, "Jacob", "12345");
        assertFalse(response.isFailure());
        assertEquals("Jacob", response.getResult());
    }

    @Test
    public void logoutTest(){
        userController.register(initialUserName, "Jacob", "24680");
        Response<String> loginResult = userController.login(initialUserName, "Jacob", "24680");
        String newUserName = loginResult.getResult();
        assertEquals("Jacob", newUserName);

        Response<String> logoutResult = userController.logout(newUserName);
        String logoutName = logoutResult.getResult();
        assertNotEquals("Jacob", logoutName);
    }

    @Test
    public void appointOwnerTest(){
        userController.register(initialUserName, "tal", "kadosh");
        userController.register(initialUserName, "yoni", "pis");

        Response<String> login = userController.login(initialUserName, "yoni", "pis");
        String newUserName = login.getResult();
        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore");
        int storeID = storeRes.getResult();

        User tal = new User(UserDAO.getInstance().getUser("tal"));
        assertFalse(tal.isOwner(storeID));

        commerceService.appointStoreOwner(newUserName, "tal", storeID);

        tal = new User(UserDAO.getInstance().getUser("tal"));
        assertTrue(tal.isOwner(storeID));
    }

    @Test
    public void appointManagerTest(){
        userController.register(initialUserName, "tal", "kadosh");
        userController.register(initialUserName, "yoni", "pis");

        Response<String> login = userController.login(initialUserName, "yoni", "pis");
        String newUserName = login.getResult();
        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore");
        int storeID = storeRes.getResult();

        User tal = new User(UserDAO.getInstance().getUser("tal"));
        assertFalse(tal.isManager(storeID));

//        userController.appointStoreManager(newUserName, "tal", storeID);

        tal = new User(UserDAO.getInstance().getUser("tal"));
        assertTrue(tal.isManager(storeID));
    }

    @Test
    public void removeOwnerTest(){
        userController.register(initialUserName, "tal", "kadosh");
        userController.register(initialUserName, "yoni", "pis");

        Response<String> login = userController.login(initialUserName, "yoni", "pis");
        String newUserName = login.getResult();
        Response<Integer> storeRes = userController.openStore(newUserName, "eggStore");
        int storeID = storeRes.getResult();

        User tal = new User(UserDAO.getInstance().getUser("tal"));
        assertFalse(tal.isOwner(storeID));

        userController.appointOwner(newUserName, "tal", storeID);

        tal = new User(UserDAO.getInstance().getUser("tal"));
        assertTrue(tal.isOwner(storeID));

        userController.removeOwnerAppointment(newUserName, "tal", storeID);

        tal = new User(UserDAO.getInstance().getUser("tal"));
        assertFalse(tal.isOwner(storeID));

    }

    @Test
    public void removeManagerTest(){
/**/
    }

    @Test
    public void cascadingRemovalTest(){

    }

    @Test
    public void addPermissionTest(){

    }

    @Test
    public void removePermissionTest(){

    }

    @Test
    public void getStoreWorkersDetailsTest(){

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