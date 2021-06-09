package TestComponent.UnitTesting.UserComponentTest;

import Server.DAL.DALService;
import Server.Domain.UserManager.PermissionsEnum;
import Server.Domain.UserManager.User;
import Server.Domain.UserManager.UserController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class UserTests {

    @Before
    public void setUp(){
        DALService.getInstance().useTestDatabase();
        DALService.getInstance().startDB();
        DALService.getInstance().resetDatabase();
    }

    @Test
    public void openStoreAddToStoresOwned() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guest2 = userController.addGuest().getResult();
        userController.register(guest2, "almog", "lol");
        String almogID = userController.login(guest2, "almog", "lol").getResult();
        User almog = userController.getConnectedUsers().get(almogID);

        int storeID = almog.openStore("Rami Levi").getResult();
        Assert.assertTrue(almog.getStoresOwned().contains(storeID));
    }

    @Test
    public void registerSuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        User guest = userController.getConnectedUsers().get(guestID);

        Assert.assertFalse(guest.register().isFailure());
    }

    @Test
    public void registerFailureNotAGuest() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        User shaked = userController.getConnectedUsers().get("shaked"); // prebuilt admin

        Assert.assertTrue(shaked.register().isFailure());
    }

    @Test
    public void logoutSuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        userController.register(guestID, "yaakov", "lol");
        String yaakovID = userController.login(guestID, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        Assert.assertFalse(yaakov.logout().isFailure());
    }

    @Test
    public void logoutFailureNotRegistered() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        User guest = userController.getConnectedUsers().get(guestID);

        Assert.assertTrue(guest.logout().isFailure());
    }

    @Test
    public void openStoreSuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        userController.register(guestID, "yaakov", "lol");
        String yaakovID = userController.login(guestID, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        Assert.assertFalse(yaakov.openStore("yaakov's cool store").isFailure());
    }

    @Test
    public void openStoreFailureNotRegistered() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        User guest = userController.getConnectedUsers().get(guestID);

        Assert.assertTrue(guest.openStore("guest's cool store").isFailure());
    }

    @Test
    public void getPurchaseHistoryContentsSuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        userController.register(guestID, "yaakov", "lol");
        String yaakovID = userController.login(guestID, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        Assert.assertFalse(yaakov.getPurchaseHistoryContents().isFailure());
    }

    @Test
    public void getPurchaseHistoryContentsFailureNotRegistered() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        User guest = userController.getConnectedUsers().get(guestID);

        Assert.assertTrue(guest.getPurchaseHistoryContents().isFailure());
    }

    @Test
    public void receiveStorePurchaseHistorySuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        User shaked = userController.getConnectedUsers().get("shaked"); // prebuilt admin

        String guest2 = userController.addGuest().getResult();
        userController.register(guest2, "almog", "lol");
        userController.login(guest2, "almog", "lol");
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        Assert.assertFalse(shaked.getStorePurchaseHistory(store2ID).isFailure());
    }

    @Test
    public void receiveStorePurchaseHistoryFailureNotPermitted() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        userController.register(guestID, "yaakov", "lol");
        String yaakovID = userController.login(guestID, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        String guest2 = userController.addGuest().getResult();
        userController.register(guest2, "almog", "lol");
        userController.login(guest2, "almog", "lol");
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        Assert.assertTrue(yaakov.getStorePurchaseHistory(store2ID).isFailure());
    }

    @Test
    public void receiveUserPurchaseHistorySuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        User shaked = userController.getConnectedUsers().get("shaked"); // prebuilt admin

        Assert.assertFalse(shaked.getUserPurchaseHistory("almog").isFailure());
    }

    @Test
    public void receiveUserPurchaseHistoryFailureNotPermitted() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        userController.register(guestID, "yaakov", "lol");
        String yaakovID = userController.login(guestID, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        Assert.assertTrue(yaakov.getUserPurchaseHistory("almog").isFailure());
    }

    @Test
    public void receiveWorkerInfoSuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        userController.register(guestID, "almog", "lol");
        String almogID = userController.login(guestID, "almog", "lol").getResult();
        User almog = userController.getConnectedUsers().get(almogID);
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        Assert.assertFalse(almog.getStoreWorkersDetails(store2ID).isFailure());
    }

    @Test
    public void receiveWorkerInfoFailureNotPermitted() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        userController.register(guestID, "yaakov", "lol");
        String yaakovID = userController.login(guestID, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        String guest2 = userController.addGuest().getResult();
        userController.register(guest2, "almog", "lol");
        userController.login(guest2, "almog", "lol");
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        Assert.assertTrue(yaakov.getStoreWorkersDetails(store2ID).isFailure());
    }

    @Test
    public void receiveStoreHistorySuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        userController.register(guestID, "almog", "lol");
        String almogID = userController.login(guestID, "almog", "lol").getResult();
        User almog = userController.getConnectedUsers().get(almogID);
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        Assert.assertFalse(almog.getPurchaseDetails(store2ID).isFailure());
    }

    @Test
    public void receiveStoreHistoryFailureNotPermitted() {
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        userController.register(guestID, "yaakov", "lol");
        String yaakovID = userController.login(guestID, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        String guest2 = userController.addGuest().getResult();
        userController.register(guest2, "almog", "lol");
        userController.login(guest2, "almog", "lol");
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        Assert.assertTrue(yaakov.getPurchaseDetails(store2ID).isFailure());
    }

    @Test
    public void concurrencyAddAppointmentTest() throws InterruptedException{
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        userController.register(guestID, "yaakov", "lol");
        String yaakovID = userController.login(guestID, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        AtomicInteger id = new AtomicInteger(1000);
        int numberOfThreads = 100;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                int currentId = id.getAndIncrement();
                if(currentId % 2 == 0){
                    yaakov.addStoresOwned(currentId);
                }
                else{
                    yaakov.addStoresManaged(currentId, new Vector<>());
                }
                latch.countDown();
            });
        }

        latch.await();
        // Check that every appointment was added
        for(int storeId = 1000; storeId < 1100; storeId++){
            if(storeId % 2 == 0) {
                Assert.assertTrue(yaakov.isOwner(storeId));
            }
            else{
                Assert.assertTrue(yaakov.isManager(storeId));
            }
        }
        service.shutdownNow();
    }

    @Test
    public void concurrencyRemoveAppointmentTest() throws InterruptedException{
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        userController.register(guestID, "yaakov", "lol");
        String yaakovID = userController.login(guestID, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        AtomicInteger id = new AtomicInteger(3000);
        int numberOfThreads = 100;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for(int storeId = 3000; storeId < 3100; storeId++){
            if(storeId % 2 == 0){
                yaakov.addStoresOwned(storeId);
            }
            else{
                yaakov.addStoresManaged(storeId, new Vector<>());
            }
        }

        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                int currentId = id.getAndIncrement();
                yaakov.removeRole(currentId);
                latch.countDown();
            });
        }

        latch.await();
        // Check that every appointment was removed
        for(int storeId = 3000; storeId < 3100; storeId++){
            if(storeId % 2 == 0) {
                Assert.assertFalse(yaakov.isOwner(storeId));
            }
            else{
                Assert.assertFalse(yaakov.isManager(storeId));
            }
        }
        service.shutdownNow();
    }

    @Test
    public void concurrencyAddPermissionTest() throws InterruptedException{
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        userController.register(guestID, "yaakov", "lol");
        String yaakovID = userController.login(guestID, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        AtomicInteger id = new AtomicInteger(2000);
        int numberOfThreads = 100;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for(int storeId = 2000; storeId < 2100; storeId++){
            yaakov.addStoresManaged(storeId, new Vector<>());
        }

        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                int currentId = id.getAndIncrement();
                yaakov.addSelfPermission(currentId, PermissionsEnum.ADD_PRODUCT_TO_STORE);
                latch.countDown();
            });
        }

        latch.await();
        // Check that every permission was added
        for(int storeId = 2000; storeId < 2100; storeId++){
            Assert.assertTrue(yaakov.getStoresManaged().get(storeId).contains(PermissionsEnum.ADD_PRODUCT_TO_STORE));
        }
        service.shutdownNow();
    }

    @Test
    public void concurrencyRemovePermissionTest() throws InterruptedException{
        UserController userController = UserController.getInstance();
        userController.adminBoot("shaked", "jacob");
        String guestID = userController.addGuest().getResult();
        userController.register(guestID, "yaakov", "lol");
        String yaakovID = userController.login(guestID, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        AtomicInteger id = new AtomicInteger(4000);
        int numberOfThreads = 100;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for(int storeId = 4000; storeId < 4100; storeId++){
            yaakov.addStoresManaged(storeId, new Vector<>());
            yaakov.addSelfPermission(storeId, PermissionsEnum.ADD_PRODUCT_TO_STORE);
        }

        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service.execute(() -> {
                int currentId = id.getAndIncrement();
                yaakov.removeSelfPermission(currentId, PermissionsEnum.ADD_PRODUCT_TO_STORE);
                latch.countDown();
            });
        }

        latch.await();
        // Check that every permission was removed
        for(int storeId = 4000; storeId < 4100; storeId++){
            Assert.assertFalse(yaakov.getStoresManaged().get(storeId).contains(PermissionsEnum.ADD_PRODUCT_TO_STORE));
        }
        service.shutdownNow();
    }
}
