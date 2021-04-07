package TestComponent.UnitTesting.UserComponentTest;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.UserManager.ShoppingBasket;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import java.util.Map;

public class ShoppingBasketTest {

    private ShoppingBasket basket;
    private Product product1;
    private Product product2;
    private Product product3;

    @Before
    public void setUp(){
        basket = new ShoppingBasket(10);
        product1 = Product.createProduct(new ProductDTO("TV", 10, 1299.9, null, null, null));
        product2 = Product.createProduct(new ProductDTO("AirPods", 9, 1299.9, null, null, null));
        product3 = Product.createProduct(new ProductDTO("TV", 10, 1299.9, null, null, null));
    }

    @Test
    public void addProductLegalTest(){
        Map<ProductDTO, Integer> products;
        int pNum = 0;

        basket.addProduct(product1.getProductDTO());
        basket.addProduct(product1.getProductDTO());
        basket.addProduct(product3.getProductDTO());
        basket.addProduct(product3.getProductDTO());

        products = basket.getProducts();
        for(Integer amount: products.values()) {
            pNum += amount;
        }

        Assert.assertEquals(4, pNum);
    }

    @Test
    public void addProductIllegalTest(){
        Map<ProductDTO, Integer> products;
        Response<Boolean> res;

        res = basket.addProduct(product2.getProductDTO()); // wrong storeID
        products = basket.getProducts();
        Assert.assertTrue(res.isFailure());
        Assert.assertEquals(0, products.size());
    }

    @Test
    public void removeExistingProductTest(){
        Map<ProductDTO, Integer> products;
        Response<Boolean> res;

        basket.addProduct(product1.getProductDTO());

        res = basket.removeProduct(product1.getProductID());
        Assert.assertTrue(res.getResult());

        products = basket.getProducts();
        Assert.assertEquals(0, products.size());
    }

    @Test
    public void removeAbsentProductTest(){
        Map<ProductDTO, Integer> products;
        Response<Boolean> res;

        basket.addProduct(product1.getProductDTO());

        res = basket.removeProduct(product2.getProductID());
        Assert.assertFalse(res.getResult());

        products = basket.getProducts();
        Assert.assertEquals(1, products.size());
    }
}
