package Domain.UserManager.UnitTest;

import Domain.CommonClasses.Response;
import Domain.ShoppingManager.Product;
import Domain.UserManager.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShoppingCartTest {

    private ShoppingCart cart;
    private Product[] products;

    @BeforeEach
    public void setUp(){
        cart = new ShoppingCart();

        products = new Product[]{
                new Product(0, 10, "TV", 1299.9, null, null),
                new Product(1, 10, "Watch", 600, null, null),
                new Product(2, 15, "AirPods", 799.9, null, null),
                new Product(3, 10, "Watch2", 600, null, null)
        };

    }

    @Test
    public void addProductTest(){
        Map<Integer, Map<Product, Integer>> baskets;
        int numProducts = 0;

        cart.addProduct(products[0]);
        cart.addProduct(products[0]);
        cart.addProduct(products[1]);
        cart.addProduct(products[2]);

        baskets = cart.getBaskets();
        assertEquals(2, baskets.size());


        for(Map<Product, Integer> basket: baskets.values()){
            for(Integer pBasket: basket.values()) {
                numProducts += pBasket;
            }
        }

        assertEquals(4, numProducts);
    }

    @Test
    public void removeExistingProductTest(){
        Map<Integer, Map<Product, Integer>> baskets;
        Response<Boolean> res;
        int numProducts = 0;

        cart.addProduct(products[0]);
        cart.addProduct(products[1]);

        res = cart.removeProduct(products[1]);
        assertTrue(res.getResult());

        baskets = cart.getBaskets();
        for(Map<Product, Integer> basket: baskets.values()){
            for(Integer pBasket: basket.values()) {
                numProducts += pBasket;
            }
        }

        assertTrue(baskets.size() == 1 && numProducts == 1);
    }

    @Test
    public void removeAbsentProductTest(){
        Response<Boolean> res;

        res = cart.removeProduct(products[2]); // no basket exist for this product
        assertTrue(res.isFailure());

        res = cart.removeProduct(products[3]); // does not belong to the relevant basket
        assertTrue(res.isFailure());
    }

    @Test
    public void concurrencyTest() throws InterruptedException {
        int numProducts = 0;
        int numberOfThreads1 = 100;
        int numberOfThreads2 = 50;
        Map<Integer, Map<Product, Integer>> baskets;

        CountDownLatch latch = new CountDownLatch(numberOfThreads1 + numberOfThreads2);

        ExecutorService service1 = Executors.newFixedThreadPool(numberOfThreads1);
        for (int i = 0; i < numberOfThreads1; i++) {
            service1.execute(() -> {
                cart.addProduct(products[3]);
                latch.countDown();
            });
        }

        ExecutorService service2 = Executors.newFixedThreadPool(numberOfThreads2);
        for (int i = 0; i < numberOfThreads2; i++) {
            service2.execute(() -> {
                cart.removeProduct(products[3]);
                latch.countDown();
            });
        }

        latch.await();
        baskets = cart.getBaskets();

        for(Map<Product, Integer> basket: baskets.values()){
            for(Integer pBasket: basket.values()) {
                numProducts += pBasket;
            }
        }

        assertTrue(baskets.size() == 1 && numProducts == numberOfThreads1 - numberOfThreads2);
    }
}
