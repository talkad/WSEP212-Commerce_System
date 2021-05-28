package TestComponent.AcceptanceTestings.Tests;

import Server.DAL.DALService;
import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.CommerceSystem;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentSystemAdapter;
import Server.Domain.UserManager.ExternalSystemsAdapters.ProductSupplyAdapter;
import TestComponent.AcceptanceTestings.Bridge.Driver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SystemTests extends ProjectAcceptanceTests {


    @Before
    public void setUp(){
        super.setUp(false);
    }

    @Test
    public void systemBootTest(){ // 1.1
        // checking if there exists an admin. there's a built in admin and we'll try to log into his account
        bridge = Driver.getBridge();
        bridge.init();
        notifier = Driver.getNotifier();
        DALService.getInstance().useTestDatabase();
        DALService.getInstance().resetDatabase();
        PaymentSystemAdapter.getInstance().setMockFlag();
        ProductSupplyAdapter.getInstance().setMockFlag();
        String guestName = bridge.addGuest().getResult();
        Response<String> loginResponse = bridge.login(guestName, "u1", "u1");
        Assert.assertFalse(loginResponse.isFailure());
    }

    @Test
    public void initfileSuccess(){
        bridge = Driver.getBridge();
        DALService.getInstance().useTestDatabase();
        DALService.getInstance().resetDatabase();
        //bridge.init();
        bridge.configInit();
        notifier = Driver.getNotifier();

        PaymentSystemAdapter.getInstance().setMockFlag();
        ProductSupplyAdapter.getInstance().setMockFlag();
        Response<Boolean> res = bridge.initState("initfile");
        Assert.assertFalse(res.isFailure());
        Assert.assertEquals("u2", bridge.getUserByName("u2").getName());
    }

    @Test
    public void initfileFailure(){
        bridge = Driver.getBridge();
        bridge.configInit();
        notifier = Driver.getNotifier();
        DALService.getInstance().useTestDatabase();
        DALService.getInstance().resetDatabase();
        PaymentSystemAdapter.getInstance().setMockFlag();
        ProductSupplyAdapter.getInstance().setMockFlag();
        Response<Boolean> res = bridge.initState("failedinitfile");
        Assert.assertTrue(res.isFailure());
        Assert.assertEquals(res.getErrMsg(), "CRITICAL Error: Bad initfile");
    }
}
