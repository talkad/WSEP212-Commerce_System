package TestComponent.UnitTesting.ShopComponentTests;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Inventory;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.StoreController;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InventoryTest {

    @Test
    public void addProductTest(){
        Response<Integer> res1 = StoreController.getInstance().openStore("American Eagle", "Tal");
        Inventory inventory = new Inventory();
        ProductClientDTO productDTO1 = new ProductClientDTO("TV", 456, res1.getResult(), 1299.9, null, null, null, 0, 0);
        ProductClientDTO productDTO2 = new ProductClientDTO("Watch", 756, res1.getResult(), 600, null, null, null, 0, 0);

        inventory.addProducts(productDTO1, 10);
        Assert.assertEquals(10, inventory.getProductAmount(456), 0);

        inventory.addProducts(productDTO2, 5);
        Assert.assertEquals(5, inventory.getProductAmount(756), 0);
    }

    @Test
    public void removeExistProductTest(){
        Response<Integer> res1 = StoreController.getInstance().openStore("American Eagle", "Tal");
        Inventory inventory = new Inventory();

        ProductClientDTO productDTO1 = new ProductClientDTO("TV", 456, res1.getResult(), 1299.9, null, null, null, 0, 0);

        inventory.addProducts(productDTO1, 10);
        inventory.removeProducts(456, 5);
        Assert.assertEquals(5, inventory.getProductAmount(456), 0);

        inventory.removeProducts(456, 5);
        Assert.assertEquals(0, inventory.getProductAmount(456), 0);

        inventory.removeProducts(456, 5);
        Assert.assertEquals(0, inventory.getProductAmount(456), 0);
    }

    @Test
    public void removeAbsentProductTest(){
        Inventory inventory = new Inventory();

        Response<Boolean> res;
        res = inventory.removeProducts(4528, 5);
        Assert.assertTrue(res.isFailure());
    }

    @Test
    public void concurrencyTest() throws InterruptedException {
        Response<Integer> res1 = StoreController.getInstance().openStore("American Eagle", "Tal");
        Inventory inventory = new Inventory();

        ProductClientDTO productDTO1 = new ProductClientDTO("TV", 456, res1.getResult(), 1299.9, null, null, null, 0, 0);
        int numberOfThreads = 100;

        CountDownLatch latch1 = new CountDownLatch(numberOfThreads);

        ExecutorService service1 = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service1.execute(() -> {
                inventory.addProducts(productDTO1, 10);
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
