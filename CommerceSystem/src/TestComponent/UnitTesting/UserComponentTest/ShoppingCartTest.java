package TestComponent.UnitTesting.UserComponentTest;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.ShoppingManager.StoreController;
import Server.Domain.UserManager.ShoppingCart;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ShoppingCartTest {

    private ShoppingCart cart = new ShoppingCart();
    private ProductDTO[] productsDTO;
    private static boolean setUpIsDone = false;

    @Before
    public void setUp(){
        if(setUpIsDone)
            return;

        Response<Integer> res1 = StoreController.getInstance().openStore("American Eagle", "Tal");
        Response<Integer> res2 = StoreController.getInstance().openStore("Renuar", "Yoni");

        this.productsDTO = new ProductDTO[]{
                new ProductDTO("TV", 1111, res1.getResult(), 1299.9, new LinkedList<>(),
                        new LinkedList<>(), new LinkedList<>(),0,0),
                new ProductDTO("Watch",2222, res1.getResult(), 600, new LinkedList<>(),
                        new LinkedList<>(), new LinkedList<>(),0,0),
                new ProductDTO("AirPods",3333, res2.getResult(), 799.9, new LinkedList<>(),
                        new LinkedList<>(), new LinkedList<>(),0,0),
                new ProductDTO("Watch2",4444, res1.getResult(), 600, new LinkedList<>(),
                        new LinkedList<>(), new LinkedList<>(),0,0)
        };

        StoreController.getInstance().addProductToStore(productsDTO[0], 10);
        StoreController.getInstance().addProductToStore(productsDTO[1], 10);
        StoreController.getInstance().addProductToStore(productsDTO[2], 10);
        StoreController.getInstance().addProductToStore(productsDTO[3], 10);

        setUpIsDone = true;
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
        Assert.assertEquals(2, baskets.size());

        for(Map<ProductDTO, Integer> basket: baskets.values()){
            for(Integer pBasket: basket.values()) {
                numProducts += pBasket;
            }
        }

        Assert.assertEquals(4, numProducts);
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
            Assert.assertTrue(res.getResult());
        }
        else{
            Assert.assertEquals(1, 2); // error
        }

        baskets = cart.getBaskets();
        for(Map<ProductDTO, Integer> basket: baskets.values()){
            for(Integer pBasket: basket.values()) {
                numProducts += pBasket;
            }
        }

        Assert.assertTrue(baskets.size() == 1 && numProducts == 3);
    }

    @Test
    public void removeAbsentProductTest(){
        Response<Boolean> res;

        res = cart.removeProduct(2, 78321); // no basket exist for this product
        Assert.assertTrue(res.isFailure());
    }

    @Test
    public void concurrencyTest() throws InterruptedException {
        int numberOfThreads1 = 100;
        int numberOfThreads2 = 50;
        Map<Integer, Map<ProductDTO, Integer>> baskets;

        CountDownLatch latch = new CountDownLatch(numberOfThreads1);

        ExecutorService service1 = Executors.newFixedThreadPool(numberOfThreads1);
        for (int i = 0; i < numberOfThreads1; i++) {
            service1.execute(() -> {
                cart.addProduct(0,1111);
                latch.countDown();
            });
        }

        ExecutorService service2 = Executors.newFixedThreadPool(numberOfThreads2);
        for (int i = 0; i < numberOfThreads2; i++) {
            service2.execute(() -> {
                cart.removeProduct(0,1111);
                latch.countDown();
            });
        }

        latch.await();
        baskets = cart.getBaskets();

        Assert.assertEquals(1, baskets.size());
        service1.shutdownNow();
        service2.shutdownNow();
    }
}
