package Domain.ShoppingManager.UnitTest;

import Domain.CommonClasses.Response;
import Domain.ShoppingManager.Inventory;
import Domain.ShoppingManager.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InventoryTest {

    private Inventory inventory;
    private Product product;

    @BeforeEach
    public void setUp(){
        inventory = new Inventory();
        product = new Product(0, 0, "Oreo", 22.9, null, null);
    }

    @Test
    public void addProductTest(){
        inventory.addProducts(product, 10);
        assertEquals(10, inventory.getProductAmount(0));

        inventory.addProducts(product, 5);
        assertEquals(15, inventory.getProductAmount(0));
    }

    @Test
    public void removeExistProductTest(){
        inventory.addProducts(product, 10);
        inventory.removeProducts(product, 5);
        assertEquals(5, inventory.getProductAmount(0));

        inventory.removeProducts(product, 5);
        assertEquals(0, inventory.getProductAmount(0));

        inventory.removeProducts(product, 5);
        assertEquals(0, inventory.getProductAmount(0));
    }

    @Test
    public void removeAbsentProductTest(){
        Product product2;
        Response<Boolean> res;

        product2 = new Product(1,0,"T-Shirt", 25.9, null, null);
        res = inventory.removeProducts(product2, 5);
        assertTrue(res.isFailure());
    }

    @Test
    public void concurrencyTest() throws InterruptedException {
        int numberOfThreads = 100;

        CountDownLatch latch = new CountDownLatch(numberOfThreads * 2);

        ExecutorService service1 = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service1.execute(() -> {
                inventory.addProducts(product, 10);
                latch.countDown();
            });
        }

        ExecutorService service2 = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service2.execute(() -> {
                inventory.removeProducts(product, 5);
                latch.countDown();
            });
        }

        latch.await();
        assertEquals(500, inventory.getProductAmount(0));
        assertEquals(1, inventory.getInventory().size());
    }
}
