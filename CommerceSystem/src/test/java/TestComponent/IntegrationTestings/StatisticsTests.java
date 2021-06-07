package TestComponent.IntegrationTestings;


import Server.DAL.DALService;
import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentSystemAdapter;
import Server.Domain.UserManager.ExternalSystemsAdapters.ProductSupplyAdapter;
import Server.Domain.UserManager.UserController;
import Server.Service.CommerceService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

public class StatisticsTests {
    @Before
    public void init(){
        DALService.getInstance().useTestDatabase();
        DALService.getInstance().resetDatabase();
        PaymentSystemAdapter.getInstance().setMockFlag();
        ProductSupplyAdapter.getInstance().setMockFlag();
    }

    @Test
    public void basicSuccessStatistics() {
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();
        userController.login(initialUserName, "u1", "u1");
        Map<String, Integer> statistics = userController.getDailyStatistics("u1", LocalDate.now()).getResult();
        Assert.assertEquals(3, statistics.get("Guest"), 0);
        Assert.assertEquals(0, statistics.get("Registered"), 0);
        Assert.assertEquals(0, statistics.get("Manager"), 0);
        Assert.assertEquals(0, statistics.get("Owner"), 0);
        Assert.assertEquals(2, statistics.get("Admin"), 0);
    }

    @Test
    public void failedFutureDatestatistics() {
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();
        userController.login(initialUserName, "u1", "u1");
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        Response<Map<String, Integer>> statisticsRes = userController.getDailyStatistics("u1", tomorrow);
        Assert.assertTrue(statisticsRes.isFailure());
    }
}
