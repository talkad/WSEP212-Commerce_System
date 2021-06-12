package TestComponent.IntegrationTestings.ExternalComponentsTests;

import Server.DAL.DALControllers.DALService;
import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.UserManager.CommerceSystem;
import Server.Domain.UserManager.ExternalSystemsAdapters.ProductSupplyAdapter;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class DeliverySystemTests {
    ProductSupplyAdapter supplier;
    ProductClientDTO productDTO;
    Map<Integer,Map<ProductClientDTO, Integer>> toDeliver;

    @Before
    public void setUp(){
        supplier = ProductSupplyAdapter.getInstance();
        CommerceSystem.getInstance().configInit("successconfigfile.json");
        DALService.getInstance().startDB();
        DALService.getInstance().resetDatabase();
    }

    @Test
    public void externalDeliveryTestPass(){
        Response<Integer> res;

        SupplyDetails details = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        res = supplier.supply(details);
        Assert.assertTrue(!res.isFailure() && res.getResult() >= 10000 && res.getResult() <= 100000);
    }

//    @Test // the external systems always respond with positive result
//    public void externalDeliveryTestFail(){
//        Response<Integer> res;
//
//        SupplyDetails details = new SupplyDetails("", "", "", "", "0");
//
//        res = supplier.supply(details);
//        Assert.assertTrue(res.isFailure());
//    }

    @Test
    public void externalCancelSupplyTestPass(){
        Response<Integer> res;

        res = supplier.cancelSupply("4568");

        Assert.assertTrue(!res.isFailure() && res.getResult() == 1);
    }

    // run this test offline
    @Test
    public void offlineTestFailure(){
        Response<Integer> res;

        res = supplier.cancelSupply("4568");

        Assert.assertTrue(res.getErrMsg().contains("supply cancellation transaction failed due to error in handshake (CRITICAL)"));
    }
}
