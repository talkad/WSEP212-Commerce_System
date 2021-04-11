package TestComponent.UnitTesting.ShopComponentTests;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InventoryTest {

    private  Response<Integer> res1 = StoreController.getInstance().openStore("American Eagle", "Tal");
    private  Response<Integer> res2 = StoreController.getInstance().openStore("Renuar", "Yoni");
    private  Inventory inventory = new Inventory();
    private  ProductDTO[] productsDTO = new ProductDTO[]{
            new ProductDTO("TV", 456, res1.getResult(), 1299.9, null, null, null, 0, 0),
            new ProductDTO("Watch", 756, res1.getResult(), 600, null, null, null, 0, 0),
            new ProductDTO("AirPods", 816, res2.getResult(), 799.9, null, null, null, 0, 0),
            new ProductDTO("Watch2", 159, res1.getResult(), 600, null, null, null, 0, 0)
    };

    @Before
    public void setUp(){
        // do nothing
    }

    @Test
    public void addProductTest(){
        inventory.addProducts(productsDTO[0], 10);
        Assert.assertEquals(10, inventory.getProductAmount(456), 0);

        inventory.addProducts(productsDTO[1], 5);
        Assert.assertEquals(5, inventory.getProductAmount(756), 0);
    }

    @Test
    public void removeExistProductTest(){
        inventory.addProducts(productsDTO[0], 10);
        inventory.removeProducts(456, 5);
        Assert.assertEquals(5, inventory.getProductAmount(456), 0);

        inventory.removeProducts(456, 5);
        Assert.assertEquals(0, inventory.getProductAmount(456), 0);

        inventory.removeProducts(456, 5);
        Assert.assertEquals(0, inventory.getProductAmount(456), 0);
    }

    @Test
    public void removeAbsentProductTest(){
        Response<Boolean> res;
        res = inventory.removeProducts(4528, 5);
        Assert.assertTrue(res.isFailure());
    }

    @Test
    public void concurrencyTest() throws InterruptedException {
        int numberOfThreads = 100;

        CountDownLatch latch1 = new CountDownLatch(numberOfThreads);

        ExecutorService service1 = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service1.execute(() -> {
                inventory.addProducts(productsDTO[0], 10);
                latch1.countDown();
            });
        }
        latch1.await();


        CountDownLatch latch2 = new CountDownLatch(numberOfThreads);

        ExecutorService service2 = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service2.execute(() -> {
                inventory.removeProducts(456, 5);
                latch2.countDown();
            });
        }

        latch2.await();

        Assert.assertTrue(inventory.getProducts().size() == 1 && inventory.getProductAmount(456) == 500);
        service1.shutdownNow();
        service2.shutdownNow();
    }

}
