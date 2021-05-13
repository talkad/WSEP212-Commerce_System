package TestComponent.IntegrationTestings.ExternalComponentsTests;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.ProductSupplyAdapter;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeliverySystemTests {
    ProductSupplyAdapter supplier;
    ProductDTO productDTO;
    Map<Integer,Map<ProductDTO, Integer>> toDeliver;

    @Before
    public void setUp(){
        supplier = ProductSupplyAdapter.getInstance();
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
}
