package TestComponent.AcceptanceTestings.Tests;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.UserManager.Permissions;
import Server.Domain.UserManager.Publisher;
import Server.Domain.UserManager.PurchaseDTO;
import Server.Domain.UserManager.User;
import TestComponent.AcceptanceTestings.Bridge.ProxyNotifier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * also Store Manager tests
 */
public class StoreOwnerTests extends ProjectAcceptanceTests{

    private int storeID; // the store id of the store we are going to test on

    private static boolean initialized = false;

    @Before
    public void setUp(){
        if(!initialized) {
            super.setUp();

            // pre-registered users in the system
            String guestName = bridge.addGuest().getResult();
            bridge.register(guestName, "aviad", "123456"); // store owner
            bridge.register(guestName, "jacob", "123456");

            // logged in users
            bridge.login(guestName, "aviad", "123456");
            bridge.login(guestName, "jacob", "123456");
            //bridge.login(guestName, "abraham", "123456");
            //bridge.login(guestName, "issac", "123456");

            // opening some stores for later use
            this.storeID = bridge.openStore("aviad", "masmerim behinam").getResult();

            ProductDTO productDTO = new ProductDTO("masmer adom", this.storeID, 20,
                    new LinkedList<String>(Arrays.asList("red", "nail")),
                    new LinkedList<String>(Arrays.asList("masmer")));

            bridge.addProductsToStore("aviad", productDTO, 20);

            productDTO = new ProductDTO("masmer varod", this.storeID, 20,
                    new LinkedList<String>(Arrays.asList("pink", "nail")),
                    new LinkedList<String>(Arrays.asList("masmer")));

            bridge.addProductsToStore("aviad", productDTO, 20);

            initialized = true;
        }
    }

    @Test
    public void addProductToStoreWithPermissionsTest(){ // 4.1.1 good
        // a user with permissions of a store adding a products to the store

        ProductDTO productDTO = new ProductDTO("masmer yarok", this.storeID, 20,
                new LinkedList<String>(Arrays.asList("green", "nail")),
                new LinkedList<String>(Arrays.asList("masmer")));

        Response<Boolean> addResponse = bridge.addProductsToStore("aviad", productDTO, 20);
        Assert.assertTrue(addResponse.getResult());

        // looking the product up in the store
        Response<List<ProductDTO>> searchResponse = bridge.searchByProductName("masmer yarok");

        boolean exists = false;
        for(ProductDTO product: searchResponse.getResult()){
            if(product.getStoreID() == this.storeID){
                exists = true;
            }
        }

        Assert.assertTrue(exists);
    }

    @Test
    public void addProductToStoreWithoutPermissionsTest() { // 4.1.1 bad
        // now a user which doesn't have permissions will try to add a product
        ProductDTO productDTO = new ProductDTO("masmer shahor", this.storeID, 20,
                new LinkedList<String>(Arrays.asList("black", "nail")),
                new LinkedList<String>(Arrays.asList("masmer")));
        Response<Boolean> addResponse = bridge.addProductsToStore("jacob", productDTO, 20);
        Assert.assertTrue(addResponse.isFailure());

        // making sure it wasn't added
        Response<List<ProductDTO>> searchResponse = bridge.searchByProductName("masmer shahor");

        boolean exists = false;
        for(ProductDTO product: searchResponse.getResult()){
            if(product.getStoreID() == this.storeID){
                exists = true;
            }
        }

        Assert.assertFalse(exists);
    }

    @Test
    public void addProductToStoreInvalidProductAmountTest() { // 4.1.1 bad
        // now a user which doesn't have permissions will try to add a product
        ProductDTO productDTO = new ProductDTO("masmer masmeri", this.storeID, 20,
                new LinkedList<String>(Arrays.asList("nail")),
                new LinkedList<String>(Arrays.asList("masmer")));
        Response<Boolean> addResponse = bridge.addProductsToStore("aviad", productDTO, -20);
        Assert.assertTrue(addResponse.isFailure());

        // making sure it wasn't added
        Response<List<ProductDTO>> searchResponse = bridge.searchByProductName("masmer shahor");

        boolean exists = false;
        for(ProductDTO product: searchResponse.getResult()){
            if(product.getStoreID() == this.storeID){
                exists = true;
            }
        }

        Assert.assertFalse(exists);
    }

    @Test
    public void removeProductFromStoreWithPermissionsTest(){ // 4.1.2 good
        // a permitted user trying to remove a non-existing product
        Response<Boolean> deletionResponse = bridge.removeProductsFromStore("aviad", this.storeID,
                -1, 1);
        Assert.assertFalse(deletionResponse.getResult());

        // a permitted user trying to remove an existing product
        Response<List<ProductDTO>> searchResponse = bridge.searchByProductName("masmer adom");
        int productID = searchResponse.getResult().get(0).getProductID();
        deletionResponse = bridge.removeProductsFromStore("aviad", this.storeID, productID, 20);
        Assert.assertTrue(deletionResponse.getResult());

        // making sure the product is gone
        searchResponse = bridge.searchByProductName("masmer adom");

        boolean exists = false;
        for(ProductDTO product: searchResponse.getResult()){
            if(product.getStoreID() == this.storeID){
                exists = true;
            }
        }

        Assert.assertFalse(exists);
    }

    @Test
    public void removeProductFromStoreWithoutPermissionsTest() { // 4.1.2 bad
        // now a user without permissions will try to remove an existing product
        Response<List<ProductDTO>> searchResponse = bridge.searchByProductName("masmer varod");
        int productID = searchResponse.getResult().get(0).getProductID();
        Response<Boolean> deletionResponse = bridge.removeProductsFromStore("jacob", this.storeID, productID, 20);
        Assert.assertTrue(deletionResponse.isFailure());

        // making sure it's not deleted
        searchResponse = bridge.searchByProductName("masmer varod");

        boolean exists = false;
        for(ProductDTO product: searchResponse.getResult()){
            if(product.getStoreID() == this.storeID){
                exists = true;
            }
        }

        Assert.assertTrue(exists);
    }

    @Test
    public void removeProductFromStoreInvalidAmountTest() { // 4.1.2 bad
        // now a user without permissions will try to remove an existing product
        Response<List<ProductDTO>> searchResponse = bridge.searchByProductName("masmer varod");
        int productID = searchResponse.getResult().get(0).getProductID();
        Response<Boolean> deletionResponse = bridge.removeProductsFromStore("aviad", this.storeID, productID, -20);
        Assert.assertTrue(deletionResponse.isFailure());

        // making sure it's not deleted
        searchResponse = bridge.searchByProductName("masmer varod");

        boolean exists = false;
        for(ProductDTO product: searchResponse.getResult()){
            if(product.getStoreID() == this.storeID){
                exists = true;
            }
        }

        Assert.assertTrue(exists);
    }

    @Test
    public void updateProductInfoWithPermissionsTest(){ // 4.1.3 good
        // a user with permissions trying to update
        Response<List<ProductDTO>> searchResponse = bridge.searchByProductName("masmer varod");
        int productID = searchResponse.getResult().get(0).getProductID();
        int newPrice = 100;
        Response<Boolean> updateResponse = bridge.updateProductInfo("aviad", this.storeID,
                productID, newPrice, "masmer varod");
        Assert.assertTrue(updateResponse.getResult());

        // making sure it's changed
        searchResponse = bridge.searchByProductName("masmer varod");

        boolean updated = false;
        for(ProductDTO product: searchResponse.getResult()){
            if(product.getStoreID() == this.storeID){
                if(product.getPrice() == newPrice) {
                    updated = true;
                }
            }
        }

        Assert.assertTrue(updated);
    }

    @Test
    public void updateProductInfoWithoutPermissionsTest() { // 4.1.3 bad
        // now a user without permissions will try to update
        Response<List<ProductDTO>> searchResponse = bridge.searchByProductName("masmer varod");
        int productID = searchResponse.getResult().get(0).getProductID();
        int newerPrice = 200;
        Response<Boolean> updateResponse = bridge.updateProductInfo("jacob", this.storeID,
                productID, newerPrice, "masmer varod");

        // making sure it's not changed
        searchResponse = bridge.searchByProductName("masmer varod");

        boolean updated = false;
        for(ProductDTO product: searchResponse.getResult()){
            if(product.getStoreID() == this.storeID){
                if(product.getPrice() == newerPrice) {
                    updated = true;
                }
            }
        }

        Assert.assertFalse(updated);
    }

    @Test
    public void updateProductInfoInvalidPriceTest() { // 4.1.3 bad
        // now a user without permissions will try to update
        Response<List<ProductDTO>> searchResponse = bridge.searchByProductName("masmer varod");
        int productID = searchResponse.getResult().get(0).getProductID();
        int newerPrice = -200;
        Response<Boolean> updateResponse = bridge.updateProductInfo("aviad", this.storeID,
                productID, newerPrice, "masmer varod");

        // making sure it's not changed
        searchResponse = bridge.searchByProductName("masmer varod");

        boolean updated = false;
        for(ProductDTO product: searchResponse.getResult()){
            if(product.getStoreID() == this.storeID){
                if(product.getPrice() == newerPrice) {
                    updated = true;
                }
            }
        }

        Assert.assertFalse(updated);
    }

    @Test
    public void appointStoreOwnerWithPermissionTest(){ // 4.3.1 good
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "abraham", "123456");
        bridge.register(guestName, "issac", "123456");

        bridge.login(bridge.addGuest().getResult(), "abraham", "123456");
        bridge.login(bridge.addGuest().getResult(), "issac", "123456");

        // owner appoints a new owner
        Response<Boolean> appointResult = bridge.appointStoreOwner("aviad", "issac",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());


        // if the appointment succeed then he should be able to appoint a new owner
        appointResult = bridge.appointStoreOwner("issac", "abraham", this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // now an owner will try to appoint the original store owner. should fail
        appointResult = bridge.appointStoreOwner("abraham", "aviad", this.storeID);
        Assert.assertFalse(appointResult.getResult());
    }

    @Test
    public void appointStoreOwnerWithoutPermissionTest() { // 4.3.1 bad
        String guestName = bridge.addGuest().getResult();
        // now a user without any permissions will try to appoint another user
        bridge.register(guestName, "abraham2", "123456");
        bridge.register(guestName, "issac2", "123456");
        bridge.login(bridge.addGuest().getResult(), "abraham2", "123456");
        bridge.login(bridge.addGuest().getResult(), "issac2", "123456");

        Response<Boolean>appointResult = bridge.appointStoreOwner("abraham2", "issac2", this.storeID);
        Assert.assertFalse(appointResult.getResult());
    }


    @Test
    public void removeOwnerAppointmentWithPermissionsTest(){ // 4.4.1 good
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "aaa", "123456");
        bridge.register(guestName, "bbb", "123456");
        bridge.register(guestName, "ccc", "123456");

        bridge.login(bridge.addGuest().getResult(), "aaa", "123456");
        bridge.login(bridge.addGuest().getResult(), "bbb", "123456");
        bridge.login(bridge.addGuest().getResult(), "ccc", "123456");

        // owner appoints a new owner
        Response<Boolean> appointResult = bridge.appointStoreOwner("aviad", "aaa",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // the new appointee tries to appoint a new owner
        appointResult = bridge.appointStoreOwner("aaa", "bbb",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // the new appointee will try to remove his appointer
        Response<Boolean> removeResult = bridge.removeOwnerAppointment("aaa", "aviad",
                this.storeID);
        Assert.assertFalse(removeResult.getResult());

        // owner tries to remove him
        removeResult = bridge.removeOwnerAppointment("aviad", "aaa",
                this.storeID);
        Assert.assertTrue(removeResult.getResult());

        // the removed user will try to appoint another user. should fail
        appointResult = bridge.appointStoreOwner("aaa", "ccc",
                this.storeID);
        Assert.assertFalse(appointResult.getResult());

        // the second degree appointee shouldn't be able to appoint either.
        appointResult = bridge.appointStoreOwner("bbb", "ccc",
                this.storeID);
        Assert.assertFalse(appointResult.getResult());
    }

    @Test
    public void removeOwnerAppointmentWithoutPermissionsTest() { // 4.4.1 bad
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "rrr", "123456");

        bridge.login(bridge.addGuest().getResult(), "rrr", "123456");

        // a user without any permissions tried to remove the ownership of an owner
        Response<Boolean> removeResult = bridge.removeOwnerAppointment("rrr", "aviad",
                this.storeID);
        Assert.assertFalse(removeResult.getResult());
    }

    @Test
    public void appointStoreManagerWithPermissions(){ // 4.5.1 good
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "aa", "123456");
        bridge.register(guestName, "bb", "123456");

        bridge.login(bridge.addGuest().getResult(), "aa", "123456");
        bridge.login(bridge.addGuest().getResult(), "bb", "123456");
        bridge.login(bridge.addGuest().getResult(), "cc", "123456");

        // owner appoints a manager
        Response<Boolean> appointResult = bridge.appointStoreManager("aviad", "aa",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // new appointee should be able to see the workers but not appoint a new owner or manager
        Response<List<User>> workersResult = bridge.getStoreWorkersDetails("aa", this.storeID);
        Assert.assertFalse(workersResult.isFailure());
        Assert.assertFalse(workersResult.getResult().isEmpty());

        appointResult = bridge.appointStoreManager("aa", "bb",
                this.storeID);
        Assert.assertFalse(appointResult.getResult());

        workersResult = bridge.getStoreWorkersDetails("bb", this.storeID);
        Assert.assertTrue(workersResult.isFailure());

        // owner trying to appoint the new manager again. should fail
        appointResult = bridge.appointStoreManager("aviad", "aa",
                this.storeID);
        Assert.assertFalse(appointResult.getResult());
    }

    @Test
    public void appointStoreManagerWithoutPermissions() { // 4.5.1 good
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "rr", "123456");
        bridge.register(guestName, "tt", "123456");

        bridge.login(bridge.addGuest().getResult(), "rr", "123456");
        bridge.login(bridge.addGuest().getResult(), "tt", "123456");

        // a user without permissions appoints a manager
        Response<Boolean> appointResult = bridge.appointStoreManager("rr", "tt",
                this.storeID);
        Assert.assertFalse(appointResult.getResult());

        // new appointee should not be able to see the workers
        Response<List<User>> workersResult = bridge.getStoreWorkersDetails("tt", this.storeID);
        Assert.assertTrue(workersResult.isFailure());
    }

    @Test
    public void addPermissionWithPermissionsTest(){ // 4.6.1 good
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "d", "123456");
        bridge.register(guestName, "e", "123456");
        bridge.register(guestName, "f", "123456");

        bridge.login(bridge.addGuest().getResult(), "d", "123456");
        bridge.login(bridge.addGuest().getResult(), "e", "123456");
        bridge.login(bridge.addGuest().getResult(), "f", "123456");

        // owner will appoint a new manager and each time will give him permission to do something
        Response<Boolean> appointResult = bridge.appointStoreManager("aviad", "e",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // permission to add a product
        Response<Boolean> permissionResult = bridge.addPermission("aviad", this.storeID, "e",
                Permissions.ADD_PRODUCT_TO_STORE);
        Assert.assertTrue(permissionResult.getResult());

        ProductDTO productDTO = new ProductDTO("masmer krem", this.storeID, 20,
                new LinkedList<String>(Arrays.asList("krem", "nail")),
                new LinkedList<String>(Arrays.asList("masmer")));

        Response<Boolean> actionResult = bridge.addProductsToStore("e", productDTO, 20);
        Assert.assertTrue(actionResult.getResult());

        ProductDTO product = bridge.searchByProductName("masmer krem").getResult().get(0);

        // permission to update a product
        permissionResult = bridge.addPermission("aviad", this.storeID, "e",
                Permissions.UPDATE_PRODUCT_PRICE);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = bridge.updateProductInfo("e", this.storeID, product.getProductID(),
                50, "masmer kremi");
        Assert.assertTrue(actionResult.getResult());

        // permission to remove a product
        permissionResult = bridge.addPermission("aviad", this.storeID, "e",
                Permissions.REMOVE_PRODUCT_FROM_STORE);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = bridge.removeProductsFromStore("e", this.storeID, product.getProductID(),
                10);
        Assert.assertTrue(actionResult.getResult());

        // permission to appoint an owner
        permissionResult = bridge.addPermission("aviad", this.storeID, "e",
                Permissions.APPOINT_OWNER);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = bridge.appointStoreOwner("e", "d", this.storeID);
        Assert.assertTrue(actionResult.getResult());

        // permission to remove an owner appointment
        permissionResult = bridge.addPermission("aviad", this.storeID, "e",
                Permissions.REMOVE_OWNER_APPOINTMENT);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = bridge.removeOwnerAppointment("e", "d", this.storeID);
        Assert.assertTrue(actionResult.getResult());

        // permission to appoint a manager
        permissionResult = bridge.addPermission("aviad", this.storeID, "e",
                Permissions.APPOINT_MANAGER);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = bridge.appointStoreManager("e", "f", this.storeID);
        Assert.assertTrue(actionResult.getResult());

        // permission to edit permissions
        permissionResult = bridge.addPermission("aviad", this.storeID, "e",
                Permissions.EDIT_PERMISSION);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = bridge.addPermission("e", this.storeID, "f",
                Permissions.RECEIVE_STORE_HISTORY);
        Assert.assertTrue(actionResult.getResult());

        Response<Collection<PurchaseDTO>> newActionResult = bridge.getPurchaseDetails("f", this.storeID);
        Assert.assertFalse(newActionResult.isFailure());

        //permission to remove a manager appointment
        permissionResult = bridge.addPermission("aviad", this.storeID, "e",
                Permissions.REMOVE_MANAGER_APPOINTMENT);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = bridge.removeManagerAppointment("e", "f", this.storeID);
        Assert.assertTrue(actionResult.getResult());
    }

    @Test
    public void addPermissionWithoutPermissionsTest() { // 4.6.1 bad
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "h", "123456");
        bridge.register(guestName, "j", "123456");


        bridge.login(bridge.addGuest().getResult(), "h", "123456");
        bridge.login(bridge.addGuest().getResult(), "j", "123456");

        // owner will appoint a new manager and another user without permissions will give him permission to view the purchase history. should fail
        Response<Boolean> appointResult = bridge.appointStoreManager("aviad", "h",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        Response<Boolean> actionResult = bridge.addPermission("j", this.storeID, "h",
                Permissions.RECEIVE_STORE_HISTORY);
        Assert.assertFalse(actionResult.getResult());

        // making sure he can't do that
        Response<Collection<PurchaseDTO>> newActionResult = bridge.getPurchaseDetails("h", this.storeID);
        Assert.assertTrue(newActionResult.isFailure());
    }

    @Test
    public void removePermissionWithPermissionTest(){ // 4.6.2 good
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "x", "123456");
        bridge.register(guestName, "y", "123456");
        bridge.register(guestName, "z", "123456");

        bridge.login(bridge.addGuest().getResult(), "x", "123456");
        bridge.login(bridge.addGuest().getResult(), "y", "123456");
        bridge.login(bridge.addGuest().getResult(), "z", "123456");

        // the owner will give a manager all of the available permissions and will remove them one at a time
        Response<Boolean> appointResult = bridge.appointStoreManager("aviad", "x",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());


        // add product permission
        Response<Boolean> permissionResult = bridge.addPermission("aviad", this.storeID, "x",
                Permissions.ADD_PRODUCT_TO_STORE);
        Assert.assertTrue(permissionResult.getResult());

        // remove product permission
        permissionResult = bridge.addPermission("aviad", this.storeID, "x",
                Permissions.REMOVE_PRODUCT_FROM_STORE);
        Assert.assertTrue(permissionResult.getResult());

        // update product permission
        permissionResult = bridge.addPermission("aviad", this.storeID, "x",
                Permissions.UPDATE_PRODUCT_PRICE);
        Assert.assertTrue(permissionResult.getResult());

        // appoint owner permission
        permissionResult = bridge.addPermission("aviad", this.storeID, "x",
                Permissions.APPOINT_OWNER);
        Assert.assertTrue(permissionResult.getResult());

        // remove owner permission
        permissionResult = bridge.addPermission("aviad", this.storeID, "x",
                Permissions.REMOVE_OWNER_APPOINTMENT);
        Assert.assertTrue(permissionResult.getResult());

        // appoint manager permission
        permissionResult = bridge.addPermission("aviad", this.storeID, "x",
                Permissions.APPOINT_MANAGER);
        Assert.assertTrue(permissionResult.getResult());

        // edit permission permission
        permissionResult = bridge.addPermission("aviad", this.storeID, "x",
                Permissions.EDIT_PERMISSION);
        Assert.assertTrue(permissionResult.getResult());

        // remove manager permission
        permissionResult = bridge.addPermission("aviad", this.storeID, "x",
                Permissions.REMOVE_MANAGER_APPOINTMENT);
        Assert.assertTrue(permissionResult.getResult());

        // receive store worker info permission
        permissionResult = bridge.addPermission("aviad", this.storeID, "x", // although it is given to him beforehand
                Permissions.RECEIVE_STORE_WORKER_INFO);
        Assert.assertTrue(permissionResult.isFailure()); // should fail cause he already has it

        // receive store history permission
        permissionResult = bridge.addPermission("aviad", this.storeID, "x",
                Permissions.RECEIVE_STORE_HISTORY);
        Assert.assertTrue(permissionResult.getResult());


        // remove permissions testings

        // add product removed
        permissionResult = bridge.removePermission("aviad", this.storeID, "x",
                Permissions.ADD_PRODUCT_TO_STORE);
        Assert.assertTrue(permissionResult.getResult());

        ProductDTO productDTO = new ProductDTO("masmer hum", this.storeID, 20,
                new LinkedList<String>(Arrays.asList("brown", "nail")),
                new LinkedList<String>(Arrays.asList("masmer")));

        Response<Boolean> actionResult = bridge.addProductsToStore("x", productDTO, 20);
        Assert.assertFalse(actionResult.getResult());

        // remove product removed
        permissionResult = bridge.removePermission("aviad", this.storeID, "x",
                Permissions.REMOVE_PRODUCT_FROM_STORE);
        Assert.assertTrue(permissionResult.getResult());

        ProductDTO product = bridge.searchByProductName("masmer varod").getResult().get(0);

        actionResult = bridge.removeProductsFromStore("x", this.storeID, product.getProductID(), 20);
        Assert.assertFalse(actionResult.getResult());

        // update product removed
        permissionResult = bridge.removePermission("aviad", this.storeID, "x",
                Permissions.UPDATE_PRODUCT_PRICE);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = bridge.updateProductInfo("x", this.storeID, product.getProductID(),
                50, "masmer lavan");
        Assert.assertFalse(actionResult.getResult());

        // appoint owner removed
        permissionResult = bridge.removePermission("aviad", this.storeID, "x",
                Permissions.APPOINT_OWNER);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = bridge.appointStoreOwner("x", "y", this.storeID);
        Assert.assertFalse(actionResult.getResult());

        // remove owner removed
        permissionResult = bridge.removePermission("aviad", this.storeID, "x",
                Permissions.REMOVE_OWNER_APPOINTMENT);
        Assert.assertTrue(permissionResult.getResult());

        appointResult = bridge.appointStoreOwner("aviad", "y",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        actionResult = bridge.removeOwnerAppointment("x", "y", this.storeID);
        Assert.assertFalse(actionResult.getResult());

        // appoint manager removed
        permissionResult = bridge.removePermission("aviad", this.storeID, "x",
                Permissions.APPOINT_MANAGER);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = bridge.appointStoreManager("x", "z", this.storeID);
        Assert.assertFalse(actionResult.getResult());

        // edit permissions removed
        permissionResult = bridge.removePermission("aviad", this.storeID, "x",
                Permissions.EDIT_PERMISSION);
        Assert.assertTrue(permissionResult.getResult());

        appointResult = bridge.appointStoreManager("aviad", "z",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        actionResult = bridge.addPermission("x", this.storeID, "z",
                Permissions.RECEIVE_STORE_HISTORY);
        Assert.assertFalse(actionResult.getResult());

        // remove manager removed
        permissionResult = bridge.removePermission("aviad", this.storeID, "x",
                Permissions.REMOVE_MANAGER_APPOINTMENT);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = bridge.removeManagerAppointment("x", "z", this.storeID);
        Assert.assertFalse(actionResult.getResult());

        // receive store worker info removed
        permissionResult = bridge.removePermission("aviad", this.storeID, "x",
                Permissions.RECEIVE_STORE_WORKER_INFO);
        Assert.assertTrue(permissionResult.getResult());

        Response<List<User>> workersDetailsResult = bridge.getStoreWorkersDetails("x", this.storeID);
        Assert.assertTrue(workersDetailsResult.isFailure());

        // receive store history
        permissionResult = bridge.removePermission("aviad", this.storeID, "x",
                Permissions.RECEIVE_STORE_HISTORY);
        Assert.assertTrue(permissionResult.getResult());

        Response<Collection<PurchaseDTO>> historyResult = bridge.getPurchaseDetails("x", this.storeID);
        Assert.assertTrue(historyResult.isFailure());
    }

    @Test
    public void removePermissionWithoutPermissionTest() { // 4.6.2 bad
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "xx", "123456");
        bridge.register(guestName, "yy", "123456");

        bridge.login(bridge.addGuest().getResult(), "xx", "123456");
        bridge.login(bridge.addGuest().getResult(), "yy", "123456");

        // the owner will give a manager a permission and a user without permissions will try to take the permission from him
        Response<Boolean> appointResult = bridge.appointStoreManager("aviad", "xx",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // receive store history permission
        Response<Boolean> permissionResult = bridge.addPermission("aviad", this.storeID, "xx",
                Permissions.RECEIVE_STORE_HISTORY);
        Assert.assertTrue(permissionResult.getResult());

        // a user without permissions trying to take the permission. should fail
        permissionResult = bridge.removePermission("yy", this.storeID, "xx",
                Permissions.RECEIVE_STORE_HISTORY);
        Assert.assertFalse(permissionResult.getResult());

        // making sure he still has the permission
        Response<Collection<PurchaseDTO>> newActionResult = bridge.getPurchaseDetails("xx", this.storeID);
        Assert.assertFalse(newActionResult.isFailure());
    }

    @Test
    public void removeManagerAppointmentWithPermissionsTest(){ // 4.7 good
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "a", "123456");
        bridge.register(guestName, "b", "123456");
        bridge.register(guestName, "c", "123456");

        bridge.login(bridge.addGuest().getResult(), "a", "123456");
        bridge.login(bridge.addGuest().getResult(), "b", "123456");
        bridge.login(bridge.addGuest().getResult(), "c", "123456");

        // owner appoints a new manager
        Response<Boolean> appointResult = bridge.appointStoreManager("aviad", "a",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // giving permission to appoint to the new manager
        appointResult = bridge.addPermission("aviad", this.storeID, "a", Permissions.APPOINT_MANAGER);
        Assert.assertTrue(appointResult.getResult());

        // the new appointee tries to appoint a new owner
        appointResult = bridge.appointStoreManager("a", "b",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // the new appointee will try to remove his appointer
        Response<Boolean> removeResult = bridge.removeManagerAppointment("b", "a",
                this.storeID);
        Assert.assertFalse(removeResult.getResult());

        // owner tries to remove him
        removeResult = bridge.removeManagerAppointment("aviad", "a",
                this.storeID);
        Assert.assertTrue(removeResult.getResult());

        // the removed user will try to appoint another user. should fail
        appointResult = bridge.appointStoreOwner("a", "c",
                this.storeID);
        Assert.assertFalse(appointResult.getResult());

        // the second degree appointee shouldn't be able to look up the employees of the store
        Response<List<User>> workersDetails = bridge.getStoreWorkersDetails("b", this.storeID);
        Assert.assertTrue(workersDetails.isFailure());
    }

    @Test
    public void removeManagerAppointmentWithoutPermissionsTest() { // 4.7 bad
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "uu", "123456");
        bridge.register(guestName, "kk", "123456");

        bridge.login(bridge.addGuest().getResult(), "uu", "123456");
        bridge.login(bridge.addGuest().getResult(), "kk", "123456");

        // owner appoints a new manager
        Response<Boolean> appointResult = bridge.appointStoreManager("aviad", "uu",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // a user without permissions will try to remove his appointment. should fail
        Response<Boolean> removeResult = bridge.removeManagerAppointment("kk", "uu",
                this.storeID);
        Assert.assertFalse(removeResult.getResult());

        // manager still should be able to look up the workers of the store
        Response<List<User>> workersDetails = bridge.getStoreWorkersDetails("uu", this.storeID);
        Assert.assertFalse(workersDetails.isFailure());
    }


    @Test
    public void getStoreWorkersDetailsWithPermissionsTest(){ // 4.9 good
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "irina", "123456");

        bridge.login(bridge.addGuest().getResult(), "irina", "123456");


        // appointing a manager (has the permission to view it by default)
        Response<Boolean> appointResult = bridge.appointStoreManager("aviad", "irina",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // manager and owner trying to view it
        Response<List<User>> workerDetailsResult = bridge.getStoreWorkersDetails("irina", this.storeID);
        Assert.assertFalse(workerDetailsResult.isFailure());
        Assert.assertFalse(workerDetailsResult.getResult().isEmpty());

        workerDetailsResult = bridge.getStoreWorkersDetails("aviad", this.storeID);
        Assert.assertFalse(workerDetailsResult.isFailure());
        Assert.assertFalse(workerDetailsResult.getResult().isEmpty());
    }

    @Test
    public void getStoreWorkersDetailsWithoutPermissionsTest() { // 4.9 bad
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "shaoli", "123456");
        bridge.register(guestName, "hector", "123456");

        bridge.login(bridge.addGuest().getResult(), "shaoli", "123456");
        bridge.login(bridge.addGuest().getResult(), "hector", "123456");

        // appointing a manager (has the permission to view it by default)
        Response<Boolean> appointResult = bridge.appointStoreManager("aviad", "shaoli",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // taking the permission to view it from the manager and he tries to view it. should fail
        Response<Boolean> permissionResult = bridge.removePermission("aviad", this.storeID,
                "shaoli", Permissions.RECEIVE_STORE_WORKER_INFO);
        Assert.assertTrue(permissionResult.getResult());

        Response<List<User>> workerDetailsResult = bridge.getStoreWorkersDetails("shaoli", this.storeID);
        Assert.assertTrue(workerDetailsResult.isFailure());

        // now a user which is not a manger or an owner will try to view it
        workerDetailsResult = bridge.getStoreWorkersDetails("hector", this.storeID);
        Assert.assertTrue(workerDetailsResult.isFailure());

        // now a guest
        workerDetailsResult = bridge.getStoreWorkersDetails(guestName, this.storeID);
        Assert.assertTrue(workerDetailsResult.isFailure());
    }

    @Test
    public void getPurchaseDetailsWithPermissions(){ // 4.11 good
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "bibi", "123456");

        bridge.login(bridge.addGuest().getResult(), "bibi", "123456");

        // appointing a manager (doesn't have the permission by default)
        Response<Boolean> appointResult = bridge.appointStoreManager("aviad", "bibi",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // giving him the permission
        Response<Boolean> permissionResult = bridge.addPermission("aviad", this.storeID,
                "bibi", Permissions.RECEIVE_STORE_HISTORY);
        Assert.assertTrue(permissionResult.getResult());

        // manager and owner trying to view it
        Response<Collection<PurchaseDTO>> historyResult = bridge.getPurchaseDetails("bibi", this.storeID);
        Assert.assertFalse(historyResult.isFailure());
        Assert.assertNotNull(historyResult.getResult());

        historyResult = bridge.getPurchaseDetails("aviad", this.storeID);
        Assert.assertFalse(historyResult.isFailure());
        Assert.assertNotNull(historyResult.getResult());
    }

    @Test
    public void getPurchaseDetailsWithoutPermissions() { // 4.11 bad
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "benet", "123456");
        bridge.register(guestName, "lapid", "123456");

        bridge.login(bridge.addGuest().getResult(), "benet", "123456");
        bridge.login(bridge.addGuest().getResult(), "lapid", "123456");

        // appointing a manager (doesn't have the permission by default)
        Response<Boolean> appointResult = bridge.appointStoreManager("aviad", "benet",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // the manager tries to view it. should fail
        Response<Collection<PurchaseDTO>> historyResult = bridge.getPurchaseDetails("benet", this.storeID);
        Assert.assertTrue(historyResult.isFailure());

        // now a user which is not a manger or an owner will try to view it
        historyResult = bridge.getPurchaseDetails("lapid", this.storeID);
        Assert.assertTrue(historyResult.isFailure());

        // now a guest
        historyResult = bridge.getPurchaseDetails(guestName, this.storeID);
        Assert.assertTrue(historyResult.isFailure());
    }

    @Test
    public void removeOwnerAppointmentSuccessNotificationTest(){ // 9.1 good (4.4.1)
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "jay", "123456");

        bridge.login(bridge.addGuest().getResult(), "jay", "123456");

        notifier.addConnection("jay", null);

        // owner appoints a new owner
        Response<Boolean> appointResult = bridge.appointStoreOwner("aviad", "jay",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // owner tries to remove him
        Response<Boolean> removeResult = bridge.removeOwnerAppointment("aviad", "jay",
                this.storeID);
        Assert.assertTrue(removeResult.getResult());

        // the removed owner should have received a notification saying they were removed
        Assert.assertEquals(1, notifier.getMessages("jay").size());
    }

    @Test
    public void removeOwnerAppointmentFailureNotificationTest() { // 9.1 bad (4.4.1)
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "rrr", "123456");

        bridge.login(bridge.addGuest().getResult(), "rrr", "123456");

        notifier.addConnection("aviad", null);


        // a user without any permissions tried to remove the ownership of an owner
        Response<Boolean> removeResult = bridge.removeOwnerAppointment("rrr", "aviad",
                this.storeID);
        Assert.assertFalse(removeResult.getResult());

        // the owner didn't receive a notification because they weren't fired
        Assert.assertEquals(0, notifier.getMessages("aviad").size());
    }

    @Test
    public void removeOwnerAppointmentSuccessStoredNotificationTest(){ // 9.1 good (4.4.1)
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "jay", "123456");

        notifier.addConnection("jay", null);

        // owner appoints a new owner
        Response<Boolean> appointResult = bridge.appointStoreOwner("aviad", "jay",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // owner tries to remove him
        Response<Boolean> removeResult = bridge.removeOwnerAppointment("aviad", "jay",
                this.storeID);
        Assert.assertTrue(removeResult.getResult());

        bridge.login(bridge.addGuest().getResult(), "jay", "123456");

        // the removed owner should have received a notification saying they were removed
        Assert.assertEquals(1, notifier.getMessages("jay").size());
    }

    @Test
    public void removeOwnerAppointmentFailureStoredNotificationTest() { // 9.1 bad (4.4.1)
        String guestName = bridge.addGuest().getResult();
        bridge.register(guestName, "rrr", "123456");

        bridge.login(bridge.addGuest().getResult(), "rrr", "123456");

        notifier.addConnection("aviad", null);

        bridge.logout("aviad");


        // a user without any permissions tried to remove the ownership of an owner
        Response<Boolean> removeResult = bridge.removeOwnerAppointment("rrr", "aviad",
                this.storeID);
        Assert.assertFalse(removeResult.getResult());

        bridge.login(bridge.addGuest().getResult(), "aviad", "123456");

        // the owner didn't receive a notification because they weren't fired
        Assert.assertEquals(0, notifier.getMessages("aviad").size());
    }

}
