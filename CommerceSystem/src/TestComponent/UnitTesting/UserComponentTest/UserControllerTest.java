package TestComponent.UnitTesting.UserComponentTest;


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
        assertFalse(userDAO.userExists("Jacob").getResult());
        // login for existing user w/ incorrect password
        // login for existing user w/ correct password
    }

    @Test
    public void logoutTest(){

    }

    @Test
    public void appointOwnerTest(){

    }

    @Test
    public void appointManagerTest(){

    }

    @Test
    public void removeOwnerTest(){

    }

    @Test
    public void removeManagerTest(){

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
