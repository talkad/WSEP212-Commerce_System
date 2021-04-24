package TestComponent.UnitTesting.ShopComponentTests;

import Server.Domain.ShoppingManager.*;
import org.junit.Assert;
import org.junit.Test;

public class StoreTest {

    @Test
    public void addProductsTestSuccess(){
       Store store1 = new Store(0, "h&m",  "talkad", new DiscountPolicy(1), new PurchasePolicy(1));
       ProductDTO product1 = new ProductDTO( "TV", 0, 1299.9  , null , null);
       ProductDTO product2 = new ProductDTO("AirPods", 0, 799.9, null , null);

        int pAmount = 0;
        store1.addProduct(product1, 5);
        store1.addProduct(product2, 4);
        for(Product product : store1.getInventory().getProducts())
            pAmount += store1.getInventory().getProductAmount(product.getProductID());
        Assert.assertEquals(9, pAmount);
    }

    @Test
    public void addProductsTestFailure(){
        Store store1 = new Store(0, "h&m",  "talkad", new DiscountPolicy(1), new PurchasePolicy(1));
        ProductDTO product1 = new ProductDTO( "TV", 0, 1299.9  , null , null);

        int pAmount = 0;
        store1.addProduct(product1, -3);

        for(Product product : store1.getInventory().getProducts())
            pAmount += store1.getInventory().getProductAmount(product.getProductID());
        Assert.assertEquals(0, pAmount);
    }

    @Test
    public void addExistingProductTestSuccess1(){
        Store store1 = new Store(0, "h&m",  "talkad", new DiscountPolicy(1), new PurchasePolicy(1));
        ProductDTO product1 = new ProductDTO( "TV", 997,0, 1299.9  , null , null, null, 0 ,0);

        int inventorySize = 0;
        store1.addProduct(product1, 5);
        store1.addProduct(product1, 10);

        for(Product p : store1.getInventory().getProducts())
            ++inventorySize;
        Assert.assertEquals(1, inventorySize);
    }

    @Test
    public void addExistingProductTestSuccess2(){
        Store store1 = new Store(0, "h&m",  "talkad", new DiscountPolicy(1), new PurchasePolicy(1));
        ProductDTO product3 = new ProductDTO("Watch", 999, 0, 799.9, null, null, null, 0, 0);

        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        Assert.assertEquals(15, store1.getInventory().getProductAmount(product3.getProductID()));
    }

    @Test
    public void removeExistingProductTestSuccess(){
        Store store1 = new Store(0, "h&m",  "talkad", new DiscountPolicy(1), new PurchasePolicy(1));
        ProductDTO product3 = new ProductDTO("Watch", 999, 0, 799.9, null, null, null, 0, 0);

        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        store1.removeProduct(product3.getProductID(), 3);
        Assert.assertEquals(12, store1.getInventory().getProductAmount(product3.getProductID()));
    }

    @Test
    public void removeExistingProductTestSuccess2(){
        Store store1 = new Store(0, "h&m",  "talkad", new DiscountPolicy(1), new PurchasePolicy(1));
        ProductDTO product3 = new ProductDTO("Watch", 999, 0, 799.9, null, null, null, 0, 0);

        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        store1.removeProduct(product3.getProductID(), 15);
        Assert.assertEquals(0, store1.getInventory().getProductAmount(product3.getProductID()));
    }

    @Test
    public void removeExistingProductTestFailure(){
        Store store1 = new Store(0, "h&m",  "talkad", new DiscountPolicy(1), new PurchasePolicy(1));
        ProductDTO product3 = new ProductDTO("Watch", 999, 0, 799.9, null, null, null, 0, 0);

        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        store1.removeProduct(product3.getProductID(), 20);
        Assert.assertTrue(store1.removeProduct(product3.getProductID(), 20).isFailure());
    }

    @Test
    public void removeExistingProductTestFailure2(){
        Store store1 = new Store(0, "h&m",  "talkad", new DiscountPolicy(1), new PurchasePolicy(1));
        ProductDTO product3 = new ProductDTO("Watch", 999, 0, 799.9, null, null, null, 0, 0);

        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        store1.removeProduct(product3.getProductID(), -7);
        Assert.assertTrue(store1.removeProduct(product3.getProductID(), 20).isFailure());
    }
}
