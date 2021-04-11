package TestComponent.UnitTesting.ShopComponentTests;

import Server.Domain.CommonClasses.Rating;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.ProductDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductTest {

    private Product product;

    @Before
    public void setUp(){
        ProductDTO productDTO = new ProductDTO("Oreo", 1, 22.9, null, null, null);
        product = Product.createProduct(productDTO);
    }

    @Test
    public void simplePriceUpdateTest(){
        product.updatePrice(19.9);
        Assert.assertEquals(19.9, product.getPrice(), 0.0);
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
        Assert.assertTrue(product.getPrice() == price + 10 || product.getPrice() == price + 35.2);
        service1.shutdownNow();
        service2.shutdownNow();
    }

    @Test
    public void simpleAddRateTest(){
        product.addRating(Rating.HIGH);
        Assert.assertEquals(4, product.getRating(), 0);
    }

    @Test
    public void mediumAddRateTest(){
        for(int i=0; i < 20; i++)
            product.addRating(Rating.HIGH);

        Assert.assertEquals(4, product.getRating(), 0.0);

        for(int i=0; i < 10; i++)
            product.addRating(Rating.VERY_HIGH);

        Assert.assertEquals((double)130/30, product.getRating(), 0);

        for(int i=0; i < 5; i++)
            product.addRating(Rating.VERY_BAD);

        Assert.assertEquals((double)135/35, product.getRating(), 0);
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
        Assert.assertEquals(3, product.getRating(), 0.001);
        service1.shutdownNow();
        service2.shutdownNow();
    }
}
