package TestComponent.UnitTesting.ShopComponentTests;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Inventory;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.StoreController;
import Server.Domain.UserManager.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InventoryTest {

    private Inventory inventory;
    private ProductDTO[] productsDTO;

    @BeforeEach
    public void setUp(){
        Response<Integer> res1 = StoreController.getInstance().openStore("American Eagle", "Tal");
        Response<Integer> res2 = StoreController.getInstance().openStore("Renuar", "Yoni");

        productsDTO = new ProductDTO[]{
                new ProductDTO("TV", res1.getResult(), 1299.9, null, null, null),
                new ProductDTO("Watch", res1.getResult(), 600, null, null, null),
                new ProductDTO("AirPods", res2.getResult(), 799.9, null, null, null),
                new ProductDTO("Watch2", res1.getResult(), 600, null, null, null)
        };

        StoreController.getInstance().openStore("American Eagle", "Yoni");

        StoreController.getInstance().addProductToStore(productsDTO[0], 10);
        StoreController.getInstance().addProductToStore(productsDTO[1], 10);
        StoreController.getInstance().addProductToStore(productsDTO[2], 10);
        StoreController.getInstance().addProductToStore(productsDTO[3], 10);
    }

//    @BeforeEach
//    public void setUp(){
//        inventory = new Inventory();
//        product = new Product(0, 0, "Oreo", 22.9, null, null);
//    }

    @Test
    public void addProductTest(){
        inventory.addProducts(productsDTO[0], 10);
        assertEquals(10, inventory.getProductAmount(0));

        inventory.addProducts(productsDTO[1], 5);
        assertEquals(15, inventory.getProductAmount(1));
    }

    @Test
    public void removeExistProductTest(){
        inventory.addProducts(productsDTO[0], 10);
        inventory.removeProducts(0, 5);
        assertEquals(5, inventory.getProductAmount(0));

        inventory.removeProducts(0, 5);
        assertEquals(0, inventory.getProductAmount(0));

        inventory.removeProducts(0, 5);
        assertEquals(0, inventory.getProductAmount(0));
    }

    @Test
    public void removeAbsentProductTest(){
        Response<Boolean> res;
        res = inventory.removeProducts(10, 5);
        assertTrue(res.isFailure());
    }

    @Test
    public void concurrencyTest() throws InterruptedException {
        int numberOfThreads = 100;

        CountDownLatch latch = new CountDownLatch(numberOfThreads * 2);

        ExecutorService service1 = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service1.execute(() -> {
                inventory.addProducts(productsDTO[0], 10);
                latch.countDown();
            });
        }

        ExecutorService service2 = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service2.execute(() -> {
                inventory.removeProducts(0, 5);
                latch.countDown();
            });
        }

        latch.await();
        assertEquals(500, inventory.getProductAmount(0));
        assertEquals(1, inventory.getProducts().size());
    }
}
