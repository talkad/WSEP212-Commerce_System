package TestComponent.UnitTesting.UserComponentTest;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.ShoppingManager.StoreController;
import Server.Domain.UserManager.ShoppingCart;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ShoppingCartTest {

    @Test
    public void addProductTest(){
        ShoppingCart cart = new ShoppingCart();


        Response<Integer> res1 = StoreController.getInstance().openStore("American Eagle", "Tal");
        Response<Integer> res2 = StoreController.getInstance().openStore("Renuar", "Yoni");

        ProductDTO[] productsDTO = new ProductDTO[]{
                new ProductDTO("TV", 1111, res1.getResult(), 1299.9, new LinkedList<>(),
                        new LinkedList<>(), new LinkedList<>(),0,0),
                new ProductDTO("Watch",2222, res1.getResult(), 600, new LinkedList<>(),
                        new LinkedList<>(), new LinkedList<>(),0,0),
                new ProductDTO("AirPods",3333, res2.getResult(), 799.9, new LinkedList<>(),
                        new LinkedList<>(), new LinkedList<>(),0,0),
                new ProductDTO("Watch2",4444, res1.getResult(), 600, new LinkedList<>(),
                        new LinkedList<>(), new LinkedList<>(),0,0)
        };

        Store store1 = StoreController.getInstance().getStoreById(res1.getResult());
        Store store2 = StoreController.getInstance().getStoreById(res2.getResult());

        store1.addProduct(productsDTO[0], 10);
        store1.addProduct(productsDTO[1], 10);
        store1.addProduct(productsDTO[3], 10);
        store2.addProduct(productsDTO[2], 10);

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
        ShoppingCart cart = new ShoppingCart();

        Response<Integer> res1 = StoreController.getInstance().openStore("American Eagle", "Tal");
        Response<Integer> res2 = StoreController.getInstance().openStore("Renuar", "Yoni");

        ProductDTO[] productsDTO = new ProductDTO[]{
                new ProductDTO("TV", 73725, res1.getResult(), 1299.9, new LinkedList<>(),
                        new LinkedList<>(), new LinkedList<>(),0,0),
                new ProductDTO("Watch",12372, res1.getResult(), 600, new LinkedList<>(),
                        new LinkedList<>(), new LinkedList<>(),0,0),
                new ProductDTO("AirPods",564521, res2.getResult(), 799.9, new LinkedList<>(),
                        new LinkedList<>(), new LinkedList<>(),0,0),
                new ProductDTO("Watch2",345387, res1.getResult(), 600, new LinkedList<>(),
                        new LinkedList<>(), new LinkedList<>(),0,0)
        };

        Store store1 = StoreController.getInstance().getStoreById(res1.getResult());
        Store store2 = StoreController.getInstance().getStoreById(res2.getResult());

        store1.addProduct(productsDTO[0], 50);
        store1.addProduct(productsDTO[1], 50);
        store1.addProduct(productsDTO[3], 50);
        store2.addProduct(productsDTO[2], 50);

        Map<Integer, Map<ProductDTO, Integer>> baskets;
        Response<Boolean> res;
        int numProducts = 0;

        cart.addProduct(productsDTO[0].getStoreID(), productsDTO[0].getProductID());
        cart.addProduct(productsDTO[1].getStoreID(), productsDTO[1].getProductID());
        cart.addProduct(productsDTO[2].getStoreID(), productsDTO[2].getProductID());
        cart.addProduct(productsDTO[3].getStoreID(), productsDTO[3].getProductID());

        res = cart.removeProduct(productsDTO[2].getStoreID(), productsDTO[2].getProductID());
        Assert.assertTrue(res.getResult());

        baskets = cart.getBaskets();
        for(Map<ProductDTO, Integer> basket: baskets.values()){
            for(Integer pBasket: basket.values()) {
                numProducts += pBasket;
            }
        }

        Assert.assertEquals(1, baskets.size());
        Assert.assertEquals(3, numProducts);
    }

    @Test
    public void removeAbsentProductTest(){
        ShoppingCart cart = new ShoppingCart();
        Response<Boolean> res;

        res = cart.removeProduct(2, 78321); // no basket exist for this product
        Assert.assertTrue(res.isFailure());
    }

    @Test
    public void concurrencyTest() throws InterruptedException {
        ShoppingCart cart = new ShoppingCart();

        Response<Integer> res1 = StoreController.getInstance().openStore("American Eagle", "Tal");

        ProductDTO productDTO = new ProductDTO("TV", 1111, res1.getResult(), 1299.9, new LinkedList<>(),
                        new LinkedList<>(), new LinkedList<>(),0,0);


        Store store1 = StoreController.getInstance().getStoreById(res1.getResult());

        store1.addProduct(productDTO, 10);


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
