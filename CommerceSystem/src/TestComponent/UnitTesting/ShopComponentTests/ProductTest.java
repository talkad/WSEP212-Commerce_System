package TestComponent.UnitTesting.ShopComponentTests;

import Server.Domain.CommonClasses.Rating;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.ProductDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductTest {

    private Product product;

    @BeforeEach
    public void setUp(){
        ProductDTO productDTO = new ProductDTO("Oreo", 1, 22.9, null, null, null);
        product = Product.createProduct(productDTO);
    }

    @Test
    public void simplePriceUpdateTest(){
        product.updatePrice(19.9);
        assertEquals(19.9, product.getPrice());
    }

    @Test
    public void complexPriceUpdateTest()  throws InterruptedException {
        int numberOfThreads = 100;
        double price = product.getPrice();
        CountDownLatch latch = new CountDownLatch(numberOfThreads*2);

        ExecutorService service1 = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service1.execute(() -> {
                product.updatePrice(price + 10);
                latch.countDown();
            });
        }

        ExecutorService service2 = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service2.execute(() -> {
                product.updatePrice(price + 35.2);
                latch.countDown();
            });
        }

        latch.await();
        assertTrue(product.getPrice() == price + 10 || product.getPrice() == price + 35.2);
    }

    @Test
    public void simpleAddRateTest(){
        product.addRating(Rating.HIGH);
        assertEquals(4, product.getRating());
    }

    @Test
    public void mediumAddRateTest(){
        for(int i=0; i < 20; i++)
            product.addRating(Rating.HIGH);

        assertEquals(4, product.getRating());

        for(int i=0; i < 10; i++)
            product.addRating(Rating.VERY_HIGH);

        assertEquals((double)130/30, product.getRating());

        for(int i=0; i < 5; i++)
            product.addRating(Rating.VERY_BAD);

        assertEquals((double)135/35, product.getRating());
    }

    @Test
    public void complexAddRateTest() throws InterruptedException {
        int numberOfThreads = 100;
        CountDownLatch latch = new CountDownLatch(numberOfThreads*2);

        ExecutorService service1 = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service1.execute(() -> {
                product.addRating(Rating.VERY_HIGH);
                latch.countDown();
            });
        }

        ExecutorService service2 = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            service2.execute(() -> {
                product.addRating(Rating.VERY_BAD);
                latch.countDown();
            });
        }

        latch.await();
        // This range is for the numerical floating error
        assertTrue(product.getRating() >= 2.99 && product.getRating() <= 3.001);
    }
}
