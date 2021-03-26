package Domain.UserManager.UnitTest;

import Domain.CommonClasses.Response;
import Domain.ShoppingManager.Product;
import Domain.ShoppingManager.ProductDTO;
import Domain.UserManager.ShoppingBasket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingBasketTest {

    private ShoppingBasket basket;
    private Product product1;
    private Product product2;

    @BeforeEach
    public void setUp(){
        basket = new ShoppingBasket(10);
        product1 = new Product(0, 10, "TV", 1299.9, null);
        product2 = new Product(1, 9, "AirPods", 799.9, null);
    }

    @Test
    public void addProductLegalTest(){
        List<ProductDTO> products;

        basket.addProduct(product1);
        products = basket.getProducts();
        assertTrue(products.size() == 1 && products.get(0).getName().equals("TV"));
    }

    @Test
    public void addProductIllegalTest(){
        List<ProductDTO> products;
        Response<Boolean> res;

        res = basket.addProduct(product2); // wrong storeID
        products = basket.getProducts();
        assertTrue(res.isFailure());
        assertEquals(0, products.size());
    }

    @Test
    public void removeExistingProductTest(){
        List<ProductDTO> products;
        Response<Boolean> res;

        basket.addProduct(product1);

        res = basket.removeProduct(product1);
        assertTrue(res.getResult());

        products = basket.getProducts();
        assertEquals(0, products.size());
    }

    @Test
    public void removeAbsentProductTest(){
        List<ProductDTO> products;
        Response<Boolean> res;

        basket.addProduct(product1);

        res = basket.removeProduct(product2);
        assertFalse(res.getResult());

        products = basket.getProducts();
        assertEquals(1, products.size());
    }
}
