package TestComponent.AcceptanceTestings.Tests;

import Server.DAL.DALControllers.DALService;
import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.CommerceSystem;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentSystemAdapter;
import Server.Domain.UserManager.ExternalSystemsAdapters.ProductSupplyAdapter;
import Server.Domain.UserManager.UserController;
import Server.Service.CommerceService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

public class StatisticsTests extends ProjectAcceptanceTests{

    @Before
    public void setUp(){
        super.setUp(true);
    }


    @Test
    public void basicSuccessStatistics() {
        UserController.getInstance().zeroCounters();
        String initialUserName = bridge.addGuest().getResult();
        bridge.login(initialUserName, "u2", "u2");
        initialUserName = bridge.logout("u2").getResult();
        bridge.login(initialUserName, "u3", "u3");
        initialUserName = bridge.logout("u3").getResult();
        bridge.login(initialUserName, "a1", "a1");
        List<String> statistics = bridge.getDailyStatistics("a1", LocalDate.now()).getResult();
        Assert.assertEquals("Guest: 3", statistics.get(0));
        Assert.assertEquals("Registered: 1", statistics.get(1));
        Assert.assertEquals("Manager: 1", statistics.get(2));
        Assert.assertEquals("Owner: 0"  , statistics.get(3));
        Assert.assertEquals("Admin: 1", statistics.get(4));
    }

    @Test
    public void failedFutureDatestatistics() {
        String initialUserName = bridge.addGuest().getResult();
        bridge.login(initialUserName, "a1", "a1");
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        Response<List<String>> statisticsRes = bridge.getDailyStatistics("a1", tomorrow);
        Assert.assertTrue(statisticsRes.isFailure());
    }
}
