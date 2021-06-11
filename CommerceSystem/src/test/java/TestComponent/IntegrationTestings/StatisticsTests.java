package TestComponent.IntegrationTestings;


import Server.DAL.DALService;
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

public class StatisticsTests {
    @Before
    public void init(){
        CommerceSystem.getInstance().configInit("successconfigfile.json");
        DALService.getInstance().startDB();
        DALService.getInstance().resetDatabase();
        PaymentSystemAdapter.getInstance().setMockFlag();
        ProductSupplyAdapter.getInstance().setMockFlag();
    }

    @Test
    public void basicSuccessStatistics() {
        UserController userController = UserController.getInstance();
        String initialUserName = userController.addGuest().getResult();
        userController.login(initialUserName, "a1", "a1");
        List<String> statistics = userController.getDailyStatistics("a1", LocalDate.now()).getResult();
        Assert.assertEquals("Guest: 1", statistics.get(0));
        Assert.assertEquals("Registered: 0", statistics.get(1));
        Assert.assertEquals("Manager: 0", statistics.get(2));
        Assert.assertEquals("Owner: 0"  , statistics.get(3));
        Assert.assertEquals("Admin: 1", statistics.get(4));
    }

    @Test
    public void failedFutureDatestatistics() {
        CommerceService commerceService = CommerceService.getInstance();
        UserController userController = UserController.getInstance();
        String initialUserName = commerceService.addGuest().getResult();
        userController.login(initialUserName, "a1", "a1");
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        Response<List<String>> statisticsRes = userController.getDailyStatistics("a1", tomorrow);
        Assert.assertTrue(statisticsRes.isFailure());
    }
}
