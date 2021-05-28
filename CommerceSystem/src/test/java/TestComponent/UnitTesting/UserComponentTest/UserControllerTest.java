package TestComponent.UnitTesting.UserComponentTest;


import Server.DAL.DALService;
import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.UserController;
import Server.Domain.UserManager.UserDAO;
import Server.Service.CommerceService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserControllerTest {

    @Before
    public void setUp(){
        DALService.getInstance().useTestDatabase();
        DALService.getInstance().resetDatabase();
    }

    @Test
    public void removeGuestTest(){
        UserController userController = UserController.getInstance();
        String guestName = userController.addGuest().getResult();

        Assert.assertTrue(userController.getConnectedUsers().containsKey(guestName));
        userController.removeGuest(guestName);
        Assert.assertFalse(userController.getConnectedUsers().containsKey(guestName));
    }

    @Test
    public void addGuestTest(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();

        String guestName = userController.addGuest().getResult();
        Assert.assertTrue(userController.getConnectedUsers().containsKey(guestName));
    }

    @Test
    public void registerTestMainScenario(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        UserDAO userDAO = UserDAO.getInstance();
        String initialUserName = commerceService.addGuest().getResult();


        Assert.assertNull(DALService.getInstance().getAccount("Jacob2"));
        Assert.assertFalse(userController.register(initialUserName, "Jacob2", "12345").isFailure());
        Assert.assertNotNull(DALService.getInstance().getAccount("Jacob2"));
    }

    @Test
    public void registerTestFailureUserAlreadyExists(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        UserDAO userDAO = UserDAO.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        Assert.assertFalse(userController.register(initialUserName, "JacobLine54", "12345").isFailure());
        Assert.assertNotNull(DALService.getInstance().getAccount("JacobLine54"));
        Assert.assertTrue(userController.register(initialUserName, "JacobLine54", "12345").isFailure());
    }

    @Test
    public void loginTestSuccess(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

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
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        UserDAO userDAO = UserDAO.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        // login for non-existent user
        Assert.assertFalse(userDAO.userExists("Alice"));
        Assert.assertTrue(userController.login(initialUserName, "Alice", "abcd").isFailure());
    }

    @Test
    public void loginTestFailureIncorrectPassword(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        userController.register(initialUserName, "JacobLine82", "12345");
        Assert.assertFalse(userController.getConnectedUsers().containsKey("JacobLine82"));

        // login for existing user w/ incorrect password
        Assert.assertTrue(userController.login(initialUserName, "JacobLine82", "incorrect").isFailure());
        Assert.assertFalse(userController.getConnectedUsers().containsKey("JacobLine82"));
    }

    @Test
    public void logoutTestSuccess(){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

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
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();

        // attempted logout of guest
        Assert.assertTrue(userController.getConnectedUsers().containsKey(initialUserName));
        Assert.assertTrue(userController.logout(initialUserName).isFailure());
        Assert.assertTrue(userController.getConnectedUsers().containsKey(initialUserName));
    }

    @Test
    public void concurrencyRegistrationTest() throws InterruptedException{
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();

        AtomicInteger successes = new AtomicInteger(0);
        int numberOfThreads = 100;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                String name = userController.addGuest().getResult();
                Response<Boolean> response = userController.register(name, "John", "Doe");
                if(!response.isFailure()){
                    successes.incrementAndGet();
                }
                latch.countDown();
            });
        }

        latch.await();
        // Check that only one thread succeeded to register
        Assert.assertEquals(1, successes.get());
        service.shutdownNow();
    }

    @Test
    public void concurrencyAddGuestTest() throws InterruptedException{
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        ReadWriteLock lock = new ReentrantReadWriteLock();

        AtomicInteger duplicates = new AtomicInteger(0);
        List<String> names = new Vector<>();
        int numberOfThreads = 100;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                String name = userController.addGuest().getResult();
                lock.writeLock().lock();
                if(names.contains(name)){
                    duplicates.incrementAndGet();
                }
                else{
                    names.add(name);
                }
                lock.writeLock().unlock();
                latch.countDown();
            });
        }

        latch.await();
        // Check that there were no duplicate guest names returned
        Assert.assertEquals(0, duplicates.get());
        service.shutdownNow();
    }
}