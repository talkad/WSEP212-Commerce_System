package TestComponent.IntegrationTestings;

import Server.DAL.DALService;
import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.*;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.DiscountRules.ProductDiscountRule;
import Server.Domain.ShoppingManager.DiscountRules.StoreDiscountRule;
import Server.Domain.ShoppingManager.Predicates.BasketPredicate;
import Server.Domain.ShoppingManager.PurchaseRules.BasketPurchaseRule;
import Server.Domain.UserManager.DTOs.BasketClientDTO;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import Server.Domain.UserManager.UserController;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class PurchaseTests {

    @Test
    public void purchaseTest(){
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 65482;
        int storeID = StoreController.getInstance().openStore("Apple", "Bill Gates").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("IPhone", productID, storeID, 5000, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule( new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule( 10));

        store.addProduct(productDTO, 5);
        for(int i = 0; i < 5; i++)
            UserController.getInstance().addToCart(guestName, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        response = UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);
        Assert.assertTrue(response.getResult());
    }

    @Test
    public void purchaseIllegalBankAccountTest(){
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 6812;
        int storeID = StoreController.getInstance().openStore("Rami levi", "Rami levi").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("IPhone", productID, storeID, 5000, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule(new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule( 10));

        store.addProduct(productDTO, 5);
        UserController.getInstance().addToCart(guestName, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        response = UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);
        Assert.assertTrue(response.isFailure());
    }

    @Test
    public void purchaseIllegalLocationTest(){
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 19835;
        int storeID = StoreController.getInstance().openStore("ABU fahrizade", "Abu jihad").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("sheep", productID, storeID, 120, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule( new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule(10));

        store.addProduct(productDTO, 5);
        UserController.getInstance().addToCart(guestName, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        response = UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);
        Assert.assertTrue(response.isFailure());
    }

    @Test
    public void purchaseProductOutOfStockTest(){ // use case 2.9
        Response<Boolean> response;
        String guestName1 = UserController.getInstance().addGuest().getResult();
        String guestName2 = UserController.getInstance().addGuest().getResult();
        int productID = 424568;
        int storeID = StoreController.getInstance().openStore("KFC", "Ned Stark").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("sword", productID, storeID, 120, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule( new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule(10));

        store.addProduct(productDTO, 1);
        UserController.getInstance().addToCart(guestName1, storeID, 482163215); // product doesn't exists

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        response = UserController.getInstance().purchase(guestName1, paymentDetails, supplyDetails);
        Assert.assertTrue(response.isFailure());

        UserController.getInstance().addToCart(guestName1, storeID, productID);
        UserController.getInstance().addToCart(guestName2, storeID, productID);
        UserController.getInstance().purchase(guestName2, paymentDetails, supplyDetails);

        response = UserController.getInstance().purchase(guestName1, paymentDetails, supplyDetails);
        Assert.assertTrue(response.isFailure()); // product out of stock
    }


    @Test
    public void badPurchaseInventoryDoesntChangedTest(){
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 138435;
        int storeID = StoreController.getInstance().openStore("Spongebob", "mr. krab").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("jellyfish", productID, storeID, 120, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule( new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule(10));

        store.addProduct(productDTO, 5);

        for(int i = 0; i < 10; i++)
            UserController.getInstance().addToCart(guestName, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        response = UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);
        Assert.assertTrue(response.isFailure());

        Assert.assertEquals(5, store.getInventory().getProductAmount(productID));
    }

    @Test
    public void badPurchaseUserHistoryDoesntChangedTest(){
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 12568634;
        int storeID = StoreController.getInstance().openStore("Evil Inc.", "Dufenshmirtz").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("IHateTestInator", productID, storeID, 99999.99, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule(new BasketPredicate(2, 15, 0)));
        store.addDiscountRule(new StoreDiscountRule( 10));

        UserController.getInstance().register(guestName, "faruk", "doda lola");
        UserController.getInstance().login(guestName, "faruk", "doda lola");

        store.addProduct(productDTO, 5);

        for(int i = 0; i < 10; i++)
            UserController.getInstance().addToCart(guestName, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        UserController.getInstance().purchase("faruk", paymentDetails, supplyDetails);

        Assert.assertEquals(0, UserController.getInstance().getPurchaseHistoryContents("faruk").getResult().size());
        Assert.assertEquals(0, store.getPurchaseHistory().getResult().size());
    }

    @Test
    public void badPurchaseCartDoesntChangedTest(){
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID1 = 846512;
        int productID2 = 3151684;
        int storeID = StoreController.getInstance().openStore("Halil", "Mahmud").getResult();
        ProductClientDTO productDTO1 = new ProductClientDTO("hunus", productID1, storeID, 20, null, null, null, 0, 0);
        ProductClientDTO productDTO2 = new ProductClientDTO("tehini", productID2, storeID, 20, null, null, null, 0, 0);

        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule(new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule(10));

        store.addProduct(productDTO1, 1);
        store.addProduct(productDTO2, 1);

        UserController.getInstance().addToCart(guestName, storeID, productID1);
        UserController.getInstance().addToCart(guestName, storeID, productID2);
        UserController.getInstance().addToCart(guestName, storeID, productID2);

        List<BasketClientDTO> cart = UserController.getInstance().getShoppingCartContents(guestName).getResult();

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        response = UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);
        Assert.assertTrue(response.isFailure());

        // check that cart didnt change
        Assert.assertEquals(1, cart.size());
        Assert.assertEquals(2, cart.get(0).getProductsDTO().size());
    }

    @Test
    public void purchasePurchasePolicyTestSuccess() {
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 65482;
        int storeID = StoreController.getInstance().openStore("Apple", "Bill Gates").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("IPhone", productID, storeID, 5000, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule( new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule(10));

        store.addProduct(productDTO, 5);
        for (int i = 0; i < 3; ++i)
            UserController.getInstance().addToCart(guestName, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        response = UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);
        Assert.assertTrue(response.getResult());
    }

    @Test
    public void purchasePurchasePolicyTestFailure(){
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 65482;
        int storeID = StoreController.getInstance().openStore("Apple", "Bill Gates").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("IPhone", productID, storeID, 5000, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule(new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule(10));

        store.addProduct(productDTO, 5);
            UserController.getInstance().addToCart(guestName, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        response = UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);
        Assert.assertTrue(response.getErrMsg().contains("Not qualified of policy demands."));
    }

    @Test
    public void purchaseDiscountPolicyTestFailure() {
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 65482;
        int storeID = StoreController.getInstance().openStore("Apple", "Bill Gates").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("IPhone", productID, storeID, 5000, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule( new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new ProductDiscountRule( 345345, 10));

        store.addProduct(productDTO, 5);
        for (int i = 0; i < 3; ++i)
            UserController.getInstance().addToCart(guestName, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);

        double purchasePrice = store.getPurchaseHistory().getResult().stream().collect(Collectors.toList()).get(0).getTotalPrice();
        Assert.assertEquals(15000, purchasePrice, 0);
    }

    @Test
    public void purchaseDiscountPolicyTestSuccess() {
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 65482;
        int storeID = StoreController.getInstance().openStore("Apple", "Bill Gates").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("IPhone", productID, storeID, 5000, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule(new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule(10));

        store.addProduct(productDTO, 5);
        for (int i = 0; i < 5; ++i)
            UserController.getInstance().addToCart(guestName, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);

        double purchasePrice = store.getPurchaseHistory().getResult().stream().collect(Collectors.toList()).get(0).getTotalPrice();
        Assert.assertEquals(22500, purchasePrice, 0);
    }

    @Test
    public void badPurchaseNoExternalConnectionsTest(){
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 7537864;
        int storeID = StoreController.getInstance().openStore("Avatar", "Tzuko").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("scar", productID, storeID, 15, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule(new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule(10));

        store.addProduct(productDTO, 5);

        for(int i = 0; i < 10; i++)
            UserController.getInstance().addToCart(guestName, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        response = UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);
        Assert.assertTrue(response.getErrMsg().contains("doesn't created external connection"));
    }

    @Test
    public void goodPurchaseConnectionTest(){
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 45734132;
        int storeID = StoreController.getInstance().openStore("Apple", "Bill Gates").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("IPhone", productID, storeID, 5000, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule(new BasketPredicate(2, 5, 0)));
        store.addDiscountRule(new StoreDiscountRule(10));

        store.addProduct(productDTO, 5);
        for(int i = 0; i < 5; i++)
            UserController.getInstance().addToCart(guestName, storeID, productID);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        response = UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);
        Assert.assertFalse(response.getErrMsg().contains("doesn't created external connection"));
    }
}
