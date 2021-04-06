package TestComponent.UnitTesting.UserComponentTest;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.ProductDTO;
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

        assertEquals(4, pNum);
    }

    @Test
    public void addProductIllegalTest(){
        Map<ProductDTO, Integer> products;
        Response<Boolean> res;

        res = basket.addProduct(product2.getProductDTO()); // wrong storeID
        products = basket.getProducts();
        assertTrue(res.isFailure());
        assertEquals(0, products.size());
    }

    @Test
    public void removeExistingProductTest(){
        Map<ProductDTO, Integer> products;
        Response<Boolean> res;

        basket.addProduct(product1.getProductDTO());

        res = basket.removeProduct(product1.getProductID());
        assertTrue(res.getResult());

        products = basket.getProducts();
        assertEquals(0, products.size());
    }

    @Test
    public void removeAbsentProductTest(){
        Map<ProductDTO, Integer> products;
        Response<Boolean> res;

        basket.addProduct(product1.getProductDTO());

        res = basket.removeProduct(product2.getProductID());
        assertFalse(res.getResult());

        products = basket.getProducts();
        assertEquals(1, products.size());
    }
}
