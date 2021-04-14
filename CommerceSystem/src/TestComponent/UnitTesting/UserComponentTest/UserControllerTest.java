package TestComponent.UnitTesting.UserComponentTest;


import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.User;
import Server.Domain.UserManager.UserController;
import Server.Domain.UserManager.UserDAO;
import Server.Domain.UserManager.Permissions;
import Server.Service.CommerceService;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class UserControllerTest {

    private CommerceService commerceService;
    private UserController userController;
    private UserDAO userDAO;
    private String initialUserName;

    @Before
    public void setUp(){
        commerceService = CommerceService.getInstance();
        commerceService.init();
        userController = UserController.getInstance();
        userDAO = UserDAO.getInstance();
        initialUserName = commerceService.addGuest().getResult();
    }

    @Test
    public void removeGuestTest(){
        String guestName = userController.addGuest().getResult();
        Assert.assertTrue(userController.getConnectedUsers().containsKey(guestName));
        userController.removeGuest(guestName);
        Assert.assertFalse(userController.getConnectedUsers().containsKey(guestName));
    }

    @Test
    public void addGuestTest(){
        String guestName = userController.addGuest().getResult();
        Assert.assertTrue(userController.getConnectedUsers().containsKey(guestName));
    }

    @Test
    public void registerTestMainScenario(){
        Assert.assertFalse(userDAO.userExists("Jacob2").getResult());
        Assert.assertFalse(userController.register(initialUserName, "Jacob2", "12345").isFailure());
        Assert.assertTrue(userDAO.userExists("Jacob2").getResult());
    }

    @Test
    public void registerTestFailureUserAlreadyExists(){
        Assert.assertFalse(userController.register(initialUserName, "JacobLine54", "12345").isFailure());
        Assert.assertTrue(userDAO.userExists("JacobLine54").getResult());
        Assert.assertTrue(userController.register(initialUserName, "JacobLine54", "12345").isFailure());
    }

    @Test
    public void loginTestSuccess(){
        userController.register(initialUserName, "Jacob3", "12345");
        Assert.assertFalse(userController.getConnectedUsers().containsKey("Jacob3"));

        // login for existing user w/ correct password
        Response<String> response = userController.login(initialUserName, "Jacob3", "12345");
        Assert.assertFalse(response.isFailure());
        Assert.assertEquals("Jacob3", response.getResult());
        Assert.assertTrue(userController.getConnectedUsers().containsKey("Jacob3"));
    }

    @Test
    public void loginTestFailureNonExistentUser(){
        // login for non-existent user
        Assert.assertFalse(userDAO.userExists("Alice").getResult());
        Assert.assertTrue(userController.login(initialUserName, "Alice", "abcd").isFailure());
    }

    @Test
    public void loginTestFailureIncorrectPassword(){
        userController.register(initialUserName, "JacobLine82", "12345");
        Assert.assertFalse(userController.getConnectedUsers().containsKey("JacobLine82"));

        // login for existing user w/ incorrect password
        Assert.assertTrue(userController.login(initialUserName, "JacobLine82", "incorrect").isFailure());
        Assert.assertFalse(userController.getConnectedUsers().containsKey("JacobLine82"));
    }

    @Test
    public void logoutTestSuccess(){
        // initial user login
        userController.register(initialUserName, "Jacob", "24680");
        Response<String> loginResult = userController.login(initialUserName, "Jacob", "24680");
        String newUserName = loginResult.getResult();
        Assert.assertEquals("Jacob", newUserName);
        // user is connected
        Assert.assertTrue(userController.getConnectedUsers().containsKey("Jacob"));
        // successful logout
        Response<String> logoutResult = userController.logout(newUserName);
        String logoutName = logoutResult.getResult();
        Assert.assertNotEquals("Jacob", logoutName);
        // user is disconnected
        Assert.assertFalse(userController.getConnectedUsers().containsKey("Jacob"));
    }

    @Test
    public void logoutTestFailureNotLoggedIn(){
        // attempted logout of guest
        Assert.assertTrue(userController.getConnectedUsers().containsKey(initialUserName));
        Assert.assertTrue(userController.logout(initialUserName).isFailure());
        Assert.assertTrue(userController.getConnectedUsers().containsKey(initialUserName));
    }
}