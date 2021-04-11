package TestComponent.UnitTesting.ShopComponentTests;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

public class StoreControllerTest {
    private static StoreController storeController;
    private static ProductDTO product1;
    private static ProductDTO product2;
    private static Store store;

    private static boolean initialized = false;

    @Before
    public void setUp(){
        if(!initialized) {
            storeController = StoreController.getInstance();
            product1 = new ProductDTO("TV", 0, 1299.9, null, null, null);
            product2 = new ProductDTO("AirPods", 0, 799.9, List.of("Apple", "Headphones"), List.of("#Expensive", "#Swag"), null);
            Response<Integer> res = storeController.openStore("castro", "shaked");
            store = storeController.getStoreById(res.getResult());
            store.addProduct(product1, 5);
            store.addProduct(product2, 8);

            initialized = true;
        }
    }

    @Test
    public void addStoreTest(){
        Assert.assertFalse(storeController.openStore("zara", "aviad").isFailure());
    }

    @Test
    public void searchByProductNameTest1(){
        boolean result = false;
        List<ProductDTO> products = storeController.searchByProductName("TV").getResult();

        for(ProductDTO productDTO: products)
            if(productDTO.getProductID() == 0)
                result = true;

        Assert.assertTrue(result);
    }

    @Test
    public void searchByProductNameTest2(){
        boolean result = false;
        List<ProductDTO> products = storeController.searchByProductName("AirPods").getResult();

        for(ProductDTO productDTO: products)
            if(productDTO.getProductID() == 1)
                result = true;

        Assert.assertTrue(result);
    }

    @Test
    public void searchByCategoryTest1(){
        Assert.assertTrue(storeController.searchByCategory(null).getResult().isEmpty());
    }

    @Test
    public void searchByCategoryTest2(){
        boolean result = false;
        List<ProductDTO> products = storeController.searchByCategory("Apple").getResult();

        for(ProductDTO productDTO: products)
            if(productDTO.getProductID() == 1)
                result = true;

        Assert.assertTrue(result);
    }

    @Test
    public void searchByKeyWordTest1(){
        Assert.assertTrue(storeController.searchByKeyWord(null).getResult().isEmpty());
    }

    @Test
    public void searchByKeyWordTest2(){
        boolean result = false;
        List<ProductDTO> products = storeController.searchByKeyWord("#Swag").getResult();

        for(ProductDTO productDTO: products)
            if(productDTO.getProductID() == 1)
                result = true;

        Assert.assertTrue(result);
    }

    @Test
    public void getStoreByIdTest(){
        Store store2 = storeController.getStoreById(0);
        Assert.assertSame(store, store2);
    }
}