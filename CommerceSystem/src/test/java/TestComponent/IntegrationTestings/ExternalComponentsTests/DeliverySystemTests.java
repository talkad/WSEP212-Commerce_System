package TestComponent.IntegrationTestings.ExternalComponentsTests;

import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.UserManager.ProductSupplyAdapter;
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
        toDeliver = new ConcurrentHashMap<>();
        productDTO = new ProductDTO("chocolate", 12, 12, 33, new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), 33, 33);
        Map<ProductDTO,Integer> map1 = new ConcurrentHashMap<>();
        map1.put(productDTO, 1);
        toDeliver.put(2, map1);
        supplier = ProductSupplyAdapter.getInstance();
    }

    @Test
    public void externalDeliveryTestPass(){
        boolean b = supplier.canDeliver("Israel, Beer Sheba",toDeliver);
        Assert.assertTrue(b);
    }

    @Test
    public void externalDeliveryTestFail(){
        boolean b = supplier.canDeliver(null, toDeliver);
        Assert.assertFalse(b);
    }
}
