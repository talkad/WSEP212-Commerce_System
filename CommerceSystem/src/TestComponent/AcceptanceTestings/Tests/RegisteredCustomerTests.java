package TestComponent.AcceptanceTestings.Tests;

import Server.Domain.CommonClasses.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;

public class RegisteredCustomerTests extends ProjectAcceptanceTests{

    @Before
    public void setUp(){
        super.setUp();

        String guestName = this.bridge.addGuest().getResult();
        this.bridge.register(guestName, "aviad", "123456");
        this.bridge.login(guestName, "aviad", "123456");

        this.bridge.register(guestName, "shalom", "123456");
    }

    @BeforeEach
    public void beforeEachSetUp(){
        // TODO: do we need this?
        String guestName = this.bridge.addGuest().getResult();

    }

    @Test
    public void logout(){ // 3.1
        // logging in to an existing user.
        String guestName = this.bridge.addGuest().getResult();
        this.bridge.login(guestName, "shalom", "123456");
        Response<String> logoutResponse = this.bridge.logout("shalom");
        Assert.assertFalse(logoutResponse.isFailure());
        Assert.assertTrue(logoutResponse.getResult().contains("Guest")); // checking if the user became a guest

        // trying to log out from an already logged out user
        logoutResponse = this.bridge.logout("shalom");
        Assert.assertTrue(logoutResponse.isFailure());
    }

    @Test
    public void createStoreTest(){ // 3.2
        // opening a store from a logged in user
        Response<Integer> openStoreResponse = bridge.openStore("aviad", "hacol la even");
        Assert.assertFalse(openStoreResponse.isFailure());

        // looking the store up on the search just to be sure
        // TODO: looking the store up

        // trying to open a store from a user which is not logged in. should fail
        openStoreResponse = bridge.openStore("shalom", "bug");
        Assert.assertTrue(openStoreResponse.isFailure());
    }

    @Test
    public void reviewProduct(){ // 3.3

    }

    @Test
    public void getPurchaseHistory(){ // 3.7

    }
}
