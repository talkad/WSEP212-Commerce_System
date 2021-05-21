package TestComponent.IntegrationTestings;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.*;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import Server.Domain.UserManager.PermissionsEnum;
import Server.Domain.UserManager.User;
import Server.Domain.UserManager.UserController;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class UserIntegrationTests {

    @Test
    public void addProductReviewSuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();
        String guest3 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        String almogID = userController.login(guest3, "almog", "lol2").getResult();
        User almog = userController.getConnectedUsers().get(almogID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        List<String> categories2 = new LinkedList<>();
        categories2.add("food");
        List<String> keyword2 = new LinkedList<>();
        keyword2.add("meat");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);
        almog.addProductsToStore(new ProductClientDTO("beef", store2ID, 5, categories2, keyword2), 5);

        Response<List<ProductClientDTO>> beefSearchResult = StoreController.getInstance().searchByProductName("beef");
        ProductClientDTO beef = beefSearchResult.getResult().get(0);

        userController.addToCart(almog.getName(), beef.getStoreID(), beef.getProductID());
        userController.addToCart(almog.getName(), beef.getStoreID(), beef.getProductID());
        userController.addToCart(almog.getName(), beef.getStoreID(), beef.getProductID());

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        Assert.assertFalse(userController.purchase(almog.getName(), paymentDetails, supplyDetails).isFailure());
        Assert.assertFalse(almog.addProductReview(beef.getStoreID(), beef.getProductID(), "good product").isFailure());
    }

    @Test
    public void addProductReviewFailureDidNotPurchaseProduct() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();

        User guest = userController.getConnectedUsers().get(guestID);

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);

        Response<List<ProductClientDTO>> eggsSearchResult = StoreController.getInstance().searchByProductName("Medium eggs");
        ProductClientDTO eggs = eggsSearchResult.getResult().get(0);

        Assert.assertTrue(guest.addProductReview(store1ID, eggs.getProductID(), "good product").isFailure());
    }

    @Test
    public void addProductsToStoreSuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);

        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");

        Assert.assertFalse(yaakov.addProductsToStore(new ProductClientDTO("XL eggs", store1ID, 5, categories, keyword), 5).isFailure());
    }

    @Test
    public void addProductsToStoreFailureNotPermitted() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();
        String guest3 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        String almogID = userController.login(guest3, "almog", "lol2").getResult();
        User almog = userController.getConnectedUsers().get(almogID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        List<String> categories2 = new LinkedList<>();
        categories2.add("food");
        List<String> keyword2 = new LinkedList<>();
        keyword2.add("meat");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);
        almog.addProductsToStore(new ProductClientDTO("beef", store2ID, 5, categories2, keyword2), 5);

        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");

        Assert.assertTrue(yaakov.addProductsToStore(new ProductClientDTO("steak", store2ID, 5, categories, keyword), 5).isFailure());
    }

    @Test
    public void removeProductsFromStoreSuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);

        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");

        yaakov.addProductsToStore(new ProductClientDTO("XXXL eggs", store1ID, 10, categories, keyword), 5);
        Response<List<ProductClientDTO>> result = StoreController.getInstance().searchByProductName("XXXL eggs");
        ProductClientDTO productDTO = result.getResult().get(0);
        Assert.assertFalse(yaakov.removeProductsFromStore(store1ID, productDTO.getProductID(), 5).isFailure());
    }

    @Test
    public void removeProductsFromStoreFailureNotPermitted() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();
        String guest3 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        String almogID = userController.login(guest3, "almog", "lol2").getResult();
        User almog = userController.getConnectedUsers().get(almogID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        List<String> categories2 = new LinkedList<>();
        categories2.add("food");
        List<String> keyword2 = new LinkedList<>();
        keyword2.add("meat");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);
        almog.addProductsToStore(new ProductClientDTO("beef", store2ID, 5, categories2, keyword2), 5);

        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");

        yaakov.addProductsToStore(new ProductClientDTO("XXL eggs", store1ID, 10, categories, keyword), 5);
        Response<List<ProductClientDTO>> result = StoreController.getInstance().searchByProductName("XXL eggs");
        ProductClientDTO productDTO = result.getResult().get(0);

        Assert.assertTrue(almog.removeProductsFromStore(store1ID, productDTO.getProductID(), 5).isFailure());
    }

    @Test
    public void editProductSuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest3 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        String almogID = userController.login(guest3, "almog", "lol2").getResult();
        User almog = userController.getConnectedUsers().get(almogID);

        int store2ID = userController.openStore("almog", "meatStore").getResult();


        List<String> categories2 = new LinkedList<>();
        categories2.add("food");
        List<String> keyword2 = new LinkedList<>();
        keyword2.add("meat");

        almog.addProductsToStore(new ProductClientDTO("beef", store2ID, 5, categories2, keyword2), 5);


        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");

        almog.addProductsToStore(new ProductClientDTO("chicken", store2ID, 10, categories, keyword), 5);
        Response<List<ProductClientDTO>> result = StoreController.getInstance().searchByProductName("chicken");
        ProductClientDTO productDTO = result.getResult().get(0);

        Assert.assertFalse(almog.updateProductInfo(store2ID, productDTO.getProductID(), 15, "big chicken").isFailure());
    }

    @Test
    public void editProductFailureNotPermitted() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();
        String guest3 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        String almogID = userController.login(guest3, "almog", "lol2").getResult();
        User almog = userController.getConnectedUsers().get(almogID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        List<String> categories2 = new LinkedList<>();
        categories2.add("food");
        List<String> keyword2 = new LinkedList<>();
        keyword2.add("meat");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);
        almog.addProductsToStore(new ProductClientDTO("beef", store2ID, 5, categories2, keyword2), 5);

        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");

        almog.addProductsToStore(new ProductClientDTO("chicken", store2ID, 10, categories, keyword), 5);
        Response<List<ProductClientDTO>> result = StoreController.getInstance().searchByProductName("chicken");
        ProductClientDTO productDTO = result.getResult().get(0);

        Assert.assertTrue(yaakov.updateProductInfo(store2ID, productDTO.getProductID(), 15, "big chicken").isFailure());
    }

    @Test
    public void appointOwnerSuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        User shaked = userController.getConnectedUsers().get("shaked"); // prebuilt admin

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);

        Assert.assertFalse(shaked.isOwner(store1ID));
        Assert.assertFalse(yaakov.appointOwner("shaked", store1ID).isFailure());
        Assert.assertTrue(shaked.isOwner(store1ID));
    }

    @Test
    public void appointOwnerFailureNotPermitted() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();
        String guest3 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        User shaked = userController.getConnectedUsers().get("shaked"); // prebuilt admin

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        String almogID = userController.login(guest3, "almog", "lol2").getResult();
        User almog = userController.getConnectedUsers().get(almogID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        List<String> categories2 = new LinkedList<>();
        categories2.add("food");
        List<String> keyword2 = new LinkedList<>();
        keyword2.add("meat");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);
        almog.addProductsToStore(new ProductClientDTO("beef", store2ID, 5, categories2, keyword2), 5);

        Assert.assertFalse(shaked.isOwner(store1ID));
        Assert.assertTrue(almog.appointOwner("shaked", store1ID).isFailure());
        Assert.assertFalse(shaked.isOwner(store1ID));
    }

    @Test
    public void removeOwnerAppointmentSuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();
        String guest3 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        User shaked = userController.getConnectedUsers().get("shaked"); // prebuilt admin

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        String almogID = userController.login(guest3, "almog", "lol2").getResult();
        User almog = userController.getConnectedUsers().get(almogID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        List<String> categories2 = new LinkedList<>();
        categories2.add("food");
        List<String> keyword2 = new LinkedList<>();
        keyword2.add("meat");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);
        almog.addProductsToStore(new ProductClientDTO("beef", store2ID, 5, categories2, keyword2), 5);

        yaakov.appointOwner("shaked", store1ID);
        Assert.assertTrue(shaked.isOwner(store1ID));
        Assert.assertFalse(yaakov.removeAppointment("shaked", store1ID).isFailure());
    }

    @Test
    public void removeOwnerAppointmentFailureNotPermitted() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();
        String guest3 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        User shaked = userController.getConnectedUsers().get("shaked"); // prebuilt admin

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        String almogID = userController.login(guest3, "almog", "lol2").getResult();
        User almog = userController.getConnectedUsers().get(almogID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        List<String> categories2 = new LinkedList<>();
        categories2.add("food");
        List<String> keyword2 = new LinkedList<>();
        keyword2.add("meat");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);
        almog.addProductsToStore(new ProductClientDTO("beef", store2ID, 5, categories2, keyword2), 5);

        yaakov.appointOwner("shaked", store1ID);
        Assert.assertTrue(shaked.isOwner(store1ID));
        Assert.assertTrue(almog.removeAppointment("shaked", store1ID).isFailure());
        Assert.assertTrue(shaked.isOwner(store1ID));
    }

    @Test
    public void appointManagerSuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();
        String guest3 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        User shaked = userController.getConnectedUsers().get("shaked"); // prebuilt admin

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        String almogID = userController.login(guest3, "almog", "lol2").getResult();
        User almog = userController.getConnectedUsers().get(almogID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        List<String> categories2 = new LinkedList<>();
        categories2.add("food");
        List<String> keyword2 = new LinkedList<>();
        keyword2.add("meat");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);
        almog.addProductsToStore(new ProductClientDTO("beef", store2ID, 5, categories2, keyword2), 5);

        Assert.assertFalse(shaked.isManager(store1ID));
        Assert.assertFalse(yaakov.appointManager("shaked", store1ID).isFailure());
        Assert.assertTrue(shaked.isManager(store1ID));
    }

    @Test
    public void appointManagerFailureNotPermitted() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();
        String guest3 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        User shaked = userController.getConnectedUsers().get("shaked"); // prebuilt admin

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        String almogID = userController.login(guest3, "almog", "lol2").getResult();
        User almog = userController.getConnectedUsers().get(almogID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        List<String> categories2 = new LinkedList<>();
        categories2.add("food");
        List<String> keyword2 = new LinkedList<>();
        keyword2.add("meat");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);
        almog.addProductsToStore(new ProductClientDTO("beef", store2ID, 5, categories2, keyword2), 5);

        Assert.assertFalse(shaked.isManager(store1ID));
        Assert.assertTrue(almog.appointManager("shaked", store1ID).isFailure());
        Assert.assertFalse(shaked.isManager(store1ID));
    }

    @Test
    public void removeManagerAppointmentSuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();
        String guest3 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        User shaked = userController.getConnectedUsers().get("shaked"); // prebuilt admin

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        String almogID = userController.login(guest3, "almog", "lol2").getResult();
        User almog = userController.getConnectedUsers().get(almogID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        List<String> categories2 = new LinkedList<>();
        categories2.add("food");
        List<String> keyword2 = new LinkedList<>();
        keyword2.add("meat");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);
        almog.addProductsToStore(new ProductClientDTO("beef", store2ID, 5, categories2, keyword2), 5);

        yaakov.appointManager("shaked", store1ID);
        Assert.assertTrue(shaked.isManager(store1ID));
        Assert.assertFalse(yaakov.removeAppointment("shaked", store1ID).isFailure());
    }

    @Test
    public void removeManagerAppointmentFailureNotPermitted() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();
        String guest3 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        User shaked = userController.getConnectedUsers().get("shaked"); // prebuilt admin

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        String almogID = userController.login(guest3, "almog", "lol2").getResult();
        User almog = userController.getConnectedUsers().get(almogID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        List<String> categories2 = new LinkedList<>();
        categories2.add("food");
        List<String> keyword2 = new LinkedList<>();
        keyword2.add("meat");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);
        almog.addProductsToStore(new ProductClientDTO("beef", store2ID, 5, categories2, keyword2), 5);

        yaakov.appointManager("shaked", store1ID);
        Assert.assertTrue(shaked.isManager(store1ID));
        Assert.assertTrue(almog.removeAppointment("shaked", store1ID).isFailure());
        Assert.assertTrue(shaked.isManager(store1ID));
    }

    @Test
    public void editManagerPermissionSuccess() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();
        String guest3 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        User shaked = userController.getConnectedUsers().get("shaked"); // prebuilt admin

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        String almogID = userController.login(guest3, "almog", "lol2").getResult();
        User almog = userController.getConnectedUsers().get(almogID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        List<String> categories2 = new LinkedList<>();
        categories2.add("food");
        List<String> keyword2 = new LinkedList<>();
        keyword2.add("meat");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);
        almog.addProductsToStore(new ProductClientDTO("beef", store2ID, 5, categories2, keyword2), 5);

        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");

        yaakov.appointManager("shaked", store1ID);

        Assert.assertTrue(shaked.addProductsToStore(new ProductClientDTO("egg carton", store1ID, 10, categories, keyword), 5).isFailure());
        Assert.assertFalse(yaakov.addPermission(store1ID, "shaked", PermissionsEnum.ADD_PRODUCT_TO_STORE).isFailure());
        Assert.assertFalse(shaked.addProductsToStore(new ProductClientDTO("egg carton", store1ID, 10, categories, keyword), 5).isFailure());
    }

    @Test
    public void editManagerPermissionFailureNotPermitted() {
        UserController userController = UserController.getInstance();
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();
        String guest3 = userController.addGuest().getResult();

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        User shaked = userController.getConnectedUsers().get("shaked"); // prebuilt admin

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        User yaakov = userController.getConnectedUsers().get(yaakovID);

        String almogID = userController.login(guest3, "almog", "lol2").getResult();
        User almog = userController.getConnectedUsers().get(almogID);

        int store1ID = userController.openStore("yaakov", "eggStore").getResult();
        int store2ID = userController.openStore("almog", "meatStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");

        List<String> categories2 = new LinkedList<>();
        categories2.add("food");
        List<String> keyword2 = new LinkedList<>();
        keyword2.add("meat");

        yaakov.addProductsToStore(new ProductClientDTO("Medium eggs", store1ID, 2, categories1, keyword1), 5);
        almog.addProductsToStore(new ProductClientDTO("beef", store2ID, 5, categories2, keyword2), 5);

        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");

        yaakov.appointManager("shaked", store1ID);
        Assert.assertTrue(shaked.addProductsToStore(new ProductClientDTO("egg carton", store1ID, 10, categories, keyword), 5).isFailure());
        Assert.assertTrue(almog.addPermission(store1ID, "shaked", PermissionsEnum.ADD_PRODUCT_TO_STORE).isFailure());
        Assert.assertTrue(shaked.addProductsToStore(new ProductClientDTO("egg carton", store1ID, 10, categories, keyword), 5).isFailure());
    }
}
