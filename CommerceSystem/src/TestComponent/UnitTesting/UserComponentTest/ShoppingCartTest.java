package TestComponent.UnitTesting.UserComponentTest;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.ShoppingManager.StoreController;
import Server.Domain.UserManager.ShoppingCart;
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
    private ProductDTO[] productsDTO;

    @BeforeEach
    public void setUp(){
        cart = new ShoppingCart();

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

    @Test
    public void addProductTest(){
        Map<Integer, Map<ProductDTO, Integer>> baskets;
        int numProducts = 0;

        for(Store store: StoreController.getInstance().getContent().getResult()){
            for(Product product: store.getInventory().getProducts()){
                cart.addProduct(product.getStoreID(), product.getProductID());
            }
        }

        baskets = cart.getBaskets();
        assertEquals(2, baskets.size());

        for(Map<ProductDTO, Integer> basket: baskets.values()){
            for(Integer pBasket: basket.values()) {
                numProducts += pBasket;
            }
        }

        assertEquals(4, numProducts);
    }

    @Test
    public void removeExistingProductTest(){
        Map<Integer, Map<ProductDTO, Integer>> baskets;
        Response<Boolean> res;
        int numProducts = 0;
        Product lastProduct = null;

        for(Store store: StoreController.getInstance().getContent().getResult()){
            for(Product product: store.getInventory().getProducts()){
                cart.addProduct(product.getStoreID(), product.getProductID());
                lastProduct = product;
            }
        }

        if(lastProduct != null) {
            res = cart.removeProduct(lastProduct.getStoreID(), lastProduct.getProductID());
            assertTrue(res.getResult());
        }
        else{
            assertEquals(1, 2); // error
        }

        baskets = cart.getBaskets();
        for(Map<ProductDTO, Integer> basket: baskets.values()){
            for(Integer pBasket: basket.values()) {
                numProducts += pBasket;
            }
        }

        assertTrue(baskets.size() == 1 && numProducts == 3);
    }

    @Test
    public void removeAbsentProductTest(){
        Response<Boolean> res;

        res = cart.removeProduct(2, 202); // no basket exist for this product
        assertTrue(res.isFailure());
    }

    @Test
    public void concurrencyTest() throws InterruptedException {
        int numProducts = 0;
        int numberOfThreads1 = 100;
        int numberOfThreads2 = 50;
        Map<Integer, Map<ProductDTO, Integer>> baskets;

        CountDownLatch latch = new CountDownLatch(numberOfThreads1 + numberOfThreads2);

        ExecutorService service1 = Executors.newFixedThreadPool(numberOfThreads1);
        for (int i = 0; i < numberOfThreads1; i++) {
            service1.execute(() -> {
                cart.addProduct(0,0);
                latch.countDown();
            });
        }

        ExecutorService service2 = Executors.newFixedThreadPool(numberOfThreads2);
        for (int i = 0; i < numberOfThreads2; i++) {
            service2.execute(() -> {
                cart.removeProduct(0,0);
                latch.countDown();
            });
        }

        latch.await();
        baskets = cart.getBaskets();

        for(Map<ProductDTO, Integer> basket: baskets.values()){
            for(Integer pBasket: basket.values()) {
                numProducts += pBasket;
            }
        }

        assertTrue(baskets.size() == 1 && numProducts == numberOfThreads1 - numberOfThreads2);
    }
}
