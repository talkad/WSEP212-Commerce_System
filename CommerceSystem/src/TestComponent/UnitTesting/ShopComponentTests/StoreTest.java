package TestComponent.UnitTesting.ShopComponentTests;

import Server.Domain.ShoppingManager.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StoreTest {

    private Store store1;
    private ProductDTO product1;
    private ProductDTO product2;
    private ProductDTO product3;

    @Before
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
        Assert.assertEquals(9, pAmount, 0);
    }

    @Test
    public void addExistingProductTest1(){
        int inventorySize = 0;
        store1.addProduct(product1, 5);
        store1.addProduct(product1, 10);
        for(Product product : store1.getInventory().getProducts())
            ++inventorySize;
        Assert.assertEquals(2, inventorySize, 0);
    }

    @Test
    public void addExistingProductTest2(){
        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        Assert.assertEquals(15, store1.getInventory().getProductAmount(product3.getProductID()), 0);
    }

    @Test
    public void removeExistingProductLegalTest(){
        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        store1.removeProduct(product3.getProductID(), 3);
        Assert.assertEquals(12, store1.getInventory().getProductAmount(product3.getProductID()), 0);
    }

    @Test
    public void removeExistingProductLegalTest2(){
        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        store1.removeProduct(product3.getProductID(), 15);
        Assert.assertEquals(0, store1.getInventory().getProductAmount(product3.getProductID()), 0);
    }

    @Test
    public void removeExistingProductIllegalTest(){
        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        store1.removeProduct(product3.getProductID(), 20);
        Assert.assertTrue(store1.removeProduct(product3.getProductID(), 20).isFailure());
    }
}
