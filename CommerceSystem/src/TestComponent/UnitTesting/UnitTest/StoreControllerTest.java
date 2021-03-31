package TestComponent.UnitTesting.UnitTest;

import Server.Domain.ShoppingManager.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StoreControllerTest {
    private StoreController storeController;
    private Store store2;
    private Product product1;
    private Product product2;


    @BeforeEach
    public void setUp(){
        storeController = StoreController.getInstance();
        product1 = new Product(0, 10, "TV", 1299.9, null, null);
        product2 = new Product(1, 9, "AirPods", 799.9, List.of("Apple", "Headphones"), List.of("#Expensive", "#Swag"));
        store2 = new Store(1, "castro", new DiscountPolicy(2), new PurchasePolicy(2));
        store2.addProduct(product1, 5);
        store2.addProduct(product2, 8);
        storeController.addStore(store2);

    }

    @Test
    public void addStoreTest(){
        assertFalse(storeController.addStore(2, "zara", new DiscountPolicy(2), new PurchasePolicy(2)).isFailure());
    }

    @Test
    public void searchByProductNameTest1(){
        assertTrue(storeController.searchByProductName("TV").contains(product1));
    }

    @Test
    public void searchByProductNameTest2(){
        assertTrue(storeController.searchByProductName("AirPods").contains(product2));
    }

    @Test
    public void searchByCategoryTest1(){
        assertTrue(storeController.searchByCategory(null).isEmpty());
    }

    @Test
    public void searchByCategoryTest2(){
        assertTrue(storeController.searchByCategory("Apple").contains(product2));
    }

    @Test
    public void searchByKeyWordTest1(){
        assertTrue(storeController.searchByKeyWord(null).isEmpty());
    }

    @Test
    public void searchByKeyWordTest2(){
        assertTrue(storeController.searchByKeyWord("#Swag").contains(product2));
    }

    @Test
    public void getStoreByIdTest(){
        Store s = storeController.getStoreById(1);
        assertSame(s, store2);
    }
}
