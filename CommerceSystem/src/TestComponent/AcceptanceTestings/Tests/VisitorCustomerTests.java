package TestComponent.AcceptanceTestings.Tests;

import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.Purchase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class VisitorCustomerTests extends ProjectAcceptanceTests{

    @Before
    public void setUp(){
        super.setUp();

        // pre-registered user in the system
        String guestName = this.bridge.addGuest().getResult();
        this.bridge.register(guestName, "aviad", "123456");

    }

    @Test
    public void enteringSystemTest(){ // 2.1
        // what TODO?
    }

    @Test
    public void quittingSystemTest(){ // 2.2
        // what TODO?
    }

    @Test
    public void registerTest(){ // 2.3
        // registering with a unique username
        String guestName = this.bridge.addGuest().getResult();
        Response<Boolean> registerResponse = this.bridge.register(guestName, "issac", "123456");
        Assert.assertFalse(registerResponse.isFailure());

        // trying to log in with the newly registered user
        Response<String> loginResponse = this.bridge.login(guestName, "issac", "123456");
        Assert.assertFalse(loginResponse.isFailure());

        // now logging out and trying to register a new user with the same username
        Response<String> logoutResponse = this.bridge.logout(loginResponse.getResult());
        registerResponse = this.bridge.register(logoutResponse.getResult(), "issac", "654321");
        Assert.assertTrue(registerResponse.isFailure());
    }

    @Test
    public void loginTest(){ // 2.4
        // trying to login to a user which does not exit
        String guestName = this.bridge.addGuest().getResult();
        Response<String> loginResponse = this.bridge.login(guestName, "shlomi", "123456");
        Assert.assertFalse(loginResponse.isFailure());

        // trying to do an action only a logged in user can do. should fail
        Response<List<Purchase>> actionResponse =  this.bridge.getPurchaseHistory("shlomi");
        Assert.assertTrue(actionResponse.isFailure());


        // logging in to the preregistered user
        Response<String> logoutResponse = this.bridge.logout(loginResponse.getResult());
        guestName = logoutResponse.getResult();
        loginResponse = this.bridge.login(guestName, "aviad", "123456");
        Assert.assertFalse(loginResponse.isFailure());

        // trying to do an action only a logged in user can do. should pass
        actionResponse =  this.bridge.getPurchaseHistory("shlomi");
        Assert.assertFalse(actionResponse.isFailure());
    }

    @Test
    public void searchStoreTest(){ // 2.5

    }

    @Test
    public void searchProductTest(){ // 2.6

    }

    @Test
    public void addToCart(){ // 2.7

    }

    @Test
    public void getCartTest(){ // 2.8.1

    }

    @Test
    public void removeProductTest(){ // 2.8.2

    }

    @Test
    public void updateProductQuantityTest(){ // 2.8.3

    }

    @Test
    public void directPurchaseTest(){ // 2.9

    }
}
