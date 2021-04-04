package TestComponent.UnitTesting.UserComponentTest;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.ShoppingManager.StoreController;
import Server.Domain.UserManager.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShoppingCartTest {

    private ShoppingCart cart;
    private Product[] products;
    private ProductDTO[] productsDTO;

    @BeforeEach
    public void setUp(){
        cart = new ShoppingCart();

        productsDTO = new ProductDTO[]{
                new ProductDTO("TV", 10, 1299.9, null, null, null),
                new ProductDTO("Watch", 10, 600, null, null, null),
                new ProductDTO("AirPods", 15, 799.9, null, null, null),
                new ProductDTO("Watch2", 10, 600, null, null, null)
        };

        products = new Product[]{
                Product.createProduct(productsDTO[0]),
                Product.createProduct(productsDTO[1]),
                Product.createProduct(productsDTO[2]),
                Product.createProduct(productsDTO[3])
        };

        StoreController.getInstance().addProductToStore(productsDTO[0], 10);
        StoreController.getInstance().addProductToStore(productsDTO[1], 10);
        StoreController.getInstance().addProductToStore(productsDTO[2], 10);
        StoreController.getInstance().addProductToStore(productsDTO[3], 10);

    }

    @Test
    public void addProductTest(){
        Map<Integer, Map<Product, Integer>> baskets;
        int numProducts = 0;

        List<Store> stores = StoreController.getInstance().getContent();

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
