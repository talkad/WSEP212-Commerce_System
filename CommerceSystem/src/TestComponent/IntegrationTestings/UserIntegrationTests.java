package TestComponent.IntegrationTestings;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.Review;
import Server.Domain.ShoppingManager.StoreController;
import Server.Domain.UserManager.Permissions;
import Server.Domain.UserManager.User;
import Server.Domain.UserManager.UserController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class UserIntegrationTests {

    private User guest;
    private User shaked;
    private User yaakov;
    private User almog;
    private UserController userController = UserController.getInstance();
    private int store1ID;
    private int store2ID;
    private ProductDTO beef;
    private ProductDTO eggs;


    @Before
    public void setUp(){
        userController.adminBoot();
        String guestID = userController.addGuest().getResult();
        String guest1 = userController.addGuest().getResult();
        String guest2 = userController.addGuest().getResult();
        String guest3 = userController.addGuest().getResult();

        guest = userController.getConnectedUsers().get(guestID);

        userController.register(guestID, "yaakov", "lol");
        userController.register(guestID, "almog", "lol2");

        //String shakedID = userController.login(guest1, "shaked", "jacob").getResult();
        shaked = userController.getConnectedUsers().get("shaked"); // prebuilt admin

        String yaakovID = userController.login(guest2, "yaakov", "lol").getResult();
        yaakov = userController.getConnectedUsers().get(yaakovID);

        String almogID = userController.login(guest3, "almog", "lol2").getResult();
        almog = userController.getConnectedUsers().get(almogID);

        store1ID = userController.openStore("yaakov", "eggStore").getResult();
        store2ID = userController.openStore("almog", "meatStore").getResult();

        List<String> categories1 = new LinkedList<>();
        categories1.add("food");
        List<String> keyword1 = new LinkedList<>();
        keyword1.add("egg");
        Collection<Review> review = new LinkedList<>();

        List<String> categories2 = new LinkedList<>();
        categories2.add("food");
        List<String> keyword2 = new LinkedList<>();
        keyword2.add("meat");

        yaakov.addProductsToStore(new ProductDTO("Medium eggs", store1ID, 2, categories1, keyword1, review), 5);
        almog.addProductsToStore(new ProductDTO("beef", store2ID, 5, categories2, keyword2, review), 5);

        Response<List<ProductDTO>> beefSearchResult = StoreController.getInstance().searchByProductName("beef");
        beef = beefSearchResult.getResult().get(0);

        Response<List<ProductDTO>> eggsSearchResult = StoreController.getInstance().searchByProductName("Medium eggs");
        eggs = eggsSearchResult.getResult().get(0);
    }

    @Test
    public void addProductReviewSuccess() {
        userController.addToCart(almog.getName(), beef.getStoreID(), beef.getProductID());
        Assert.assertFalse(userController.purchase(almog.getName(), "45801234", "Israel").isFailure());
        Assert.assertFalse(almog.addProductReview(beef.getStoreID(), beef.getProductID(), "good product").isFailure());
    }

    @Test
    public void addProductReviewFailureDidNotPurchaseProduct() {
        Assert.assertTrue(guest.addProductReview(store1ID, eggs.getProductID(), "good product").isFailure());
    }

    @Test
    public void addProductsToStoreSuccess() {
        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");
        Collection<Review> review = new LinkedList<>();

        Assert.assertFalse(yaakov.addProductsToStore(new ProductDTO("XL eggs", store1ID, 5, categories, keyword, review), 5).isFailure());
    }

    @Test
    public void addProductsToStoreFailureNotPermitted() {
        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");
        Collection<Review> review = new LinkedList<>();

        Assert.assertTrue(yaakov.addProductsToStore(new ProductDTO("steak", store2ID, 5, categories, keyword, review), 5).isFailure());
    }

    @Test
    public void removeProductsFromStoreSuccess() {
        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");
        Collection<Review> review = new LinkedList<>();
        yaakov.addProductsToStore(new ProductDTO("XXXL eggs", store1ID, 10, categories, keyword, review), 5);
        Response<List<ProductDTO>> result = StoreController.getInstance().searchByProductName("XXXL eggs");
        ProductDTO productDTO = result.getResult().get(0);
        Assert.assertFalse(yaakov.removeProductsFromStore(store1ID, productDTO.getProductID(), 5).isFailure());
    }

    @Test
    public void removeProductsFromStoreFailureNotPermitted() {
        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");
        Collection<Review> review = new LinkedList<>();
        yaakov.addProductsToStore(new ProductDTO("XXL eggs", store1ID, 10, categories, keyword, review), 5);
        Response<List<ProductDTO>> result = StoreController.getInstance().searchByProductName("XXL eggs");
        ProductDTO productDTO = result.getResult().get(0);

        Assert.assertTrue(almog.removeProductsFromStore(store1ID, productDTO.getProductID(), 5).isFailure());
    }

    @Test
    public void editProductSuccess() {
        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");
        Collection<Review> review = new LinkedList<>();
        almog.addProductsToStore(new ProductDTO("chicken", store2ID, 10, categories, keyword, review), 5);
        Response<List<ProductDTO>> result = StoreController.getInstance().searchByProductName("chicken");
        ProductDTO productDTO = result.getResult().get(0);

        Assert.assertFalse(almog.updateProductInfo(store2ID, productDTO.getProductID(), 15, "big chicken").isFailure());
    }

    @Test
    public void editProductFailureNotPermitted() {
        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");
        Collection<Review> review = new LinkedList<>();
        almog.addProductsToStore(new ProductDTO("chicken", store2ID, 10, categories, keyword, review), 5);
        Response<List<ProductDTO>> result = StoreController.getInstance().searchByProductName("chicken");
        ProductDTO productDTO = result.getResult().get(0);

        Assert.assertTrue(yaakov.updateProductInfo(store2ID, productDTO.getProductID(), 15, "big chicken").isFailure());
    }

    @Test
    public void appointOwnerSuccess() {
        Assert.assertFalse(shaked.isOwner(store1ID));
        Assert.assertFalse(yaakov.appointOwner("shaked", store1ID).isFailure());
        Assert.assertTrue(shaked.isOwner(store1ID));
    }

    @Test
    public void appointOwnerFailureNotPermitted() {
        Assert.assertFalse(shaked.isOwner(store1ID));
        Assert.assertTrue(almog.appointOwner("shaked", store1ID).isFailure());
        Assert.assertFalse(shaked.isOwner(store1ID));
    }

    @Test
    public void removeOwnerAppointmentSuccess() {
        yaakov.appointOwner("shaked", store1ID);
        Assert.assertTrue(shaked.isOwner(store1ID));
        Assert.assertFalse(yaakov.removeAppointment("shaked", store1ID).isFailure());
    }

    @Test
    public void removeOwnerAppointmentFailureNotPermitted() {
        yaakov.appointOwner("shaked", store1ID);
        Assert.assertTrue(shaked.isOwner(store1ID));
        Assert.assertTrue(almog.removeAppointment("shaked", store1ID).isFailure());
        Assert.assertTrue(shaked.isOwner(store1ID));
    }

    @Test
    public void appointManagerSuccess() {
        Assert.assertFalse(shaked.isManager(store1ID));
        Assert.assertFalse(yaakov.appointManager("shaked", store1ID).isFailure());
        Assert.assertTrue(shaked.isManager(store1ID));
    }

    @Test
    public void appointManagerFailureNotPermitted() {
        Assert.assertFalse(shaked.isManager(store1ID));
        Assert.assertTrue(almog.appointManager("shaked", store1ID).isFailure());
        Assert.assertFalse(shaked.isManager(store1ID));
    }

    @Test
    public void removeManagerAppointmentSuccess() {
        yaakov.appointManager("shaked", store1ID);
        Assert.assertTrue(shaked.isManager(store1ID));
        Assert.assertFalse(yaakov.removeAppointment("shaked", store1ID).isFailure());
    }

    @Test
    public void removeManagerAppointmentFailureNotPermitted() {
        yaakov.appointManager("shaked", store1ID);
        Assert.assertTrue(shaked.isManager(store1ID));
        Assert.assertTrue(almog.removeAppointment("shaked", store1ID).isFailure());
        Assert.assertTrue(shaked.isManager(store1ID));
    }

    @Test
    public void editManagerPermissionSuccess() {
        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");
        Collection<Review> review = new LinkedList<>();

        yaakov.appointManager("shaked", store1ID);

        Assert.assertTrue(shaked.addProductsToStore(new ProductDTO("egg carton", store1ID, 10, categories, keyword, review), 5).isFailure());
        Assert.assertFalse(yaakov.addPermission(store1ID, "shaked", Permissions.ADD_PRODUCT_TO_STORE).isFailure());
        Assert.assertFalse(shaked.addProductsToStore(new ProductDTO("egg carton", store1ID, 10, categories, keyword, review), 5).isFailure());
    }

    @Test
    public void editManagerPermissionFailureNotPermitted() {
        List<String> categories = new LinkedList<>();
        categories.add("food");
        List<String> keyword = new LinkedList<>();
        keyword.add("bread");
        Collection<Review> review = new LinkedList<>();

        yaakov.appointManager("shaked", store1ID);
        Assert.assertTrue(shaked.addProductsToStore(new ProductDTO("egg carton", store1ID, 10, categories, keyword, review), 5).isFailure());
        Assert.assertTrue(almog.addPermission(store1ID, "shaked", Permissions.ADD_PRODUCT_TO_STORE).isFailure());
        Assert.assertTrue(shaked.addProductsToStore(new ProductDTO("egg carton", store1ID, 10, categories, keyword, review), 5).isFailure());
    }
}
