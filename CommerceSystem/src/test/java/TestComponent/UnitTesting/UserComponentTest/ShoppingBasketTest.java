package TestComponent.UnitTesting.UserComponentTest;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.UserManager.ShoppingBasket;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class ShoppingBasketTest {

    @Test
    public void addProductLegalTest(){
        ShoppingBasket basket = new ShoppingBasket(10);
        Product product1 = Product.createProduct(new ProductClientDTO("TV", 10, 1299.9, null, null));
        Product product3 = Product.createProduct(new ProductClientDTO("TV", 10, 1299.9, null, null));

        Map<Product, Integer> products;
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
        ShoppingBasket basket = new ShoppingBasket(10);
        Product product2 = Product.createProduct(new ProductClientDTO("AirPods", 9, 1299.9, null, null));

        Map<Product, Integer> products;
        Response<Boolean> res;

        res = basket.addProduct(product2.getProductDTO()); // wrong storeID
        products = basket.getProducts();
        Assert.assertTrue(res.isFailure());
        Assert.assertEquals(0, products.size());
    }

    @Test
    public void removeExistingProductTest(){
        ShoppingBasket basket = new ShoppingBasket(10);
        Product product1 = Product.createProduct(new ProductClientDTO("TV", 10, 1299.9, null, null));

        Map<Product, Integer> products;
        Response<Boolean> res;

        basket.addProduct(product1.getProductDTO());

        res = basket.removeProduct(product1.getProductID());
        Assert.assertTrue(res.getResult());

        products = basket.getProducts();
        Assert.assertEquals(0, products.size());
    }

    @Test
    public void removeAbsentProductTest(){
        ShoppingBasket basket = new ShoppingBasket(10);
        Product product1 = Product.createProduct(new ProductClientDTO("TV", 10, 1299.9, null, null));
        Product product2 = Product.createProduct(new ProductClientDTO("AirPods", 9, 1299.9, null, null));

        Map<Product, Integer> products;
        Response<Boolean> res;

        basket.addProduct(product1.getProductDTO());

        res = basket.removeProduct(product2.getProductID());
        Assert.assertFalse(res.getResult());

        products = basket.getProducts();
        Assert.assertEquals(1, products.size());
    }

    @Test
    public void updateQuantityTest(){
        Response<Boolean> response;
        ShoppingBasket basket = new ShoppingBasket(10);

        Product product1 = Product.createProduct(new ProductClientDTO("TV", 194652, 10, 1299.9, null, null, null, 0, 0));
        basket.addProduct(product1.getProductDTO());
        response = basket.updateProductQuantity(194652, 20);

        Assert.assertTrue(response.getResult());
        Assert.assertEquals(20, basket.getProductAmount(194652));
    }

    @Test
    public void updateQuantityRemoveProductTest(){
        ShoppingBasket basket = new ShoppingBasket(10);
        Product product1 = Product.createProduct(new ProductClientDTO("TV", 194652, 10, 1299.9, null, null, null, 0, 0));
        basket.addProduct(product1.getProductDTO());
        basket.updateProductQuantity(194652, 0);

        Assert.assertEquals(0, basket.getProducts().keySet().size());
    }

    @Test
    public void updateQuantityIllegalTest(){
        Response<Boolean> response;
        ShoppingBasket basket = new ShoppingBasket(10);
        Product product1 = Product.createProduct(new ProductClientDTO("TV", 194652, 10, 1299.9, null, null, null, 0, 0));

        basket.addProduct(product1.getProductDTO());
        response = basket.updateProductQuantity(194652, -10);

        Assert.assertTrue(response.isFailure());
    }

}
