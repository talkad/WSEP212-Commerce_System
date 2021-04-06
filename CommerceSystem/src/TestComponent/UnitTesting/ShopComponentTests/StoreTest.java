package TestComponent.UnitTesting.ShopComponentTests;

import Server.Domain.ShoppingManager.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StoreTest {

    private Store store1;
    private ProductDTO product1;
    private ProductDTO product2;
    private ProductDTO product3;

    @BeforeEach
    public void setUp(){
        store1 = new Store(0, "h&m",  "talkad", new DiscountPolicy(1), new PurchasePolicy(1));
        product1 = new ProductDTO( "TV", 0, 1299.9  , null , null, null);
        product2 = new ProductDTO("AirPods", 0, 799.9, null , null, null);
        product3 = new ProductDTO("Watch", 999, 0, 799.9, null, null, null, 0, 0);
    }

    @Test
    public void addProductsLegalTest(){
        int pAmount = 0;
        store1.addProduct(product1, 5);
        store1.addProduct(product2, 4);
        for(Product product : store1.getInventory().getProducts())
            pAmount += store1.getInventory().getProductAmount(product.getProductID());
        assertEquals(9, pAmount);
    }

    @Test
    public void addExistingProductTest1(){
        int inventorySize = 0;
        store1.addProduct(product1, 5);
        store1.addProduct(product1, 10);
        for(Product product : store1.getInventory().getProducts())
            ++inventorySize;
        assertEquals(2, inventorySize);
    }

    @Test
    public void addExistingProductTest2(){
        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        assertEquals(15, store1.getInventory().getProductAmount(product3.getProductID()));
    }

    @Test
    public void removeExistingProductLegalTest(){
        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        store1.removeProduct(product3.getProductID(), 3);
        assertEquals(12, store1.getInventory().getProductAmount(product3.getProductID()));
    }

    @Test
    public void removeExistingProductLegalTest2(){
        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        store1.removeProduct(product3.getProductID(), 15);
        assertEquals(0, store1.getInventory().getProductAmount(product3.getProductID()));
    }

    @Test
    public void removeExistingProductIllegalTest(){
        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        store1.removeProduct(product3.getProductID(), 20);
        assertTrue(store1.removeProduct(product3.getProductID(), 20).isFailure());

    }
}
