package TestComponent.UnitTesting.ShopComponentTests;

import Server.DAL.DALControllers.DALService;
import Server.Domain.ShoppingManager.*;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.UserManager.CommerceSystem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StoreTest {

    @Before
    public void setUp(){
        CommerceSystem.getInstance().configInit("successconfigfile.json");
        DALService.getInstance().startDB();
        DALService.getInstance().resetDatabase();
    }

    @Test
    public void addProductsTestSuccess(){

       Store store1 = new Store(0, "h&m",  "talkad");
       ProductClientDTO product1 = new ProductClientDTO( "TV", 0, 1299.9  , null , null);
       ProductClientDTO product2 = new ProductClientDTO("AirPods", 0, 799.9, null , null);

        int pAmount = 0;
        store1.addProduct(product1, 5);
        store1.addProduct(product2, 4);
        for(Product product : store1.getInventory().getProducts())
            pAmount += store1.getInventory().getProductAmount(product.getProductID());
        Assert.assertEquals(9, pAmount);
    }

    @Test
    public void addProductsTestFailure(){

        Store store1 = new Store(0, "h&m",  "talkad");
        ProductClientDTO product1 = new ProductClientDTO( "TV", 0, 1299.9  , null , null);

        int pAmount = 0;
        store1.addProduct(product1, -3);

        for(Product product : store1.getInventory().getProducts())
            pAmount += store1.getInventory().getProductAmount(product.getProductID());
        Assert.assertEquals(0, pAmount);
    }

    @Test
    public void addExistingProductTestSuccess1(){
        Store store1 = new Store(0, "h&m",  "talkad");
        ProductClientDTO product1 = new ProductClientDTO( "TV", 997,0, 1299.9  , null , null, null, 0 ,0);

        int inventorySize = 0;
        store1.addProduct(product1, 5);
        store1.addProduct(product1, 10);

        for(Product p : store1.getInventory().getProducts())
            ++inventorySize;
        Assert.assertEquals(1, inventorySize);
    }

    @Test
    public void addExistingProductTestSuccess2(){
        Store store1 = new Store(0, "h&m",  "talkad");
        ProductClientDTO product3 = new ProductClientDTO("Watch", 999, 0, 799.9, null, null, null, 0, 0);

        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        Assert.assertEquals(15, store1.getInventory().getProductAmount(product3.getProductID()));
    }

    @Test
    public void removeExistingProductTestSuccess(){
        Store store1 = new Store(0, "h&m",  "talkad");
        ProductClientDTO product3 = new ProductClientDTO("Watch", 999, 0, 799.9, null, null, null, 0, 0);

        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        store1.removeProduct(product3.getProductID(), 3);
        Assert.assertEquals(12, store1.getInventory().getProductAmount(product3.getProductID()));
    }

    @Test
    public void removeExistingProductTestSuccess2(){
        Store store1 = new Store(0, "h&m",  "talkad");
        ProductClientDTO product3 = new ProductClientDTO("Watch", 999, 0, 799.9, null, null, null, 0, 0);

        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        store1.removeProduct(product3.getProductID(), 15);
        Assert.assertEquals(0, store1.getInventory().getProductAmount(product3.getProductID()));
    }

    @Test
    public void removeExistingProductTestFailure(){
        Store store1 = new Store(0, "h&m",  "talkad");
        ProductClientDTO product3 = new ProductClientDTO("Watch", 999, 0, 799.9, null, null, null, 0, 0);

        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        store1.removeProduct(product3.getProductID(), 20);
        Assert.assertTrue(store1.removeProduct(product3.getProductID(), 20).isFailure());
    }

    @Test
    public void removeExistingProductTestFailure2(){
        Store store1 = new Store(0, "h&m",  "talkad");
        ProductClientDTO product3 = new ProductClientDTO("Watch", 999, 0, 799.9, null, null, null, 0, 0);

        store1.addProduct(product3, 5);
        store1.addProduct(product3, 10);
        store1.removeProduct(product3.getProductID(), -7);
        Assert.assertTrue(store1.removeProduct(product3.getProductID(), 20).isFailure());
    }
}
