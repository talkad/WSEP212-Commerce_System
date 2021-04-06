package TestComponent.UnitTesting.ShopComponentTests;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StoreControllerTest {
    private StoreController storeController;
    private ProductDTO product1;
    private ProductDTO product2;
    private  Store store;


    @BeforeEach
    public void setUp(){
        storeController = StoreController.getInstance();
        product1 = new ProductDTO("TV", 0, 1299.9, null, null, null);
        product2 = new ProductDTO("AirPods", 0, 799.9, List.of("Apple", "Headphones"), List.of("#Expensive", "#Swag"), null);
        Response<Integer> res = storeController.openStore("castro", "shaked");
        store = storeController.getStoreById(res.getResult());
        store.addProduct(product1, 5);
        store.addProduct(product2, 8);
    }

    @Test
    public void addStoreTest(){
        assertFalse(storeController.openStore("zara", "aviad").isFailure());
    }

    @Test
    public void searchByProductNameTest1(){
        assertTrue(storeController.searchByProductName("TV").getResult().contains(product1));
    }

    @Test
    public void searchByProductNameTest2(){
        assertTrue(storeController.searchByProductName("AirPods").getResult().contains(product2));
    }

    @Test
    public void searchByCategoryTest1(){
        assertTrue(storeController.searchByCategory(null).getResult().isEmpty());
    }

    @Test
    public void searchByCategoryTest2(){
        assertTrue(storeController.searchByCategory("Apple").getResult().contains(product2));
    }

    @Test
    public void searchByKeyWordTest1(){
        assertTrue(storeController.searchByKeyWord(null).getResult().isEmpty());
    }

    @Test
    public void searchByKeyWordTest2(){
        assertTrue(storeController.searchByKeyWord("#Swag").getResult().contains(product2));
    }

    @Test
    public void getStoreByIdTest(){
        Store store2 = storeController.getStoreById(0);
        assertSame(store, store2);
    }
}
