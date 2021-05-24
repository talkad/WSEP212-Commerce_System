package TestComponent.UnitTesting.ShopComponentTests;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.Store;
import Server.Domain.ShoppingManager.StoreController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class StoreControllerTest {
    private static StoreController storeController;
    private static ProductClientDTO product1;
    private static ProductClientDTO product2;
    private static Store store;

    private static boolean initialized = false;

    @Before
    public void setUp(){
        if(!initialized) {
            storeController = StoreController.getInstance();
            product1 = new ProductClientDTO("TV", 0, 1299.9, null, null);
            product2 = new ProductClientDTO("AirPods", 0, 799.9, List.of("Apple", "Headphones"), List.of("#Expensive", "#Swag"));
            Response<Integer> res = storeController.openStore("castro", "shaked");
            store = storeController.getStoreById(res.getResult());
            store.addProduct(product1, 5);
            store.addProduct(product2, 8);

            initialized = true;
        }
    }

    @Test
    public void addStoreSuccess(){
        Assert.assertFalse(storeController.openStore("zara", "aviad").isFailure());
    }

//    @Test
//    public void searchByProductNameTestSuccess(){
//        boolean result = false;
//        List<ProductClientDTO> products = storeController.searchByProductName("TV").getResult();
//
//        for(ProductClientDTO productDTO: products)
//            if(productDTO.getProductID() == 0)
//                result = true;
//
//        Assert.assertTrue(result);
//    }
//
//    @Test
//    public void searchByProductNameTestSuccess2(){
//        boolean result = false;
//        List<ProductClientDTO> products = storeController.searchByProductName("AirPods").getResult();
//
//        for(ProductClientDTO productDTO: products)
//            if(productDTO.getProductID() == 1)
//                result = true;
//
//        Assert.assertTrue(result);
//    }

    @Test
    public void searchByCategoryTestFailure(){
        Assert.assertTrue(storeController.searchByCategory(null).getResult().isEmpty());
    }

    @Test
    public void searchByCategoryTestSuccess(){
        boolean result = false;
        List<ProductClientDTO> products = storeController.searchByCategory("Apple").getResult();

        for(ProductClientDTO productDTO: products)
            if(productDTO.getProductID() == 1)
                result = true;

        Assert.assertTrue(result);
    }

    @Test
    public void searchByKeyWordTestFailure(){
        Assert.assertTrue(storeController.searchByKeyWord(null).getResult().isEmpty());
    }

//    @Test
//    public void searchByKeyWordTestSuccess(){
//        boolean result = false;
//        List<ProductClientDTO> products = storeController.searchByKeyWord("#Swag").getResult();
//
//        for(ProductClientDTO productDTO: products)
//            if(productDTO.getProductID() == 1)
//                result = true;
//
//        Assert.assertTrue(result);
//    }

    @Test
    public void getStoreByIdTestSuccess(){
        Store store2 = storeController.getStoreById(0);
        Assert.assertSame(store, store2);
    }

    @Test
    public void getStoreByIdTestFailure(){
        Store store2 = storeController.getStoreById(0);
        Assert.assertSame(store, store2);
    }
}
