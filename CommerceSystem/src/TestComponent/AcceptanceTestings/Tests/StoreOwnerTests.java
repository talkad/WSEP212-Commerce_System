package TestComponent.AcceptanceTestings.Tests;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.UserManager.Permissions;
import Server.Domain.UserManager.PurchaseDTO;
import Server.Domain.UserManager.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

/**
 * also Store Manager tests
 */
public class StoreOwnerTests extends ProjectAcceptanceTests{

    private int storeID; // the store id of the store we are going to test on

    @BeforeClass
    public void setUp(){
        super.setUp();

        // pre-registered users in the system
        String guestName = this.bridge.addGuest().getResult();
        this.bridge.register(guestName, "aviad", "123456"); // store owner
        this.bridge.register(guestName, "jacob", "123456");

        // logged in users
        this.bridge.login(guestName, "aviad", "123456");
        this.bridge.login(guestName, "jacob", "123456");
        this.bridge.login(guestName, "abraham", "123456");
        this.bridge.login(guestName, "issac", "123456");

        // opening some stores for later use
        this.storeID = this.bridge.openStore("aviad", "masmerim behinam").getResult();

        ProductDTO productDTO = new ProductDTO("masmer adom", this.storeID, 20,
                new LinkedList<String>(Arrays.asList("red", "nail")),
                new LinkedList<String>(Arrays.asList("masmer")),
                null);

        this.bridge.addProductsToStore("aviad", productDTO, 20);

        productDTO = new ProductDTO("masmer varod", this.storeID, 20,
                new LinkedList<String>(Arrays.asList("pink", "nail")),
                new LinkedList<String>(Arrays.asList("masmer")),
                null);

        this.bridge.addProductsToStore("aviad", productDTO, 20);
    }

    @Test
    public void addProductToStoreTest(){ // 4.1.1
        // a user with permissions of a store adding a products to the store

        ProductDTO productDTO = new ProductDTO("masmer yarok", this.storeID, 20,
                new LinkedList<String>(Arrays.asList("green", "nail")),
                new LinkedList<String>(Arrays.asList("masmer")),
                null);

        Response<Boolean> addResponse = this.bridge.addProductsToStore("aviad", productDTO, 20);
        Assert.assertTrue(addResponse.getResult());

        // looking the product up in the store
        Response<List<ProductDTO>> searchResponse = this.bridge.searchByProductName("masmer yarok");

        boolean exists = false;
        for(ProductDTO product: searchResponse.getResult()){
            if(product.getStoreID() == this.storeID){
                exists = true;
            }
        }

        Assert.assertTrue(exists);

        // now a user which doesn't have permissions will try to add a product
        productDTO = new ProductDTO("masmer shahor", this.storeID, 20,
                new LinkedList<String>(Arrays.asList("black", "nail")),
                new LinkedList<String>(Arrays.asList("masmer")),
                null);
        addResponse = this.bridge.addProductsToStore("jacob", productDTO, 20);
        Assert.assertFalse(addResponse.getResult());

        // making sure it wasn't added
        exists = false;
        for(ProductDTO product: searchResponse.getResult()){
            if(product.getStoreID() == this.storeID){
                exists = true;
            }
        }

        Assert.assertFalse(exists);
    }

    @Test
    public void removeProductFromStore(){ // 4.1.2
        // a permitted user trying to remove a non-existing product
        Response<Boolean> deletionResponse = this.bridge.removeProductsFromStore("aviad", this.storeID,
                -1, 1);
        Assert.assertFalse(deletionResponse.getResult());

        // a permitted user trying to remove an existing product
        Response<List<ProductDTO>> searchResponse = this.bridge.searchByProductName("masmer adom");
        int productID = searchResponse.getResult().get(0).getProductID();
        deletionResponse = this.bridge.removeProductsFromStore("aviad", this.storeID, productID, 20);
        Assert.assertTrue(deletionResponse.getResult());

        // making sure the product is gone
        searchResponse = this.bridge.searchByProductName("masmer adom");

        boolean exists = false;
        for(ProductDTO product: searchResponse.getResult()){
            if(product.getStoreID() == this.storeID){
                exists = true;
            }
        }

        Assert.assertFalse(exists);

        // now a user without permissions will try to remove an existing product
        searchResponse = this.bridge.searchByProductName("masmer varod");
        productID = searchResponse.getResult().get(0).getProductID();
        deletionResponse = this.bridge.removeProductsFromStore("jacob", this.storeID, productID, 20);
        Assert.assertFalse(deletionResponse.getResult());

        // making sure it's not deleted
        searchResponse = this.bridge.searchByProductName("masmer varod");

        exists = false;
        for(ProductDTO product: searchResponse.getResult()){
            if(product.getStoreID() == this.storeID){
                exists = true;
            }
        }

        Assert.assertTrue(exists);
    }

    @Test
    public void updateProductInfoTest(){
        // a user with permissions trying to update
        Response<List<ProductDTO>> searchResponse = this.bridge.searchByProductName("masmer varod");
        int productID = searchResponse.getResult().get(0).getProductID();
        int newPrice = 100;
        Response<Boolean> updateResponse = this.bridge.updateProductInfo("aviad", this.storeID,
                productID, newPrice, "masmer varod");
        Assert.assertTrue(updateResponse.getResult());

        // making sure it's changed
        searchResponse = this.bridge.searchByProductName("masmer varod");

        boolean updated = false;
        for(ProductDTO product: searchResponse.getResult()){
            if(product.getStoreID() == this.storeID){
                if(product.getPrice() == newPrice) {
                    updated = true;
                }
            }
        }

        Assert.assertTrue(updated);

        // now a user without permissions will try to update
        int newerPrice = 200;
        updateResponse = this.bridge.updateProductInfo("jacob", this.storeID,
                productID, newerPrice, "masmer varod");

        // making sure it's not changed
        searchResponse = this.bridge.searchByProductName("masmer varod");

        updated = false;
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
    public void appointStoreOwnerTest(){ // 4.3.1
        String guestName = this.bridge.addGuest().getResult();
        this.bridge.register(guestName, "abraham", "123456");
        this.bridge.register(guestName, "issac", "123456");

        this.bridge.login(this.bridge.addGuest().getResult(), "abraham", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "issac", "123456");

        // owner appoints a new owner
        Response<Boolean> appointResult = this.bridge.appointStoreOwner("aviad", "issac",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());


        // if the appointment succeed then he should be able to appoint a new owner
        appointResult = this.bridge.appointStoreOwner("issac", "abraham", this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // now an owner will try to appoint the original store owner. should fail
        appointResult = this.bridge.appointStoreOwner("abraham", "aviad", this.storeID);
        Assert.assertFalse(appointResult.getResult());

        // now a user without any permissions will try to appoint another user
        this.bridge.register(guestName, "abraham2", "123456");
        this.bridge.register(guestName, "issac2", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "abraham2", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "issac2", "123456");

        appointResult = this.bridge.appointStoreOwner("abraham2", "issac2", this.storeID);
        Assert.assertFalse(appointResult.getResult());
    }

    @Test
    public void removeOwnerAppointmentTest(){ // 4.4.1
        String guestName = this.bridge.addGuest().getResult();
        this.bridge.register(guestName, "aaa", "123456");
        this.bridge.register(guestName, "bbb", "123456");
        this.bridge.register(guestName, "ccc", "123456");

        this.bridge.login(this.bridge.addGuest().getResult(), "aaa", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "bbb", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "ccc", "123456");

        // owner appoints a new owner
        Response<Boolean> appointResult = this.bridge.appointStoreOwner("aviad", "aaa",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // the new appointee tries to appoint a new owner
        appointResult = this.bridge.appointStoreOwner("aaa", "bbb",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // the new appointee will try to remove his appointer
        Response<Boolean> removeResult = this.bridge.removeOwnerAppointment("aaa", "aviad",
                this.storeID);
        Assert.assertFalse(removeResult.getResult());

        // owner tries to remove him
        removeResult = this.bridge.removeOwnerAppointment("aviad", "aaa",
                this.storeID);
        Assert.assertTrue(removeResult.getResult());

        // the removed user will try to appoint another user. should fail
        appointResult = this.bridge.appointStoreOwner("aaa", "ccc",
                this.storeID);
        Assert.assertFalse(appointResult.getResult());

        // the second degree appointee shouldn't be able to appoint either.
        appointResult = this.bridge.appointStoreOwner("bbb", "ccc",
                this.storeID);
        Assert.assertFalse(appointResult.getResult());
    }

    @Test
    public void appointStoreManager(){ // 4.5.1
        String guestName = this.bridge.addGuest().getResult();
        this.bridge.register(guestName, "aa", "123456");
        this.bridge.register(guestName, "bb", "123456");

        this.bridge.login(this.bridge.addGuest().getResult(), "aa", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "bb", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "cc", "123456");

        // owner appoints a manager
        Response<Boolean> appointResult = this.bridge.appointStoreManager("aviad", "aa",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // new appointee should be able to see the workers but not appoint a new owner or manager
        Response<List<User>> workersResult = this.bridge.getStoreWorkersDetails("aa", this.storeID);
        Assert.assertFalse(workersResult.isFailure());
        Assert.assertFalse(workersResult.getResult().isEmpty());

        appointResult = this.bridge.appointStoreManager("aa", "bb",
                this.storeID);
        Assert.assertFalse(appointResult.getResult());

        workersResult = this.bridge.getStoreWorkersDetails("bb", this.storeID);
        Assert.assertTrue(workersResult.isFailure());

        // owner trying to appoint the new manager again. should fail
        appointResult = this.bridge.appointStoreManager("aviad", "aa",
                this.storeID);
        Assert.assertFalse(appointResult.getResult());
    }

    @Test
    public void addPermissionTest(){ // 4.6.1
        String guestName = this.bridge.addGuest().getResult();
        this.bridge.register(guestName, "d", "123456");
        this.bridge.register(guestName, "e", "123456");
        this.bridge.register(guestName, "f", "123456");

        this.bridge.login(this.bridge.addGuest().getResult(), "d", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "e", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "f", "123456");

        // owner will appoint a new manager and each time will give him permission to do something
        Response<Boolean> appointResult = this.bridge.appointStoreManager("aviad", "e",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // permission to add a product
        Response<Boolean> permissionResult = this.bridge.addPermission("aviad", this.storeID, "e",
                Permissions.ADD_PRODUCT_TO_STORE);
        Assert.assertTrue(permissionResult.getResult());

        ProductDTO productDTO = new ProductDTO("masmer krem", this.storeID, 20,
                new LinkedList<String>(Arrays.asList("krem", "nail")),
                new LinkedList<String>(Arrays.asList("masmer")),
                null);

        Response<Boolean> actionResult = this.bridge.addProductsToStore("e", productDTO, 20);
        Assert.assertTrue(actionResult.getResult());

        ProductDTO product = this.bridge.searchByProductName("masmer krem").getResult().get(0);

        // permission to update a product
        permissionResult = this.bridge.addPermission("aviad", this.storeID, "e",
                Permissions.UPDATE_PRODUCT_PRICE);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = this.bridge.updateProductInfo("e", this.storeID, product.getProductID(),
                50, "masmer kremi");
        Assert.assertTrue(actionResult.getResult());

        // permission to remove a product
        permissionResult = this.bridge.addPermission("aviad", this.storeID, "e",
                Permissions.REMOVE_PRODUCT_FROM_STORE);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = this.bridge.removeProductsFromStore("e", this.storeID, product.getProductID(),
                10);
        Assert.assertTrue(actionResult.getResult());

        // permission to appoint an owner
        permissionResult = this.bridge.addPermission("aviad", this.storeID, "e",
                Permissions.APPOINT_OWNER);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = this.bridge.appointStoreOwner("e", "d", this.storeID);
        Assert.assertTrue(actionResult.getResult());

        // permission to remove an owner appointment
        permissionResult = this.bridge.addPermission("aviad", this.storeID, "e",
                Permissions.REMOVE_OWNER_APPOINTMENT);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = this.bridge.removeOwnerAppointment("e", "d", this.storeID);
        Assert.assertTrue(actionResult.getResult());

        // permission to appoint a manager
        permissionResult = this.bridge.addPermission("aviad", this.storeID, "e",
                Permissions.APPOINT_MANAGER);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = this.bridge.appointStoreManager("e", "f", this.storeID);
        Assert.assertTrue(actionResult.getResult());

        // permission to edit permissions
        permissionResult = this.bridge.addPermission("aviad", this.storeID, "e",
                Permissions.EDIT_PERMISSION);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = this.bridge.addPermission("e", this.storeID, "f",
                Permissions.GET_PURCHASE_HISTORY);
        Assert.assertTrue(actionResult.getResult());

        Response<Collection<PurchaseDTO>> newActionResult = this.bridge.getPurchaseDetails("f", this.storeID);
        Assert.assertFalse(newActionResult.isFailure());

        //permission to remove a manager appointment
        permissionResult = this.bridge.addPermission("aviad", this.storeID, "e",
                Permissions.REMOVE_MANAGER_APPOINTMENT);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = this.bridge.removeManagerAppointment("e", "f", this.storeID);
        Assert.assertTrue(actionResult.getResult());
    }

    @Test
    public void removePermissionTest(){ // 4.6.2
        String guestName = this.bridge.addGuest().getResult();
        this.bridge.register(guestName, "x", "123456");
        this.bridge.register(guestName, "y", "123456");
        this.bridge.register(guestName, "z", "123456");

        this.bridge.login(this.bridge.addGuest().getResult(), "x", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "y", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "z", "123456");

        // the owner will give a manager all of the available permissions and will remove them one at a time
        Response<Boolean> appointResult = this.bridge.appointStoreManager("aviad", "x",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());


        // add product permission
        Response<Boolean> permissionResult = this.bridge.addPermission("aviad", this.storeID, "x",
                Permissions.ADD_PRODUCT_TO_STORE);
        Assert.assertTrue(permissionResult.getResult());

        // remove product permission
        permissionResult = this.bridge.addPermission("aviad", this.storeID, "x",
                Permissions.REMOVE_PRODUCT_FROM_STORE);
        Assert.assertTrue(permissionResult.getResult());

        // update product permission
        permissionResult = this.bridge.addPermission("aviad", this.storeID, "x",
                Permissions.UPDATE_PRODUCT_PRICE);
        Assert.assertTrue(permissionResult.getResult());

        // appoint owner permission
        permissionResult = this.bridge.addPermission("aviad", this.storeID, "x",
                Permissions.APPOINT_OWNER);
        Assert.assertTrue(permissionResult.getResult());

        // remove owner permission
        permissionResult = this.bridge.addPermission("aviad", this.storeID, "x",
                Permissions.REMOVE_OWNER_APPOINTMENT);
        Assert.assertTrue(permissionResult.getResult());

        // appoint manager permission
        permissionResult = this.bridge.addPermission("aviad", this.storeID, "x",
                Permissions.APPOINT_MANAGER);
        Assert.assertTrue(permissionResult.getResult());

        // edit permission permission
        permissionResult = this.bridge.addPermission("aviad", this.storeID, "x",
                Permissions.EDIT_PERMISSION);
        Assert.assertTrue(permissionResult.getResult());

        // remove manager permission
        permissionResult = this.bridge.addPermission("aviad", this.storeID, "x",
                Permissions.REMOVE_MANAGER_APPOINTMENT);
        Assert.assertTrue(permissionResult.getResult());

        // receive store worker info permission
        permissionResult = this.bridge.addPermission("aviad", this.storeID, "x", // although it is given to him beforehand
                Permissions.RECEIVE_STORE_WORKER_INFO);
        Assert.assertTrue(permissionResult.getResult());

        // receive store history permission
        permissionResult = this.bridge.addPermission("aviad", this.storeID, "x",
                Permissions.RECEIVE_STORE_HISTORY);
        Assert.assertTrue(permissionResult.getResult());


        // remove permissions testings

        // add product removed
        permissionResult = this.bridge.removePermission("aviad", this.storeID, "x",
                Permissions.ADD_PRODUCT_TO_STORE);
        Assert.assertTrue(permissionResult.getResult());

        ProductDTO productDTO = new ProductDTO("masmer hum", this.storeID, 20,
                new LinkedList<String>(Arrays.asList("brown", "nail")),
                new LinkedList<String>(Arrays.asList("masmer")),
                null);

        Response<Boolean> actionResult = this.bridge.addProductsToStore("x", productDTO, 20);
        Assert.assertFalse(actionResult.getResult());

        // remove product removed
        permissionResult = this.bridge.removePermission("aviad", this.storeID, "x",
                Permissions.REMOVE_PRODUCT_FROM_STORE);
        Assert.assertTrue(permissionResult.getResult());

        ProductDTO product = this.bridge.searchByProductName("masmer hum").getResult().get(0);

        actionResult = this.bridge.removeProductsFromStore("x", this.storeID, product.getProductID(), 20);
        Assert.assertFalse(actionResult.getResult());

        // update product removed
        permissionResult = this.bridge.removePermission("aviad", this.storeID, "x",
                Permissions.UPDATE_PRODUCT_PRICE);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = this.bridge.updateProductInfo("x", this.storeID, product.getProductID(),
                50, "masmer lavan");
        Assert.assertFalse(actionResult.getResult());

        // appoint owner removed
        permissionResult = this.bridge.removePermission("aviad", this.storeID, "x",
                Permissions.APPOINT_OWNER);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = this.bridge.appointStoreOwner("x", "y", this.storeID);
        Assert.assertFalse(actionResult.getResult());

        // remove owner removed
        permissionResult = this.bridge.removePermission("aviad", this.storeID, "x",
                Permissions.REMOVE_OWNER_APPOINTMENT);
        Assert.assertTrue(permissionResult.getResult());

        appointResult = this.bridge.appointStoreOwner("aviad", "y",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        actionResult = this.bridge.removeOwnerAppointment("x", "y", this.storeID);
        Assert.assertFalse(actionResult.getResult());

        // appoint manager removed
        permissionResult = this.bridge.removePermission("aviad", this.storeID, "x",
                Permissions.APPOINT_MANAGER);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = this.bridge.appointStoreManager("x", "z", this.storeID);
        Assert.assertFalse(actionResult.getResult());

        // edit permissions removed
        permissionResult = this.bridge.removePermission("aviad", this.storeID, "x",
                Permissions.EDIT_PERMISSION);
        Assert.assertTrue(permissionResult.getResult());

        appointResult = this.bridge.appointStoreManager("aviad", "z",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        actionResult = this.bridge.addPermission("x", this.storeID, "z",
                Permissions.RECEIVE_STORE_HISTORY);
        Assert.assertFalse(actionResult.getResult());

        // remove manager removed
        permissionResult = this.bridge.removePermission("aviad", this.storeID, "x",
                Permissions.REMOVE_MANAGER_APPOINTMENT);
        Assert.assertTrue(permissionResult.getResult());

        actionResult = this.bridge.removeManagerAppointment("x", "z", this.storeID);
        Assert.assertFalse(actionResult.getResult());

        // receive store worker info removed
        permissionResult = this.bridge.removePermission("aviad", this.storeID, "x",
                Permissions.RECEIVE_STORE_WORKER_INFO);
        Assert.assertTrue(permissionResult.getResult());

        Response<List<User>> workersDetailsResult = this.bridge.getStoreWorkersDetails("x", this.storeID);
        Assert.assertTrue(workersDetailsResult.isFailure());

        // receive store history
        permissionResult = this.bridge.removePermission("aviad", this.storeID, "x",
                Permissions.RECEIVE_STORE_HISTORY);
        Assert.assertTrue(permissionResult.getResult());

        Response<Collection<PurchaseDTO>> historyResult = this.bridge.getPurchaseDetails("x", this.storeID);
        Assert.assertTrue(historyResult.isFailure());
    }

    @Test
    public void removeManagerAppointmentTest(){ // 4.7
        String guestName = this.bridge.addGuest().getResult();
        this.bridge.register(guestName, "a", "123456");
        this.bridge.register(guestName, "b", "123456");
        this.bridge.register(guestName, "c", "123456");

        this.bridge.login(this.bridge.addGuest().getResult(), "a", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "b", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "c", "123456");

        // owner appoints a new manager
        Response<Boolean> appointResult = this.bridge.appointStoreManager("aviad", "a",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // giving permission to appoint to the new manager
        appointResult = this.bridge.addPermission("aviad", this.storeID, "a", Permissions.APPOINT_MANAGER);
        Assert.assertTrue(appointResult.getResult());

        // the new appointee tries to appoint a new owner
        appointResult = this.bridge.appointStoreManager("a", "b",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // the new appointee will try to remove his appointer
        Response<Boolean> removeResult = this.bridge.removeManagerAppointment("b", "a",
                this.storeID);
        Assert.assertFalse(removeResult.getResult());

        // owner tries to remove him
        removeResult = this.bridge.removeManagerAppointment("aviad", "a",
                this.storeID);
        Assert.assertTrue(removeResult.getResult());

        // the removed user will try to appoint another user. should fail
        appointResult = this.bridge.appointStoreOwner("a", "c",
                this.storeID);
        Assert.assertFalse(appointResult.getResult());

        // the second degree appointee shouldn't be able to look up the employees of the store
        Response<List<User>> workersDetails = this.bridge.getStoreWorkersDetails("b", this.storeID);
        Assert.assertTrue(workersDetails.isFailure());
    }

    @Test
    public void getStoreWorkersDetailsTest(){ // 4.9
        String guestName = this.bridge.addGuest().getResult();
        this.bridge.register(guestName, "shaoli", "123456");
        this.bridge.register(guestName, "irina", "123456");
        this.bridge.register(guestName, "hector", "123456");

        this.bridge.login(this.bridge.addGuest().getResult(), "shaoli", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "irina", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "hector", "123456");


        // appointing a manager (has the permission to view it by default)
        Response<Boolean> appointResult = this.bridge.appointStoreManager("aviad", "shaoli",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // appointing a manager (has the permission to view it by default)
        appointResult = this.bridge.appointStoreOwner("aviad", "irina",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // manager and owner trying to view it
        Response<List<User>> workerDetailsResult = this.bridge.getStoreWorkersDetails("shaoli", this.storeID);
        Assert.assertFalse(workerDetailsResult.isFailure());
        Assert.assertFalse(workerDetailsResult.getResult().isEmpty());

        workerDetailsResult = this.bridge.getStoreWorkersDetails("irina", this.storeID);
        Assert.assertFalse(workerDetailsResult.isFailure());
        Assert.assertFalse(workerDetailsResult.getResult().isEmpty());

        // taking the permission to view it from the manager and he tries to view it. should fail
        Response<Boolean> permissionResult = this.bridge.removePermission("aviad", this.storeID,
                "shaoli", Permissions.RECEIVE_STORE_WORKER_INFO);
        Assert.assertTrue(permissionResult.getResult());

        workerDetailsResult = this.bridge.getStoreWorkersDetails("shaoli", this.storeID);
        Assert.assertTrue(workerDetailsResult.isFailure());

        // now a user which is not a manger or an owner will try to view it
        workerDetailsResult = this.bridge.getStoreWorkersDetails("hector", this.storeID);
        Assert.assertTrue(workerDetailsResult.isFailure());

        // now a guest
        workerDetailsResult = this.bridge.getStoreWorkersDetails(guestName, this.storeID);
        Assert.assertTrue(workerDetailsResult.isFailure());
    }

    @Test
    public void getPurchaseDetails(){ // 4.11
        String guestName = this.bridge.addGuest().getResult();
        this.bridge.register(guestName, "bibi", "123456");
        this.bridge.register(guestName, "benet", "123456");
        this.bridge.register(guestName, "lapid", "123456");

        this.bridge.login(this.bridge.addGuest().getResult(), "bibi", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "benet", "123456");
        this.bridge.login(this.bridge.addGuest().getResult(), "lapid", "123456");


        // appointing a manager (doesn't have the permission by default)
        Response<Boolean> appointResult = this.bridge.appointStoreManager("aviad", "bibi",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // giving him the permission
        Response<Boolean> permissionResult = this.bridge.removePermission("aviad", this.storeID,
                "bibi", Permissions.RECEIVE_STORE_HISTORY);
        Assert.assertTrue(permissionResult.getResult());

        // appointing a manager (has the permission to view it by default)
        appointResult = this.bridge.appointStoreOwner("aviad", "benet",
                this.storeID);
        Assert.assertTrue(appointResult.getResult());

        // manager and owner trying to view it
        Response<Collection<PurchaseDTO>> historyResult = this.bridge.getPurchaseDetails("bibi", this.storeID);
        Assert.assertFalse(historyResult.isFailure());
        Assert.assertNotNull(historyResult.getResult());

        historyResult = this.bridge.getPurchaseDetails("benet", this.storeID);
        Assert.assertFalse(historyResult.isFailure());
        Assert.assertNotNull(historyResult.getResult());

        // taking the permission to view it from the manager and he tries to view it. should fail
        permissionResult = this.bridge.removePermission("aviad", this.storeID,
                "shaoli", Permissions.RECEIVE_STORE_HISTORY);
        Assert.assertTrue(permissionResult.getResult());

        historyResult = this.bridge.getPurchaseDetails("bibi", this.storeID);
        Assert.assertTrue(historyResult.isFailure());

        // now a user which is not a manger or an owner will try to view it
        historyResult = this.bridge.getPurchaseDetails("lapid", this.storeID);
        Assert.assertTrue(historyResult.isFailure());

        // now a guest
        historyResult = this.bridge.getPurchaseDetails(guestName, this.storeID);
        Assert.assertTrue(historyResult.isFailure());
    }
}
