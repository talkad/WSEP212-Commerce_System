package TestComponent.AcceptanceTestings.Tests;

import Server.Domain.CommonClasses.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SystemTests extends ProjectAcceptanceTests {


    @Before
    public void setUp(){
        super.setUp();
    }

    @Test
    public void systemBootTest(){ // 1.1
        // checking if there exists an admin. there's a built in admin and we'll try to log into his account
        String guestName = this.bridge.addGuest().getResult();
        Response<String> loginResponse = this.bridge.login(guestName, "shaked",
                Integer.toString("jacob".hashCode()));
        Assert.assertFalse(loginResponse.isFailure());
    }
}
