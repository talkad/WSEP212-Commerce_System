package TestComponent.UnitTesting.UserComponentTest;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.UserManager.ShoppingBasket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingBasketTest {

    private ShoppingBasket basket;
    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    public void setUp(){
        basket = new ShoppingBasket(10);
        product1 = new Product(0, 10, "TV", 1299.9, null, null);
        product2 = new Product(1, 9, "AirPods", 799.9, null, null);
        product3 = new Product(0, 10, "TV", 1299.9, null, null);
    }

    @Test
    public void addProductLegalTest(){
        Map<Product, Integer> products;
        int pNum = 0;

        basket.addProduct(product1);
        basket.addProduct(product1);
        basket.addProduct(product3);
        basket.addProduct(product3);

        products = basket.getProducts();
        for(Integer amount: products.values()) {
            pNum += amount;
        }

        assertEquals(4, pNum);
    }

    @Test
    public void addProductIllegalTest(){
        Map<Product, Integer> products;
        Response<Boolean> res;

        res = basket.addProduct(product2); // wrong storeID
        products = basket.getProducts();
        assertTrue(res.isFailure());
        assertEquals(0, products.size());
    }

    @Test
    public void removeExistingProductTest(){
        Map<Product, Integer> products;
        Response<Boolean> res;

        basket.addProduct(product1);

        res = basket.removeProduct(product1);
        assertTrue(res.getResult());

        products = basket.getProducts();
        assertEquals(0, products.size());
    }

    @Test
    public void removeAbsentProductTest(){
        Map<Product, Integer> products;
        Response<Boolean> res;

        basket.addProduct(product1);

        res = basket.removeProduct(product2);
        assertFalse(res.getResult());

        products = basket.getProducts();
        assertEquals(1, products.size());
    }
}
