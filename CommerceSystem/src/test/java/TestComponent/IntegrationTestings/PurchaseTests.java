package TestComponent.IntegrationTestings;

import Server.DAL.DALService;
import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.*;

import Server.Domain.ShoppingManager.DiscountRules.*;
import Server.Domain.ShoppingManager.Predicates.BasketPredicate;
import Server.Domain.ShoppingManager.Predicates.ProductPredicate;
import Server.Domain.ShoppingManager.PurchaseRules.*;

import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.DiscountRules.ProductDiscountRule;
import Server.Domain.ShoppingManager.DiscountRules.StoreDiscountRule;
import Server.Domain.ShoppingManager.PurchaseRules.BasketPurchaseRule;
import Server.Domain.UserManager.CommerceSystem;
import Server.Domain.UserManager.DTOs.BasketClientDTO;

import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentSystemAdapter;
import Server.Domain.UserManager.ExternalSystemsAdapters.ProductSupplyAdapter;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import Server.Domain.UserManager.OfferState;
import Server.Domain.UserManager.Publisher;
import Server.Domain.UserManager.UserController;
import Server.Service.CommerceService;
import TestComponent.IntegrationTestings.Mocks.MockNotifier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import java.util.List;
import java.util.stream.Collectors;


public class PurchaseTests {

    @Before
    public void init(){
        DALService.getInstance().useTestDatabase();
        PaymentSystemAdapter.getInstance().setMockFlag();
        ProductSupplyAdapter.getInstance().setMockFlag();
    }

    @Test
    public void purchaseTest(){
        PaymentSystemAdapter.getInstance().setMockFlag();
        ProductSupplyAdapter.getInstance().setMockFlag();
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

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "204444444");
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
    public void purchaseAndPurchasePolicyTestFailure(){
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 65482, productID2 = 65784;
        int storeID = StoreController.getInstance().openStore("Supermarket", "Rami Levi").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("tomato", productID, storeID, 10, null, null, null, 0, 0);
        ProductClientDTO productDTO2 = new ProductClientDTO("corn", productID, storeID, 5, null, null, null, 0, 0);
        Store store = StoreController.getInstance().getStoreById(storeID);

        List<PurchaseRule> policyRules = new LinkedList<>();
        policyRules.add(new ProductPurchaseRule(new ProductPredicate(productID, 0, 5)));
        policyRules.add(new ProductPurchaseRule(new ProductPredicate(productID2, 2, 10)));

        store.addPurchaseRule(new AndCompositionPurchaseRule(policyRules));
        store.addDiscountRule(new StoreDiscountRule(10));

        store.addProduct(productDTO, 6);
        store.addProduct(productDTO2, 5);

        UserController.getInstance().addToCart(guestName, storeID, productID);

        for(int i =0; i<5; ++i) {
            UserController.getInstance().addToCart(guestName, storeID, productID);
            UserController.getInstance().addToCart(guestName, storeID, productID2);
        }

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        response = UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);
        Assert.assertTrue(response.getErrMsg().contains("Not qualified of policy demands."));
    }

    @Test
    public void purchaseOrPurchasePolicyTestFailure(){
        Response<Boolean> response;
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 65482, productID2 = 65784, productID3 = 65466;
        int storeID = StoreController.getInstance().openStore("Supermarket", "Rami Levi").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("tomato",  productID, storeID, 10, null, null, null, 0, 0);
        ProductClientDTO productDTO2 = new ProductClientDTO("corn", productID2, storeID, 5, null, null, null, 0, 0);

        Store store = StoreController.getInstance().getStoreById(storeID);

        List<PurchaseRule> policyRules = new LinkedList<>();
        policyRules.add(new ProductPurchaseRule(new ProductPredicate(productID, 0, 4)));
        policyRules.add(new ProductPurchaseRule(new ProductPredicate(productID2, 7, 10)));

        store.addPurchaseRule(new OrCompositionPurchaseRule(policyRules));
        store.addDiscountRule(new StoreDiscountRule(10));

        store.addProduct(productDTO, 5);
        store.addProduct(productDTO2, 5);

        for(int i =0; i<5; ++i) {
            UserController.getInstance().addToCart(guestName, storeID, productID);
            UserController.getInstance().addToCart(guestName, storeID, productID2);
        }

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
    public void purchaseXorDiscountPolicyTestSuccess() {
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 65482, productID2 = 64583, COMPOSITION_USE_ONLY = -100;
        int storeID = StoreController.getInstance().openStore("Supermarket", "Rami Levi").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("Milk", productID, storeID, 10, List.of("dairy"), null, null, 0, 0);
        ProductClientDTO productDTO2 = new ProductClientDTO("Croissant", productID2, storeID, 5, List.of("pastry"), null, null, 0, 0);

        List<DiscountRule> policyRules = new LinkedList<>();
        policyRules.add(new CategoryDiscountRule("dairy", COMPOSITION_USE_ONLY));
        policyRules.add(new CategoryDiscountRule("pastry", COMPOSITION_USE_ONLY));

        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule(new BasketPredicate(2, 20, 0)));
        store.addDiscountRule(new XorCompositionDiscountRule(10, policyRules, XorResolveType.HIGHEST));

        store.addProduct(productDTO, 5);
        store.addProduct(productDTO2, 5);

        for (int i = 0; i < 5; ++i) {
            UserController.getInstance().addToCart(guestName, storeID, productID);
            UserController.getInstance().addToCart(guestName, storeID, productID2);
        }

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);

        double purchasePrice = store.getPurchaseHistory().getResult().stream().collect(Collectors.toList()).get(0).getTotalPrice();
        Assert.assertEquals(70, purchasePrice, 0);
    }

    @Test
    public void purchaseAndDiscountPolicyTestSuccess() {
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 65482, productID2 = 64583, COMPOSITION_USE_ONLY = -100;
        int storeID = StoreController.getInstance().openStore("Supermarket", "Rami Levi").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("Bread", productID, storeID, 10, List.of("pastry"), null, null, 0, 0);
        ProductClientDTO productDTO2 = new ProductClientDTO("Buns", productID2, storeID, 5, List.of("pastry"), null, null, 0, 0);

        List<DiscountRule> policyRules = new LinkedList<>();
        policyRules.add(new ConditionalProductDiscountRule(productID, COMPOSITION_USE_ONLY, new ProductPredicate(productID, 2, 10)));
        policyRules.add(new ConditionalProductDiscountRule(productID2, COMPOSITION_USE_ONLY, new ProductPredicate(productID2, 5, 10)));

        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule(new BasketPredicate(2, 20, 0)));
        store.addDiscountRule(new AndCompositionDiscountRule("pastry", 10, policyRules));

        store.addProduct(productDTO, 5);
        store.addProduct(productDTO2, 5);

        for (int i = 0; i < 5; ++i) {
            UserController.getInstance().addToCart(guestName, storeID, productID);
            UserController.getInstance().addToCart(guestName, storeID, productID2);
        }

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);

        double purchasePrice = store.getPurchaseHistory().getResult().stream().collect(Collectors.toList()).get(0).getTotalPrice();
        Assert.assertEquals(67.5, purchasePrice, 0);
    }

    @Test
    public void purchaseAndDiscountPolicyTestFailure() {
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 65482, productID2 = 64583, COMPOSITION_USE_ONLY = -100;
        int storeID = StoreController.getInstance().openStore("Supermarket", "Rami Levi").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("Bread", productID, storeID, 10, List.of("pastry"), null, null, 0, 0);
        ProductClientDTO productDTO2 = new ProductClientDTO("Buns", productID2, storeID, 5, List.of("pastry"), null, null, 0, 0);

        List<DiscountRule> policyRules = new LinkedList<>();
        policyRules.add(new ConditionalProductDiscountRule(productID, COMPOSITION_USE_ONLY, new ProductPredicate(productID, 2, 10)));
        policyRules.add(new ConditionalProductDiscountRule(productID2, COMPOSITION_USE_ONLY, new ProductPredicate(productID2, 6, 10)));

        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule(new BasketPredicate(2, 20, 0)));
        store.addDiscountRule(new AndCompositionDiscountRule("pastry", 10, policyRules));

        store.addProduct(productDTO, 5);
        store.addProduct(productDTO2, 5);

        for (int i = 0; i < 5; ++i) {
            UserController.getInstance().addToCart(guestName, storeID, productID);
            UserController.getInstance().addToCart(guestName, storeID, productID2);
        }

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);

        double purchasePrice = store.getPurchaseHistory().getResult().stream().collect(Collectors.toList()).get(0).getTotalPrice();
        Assert.assertEquals(75, purchasePrice, 0);
    }

    @Test
    public void purchaseOrDiscountPolicyTestSuccess() {
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 65482, productID2 = 64583, COMPOSITION_USE_ONLY = -100;
        int storeID = StoreController.getInstance().openStore("Supermarket", "Rami Levi").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("Cottage", productID, storeID, 10, List.of("dairy"), null, null, 0, 0);
        ProductClientDTO productDTO2 = new ProductClientDTO("Yogurt", productID2, storeID, 5, List.of("dairy"), null, null, 0, 0);

        List<DiscountRule> policyRules = new LinkedList<>();
        policyRules.add(new ConditionalProductDiscountRule(productID, COMPOSITION_USE_ONLY, new ProductPredicate(productID, 3, 10)));
        policyRules.add(new ConditionalProductDiscountRule(productID2, COMPOSITION_USE_ONLY, new ProductPredicate(productID2, 2, 10)));

        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule(new BasketPredicate(2, 20, 0)));
        store.addDiscountRule(new OrCompositionDiscountRule("dairy", 10, policyRules));

        store.addProduct(productDTO, 5);
        store.addProduct(productDTO2, 5);

        for (int i = 0; i < 5; ++i) {
            UserController.getInstance().addToCart(guestName, storeID, productID);
            UserController.getInstance().addToCart(guestName, storeID, productID2);
        }

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);

        double purchasePrice = store.getPurchaseHistory().getResult().stream().collect(Collectors.toList()).get(0).getTotalPrice();
        Assert.assertEquals(67.5, purchasePrice, 0);
    }

    @Test
    public void purchaseMaximumDiscountPolicyTestSuccess() {
        String guestName = UserController.getInstance().addGuest().getResult();
        int productID = 65482, productID2 = 64583, COMPOSITION_USE_ONLY = -100;
        int storeID = StoreController.getInstance().openStore("Supermarket", "Rami Levi").getResult();
        ProductClientDTO productDTO = new ProductClientDTO("Cottage", productID, storeID, 10, List.of("dairy"), null, null, 0, 0);
        ProductClientDTO productDTO2 = new ProductClientDTO("Yogurt", productID2, storeID, 5, List.of("dairy"), null, null, 0, 0);

        List<DiscountRule> policyRules = new LinkedList<>();
        policyRules.add(new ProductDiscountRule(productID, 10));
        policyRules.add(new ProductDiscountRule(productID2, 10));

        Store store = StoreController.getInstance().getStoreById(storeID);
        store.addPurchaseRule(new BasketPurchaseRule(new BasketPredicate(2, 20, 0)));
        store.addDiscountRule(new MaximumCompositionDiscountRule(policyRules));

        store.addProduct(productDTO, 5);
        store.addProduct(productDTO2, 5);

        for (int i = 0; i < 5; ++i) {
            UserController.getInstance().addToCart(guestName, storeID, productID);
            UserController.getInstance().addToCart(guestName, storeID, productID2);
        }

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        UserController.getInstance().purchase(guestName, paymentDetails, supplyDetails);

        double purchasePrice = store.getPurchaseHistory().getResult().stream().collect(Collectors.toList()).get(0).getTotalPrice();
        Assert.assertEquals(70, purchasePrice, 0);
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

    @Test
    public void bidNotificationTest(){
        int productId = 200;
        ProductClientDTO productDTO;
        CommerceSystem commerceSystem = CommerceSystem.getInstance();
        commerceSystem.init();
        UserController userController = UserController.getInstance();
        String storeOwnerName = commerceSystem.addGuest().getResult();
        String costumerName = UserController.getInstance().addGuest().getResult();

        MockNotifier mock = new MockNotifier();
        mock.addConnection("user1", null);
        mock.addConnection("user2", null);
        Publisher.getInstance().setNotifier(mock);

        userController.register(storeOwnerName, "user1", "user1");
        userController.register(costumerName, "user2", "user2");

        userController.login(storeOwnerName, "user1", "user1");
        userController.login(costumerName, "user2", "user2");

        Response<Integer> storeRes = userController.openStore("user1", "eggStore");
        Store store = StoreController.getInstance().getStoreById(storeRes.getResult());
        productDTO = new ProductClientDTO("Eggs", productId,storeRes.getResult(),13.5, null, null, null, 0,0);
        store.addProduct(productDTO, 100);

        commerceSystem.logout("user1");
        commerceSystem.bidOffer("user2", 200, storeRes.getResult(), 10);

        Assert.assertEquals(1, userController.getUserByName("user1").getPendingMessages().size());

    }

    @Test
    public void bidManagerReplyTest(){
        int productId = 200;
        ProductClientDTO productDTO;
        CommerceSystem commerceSystem = CommerceSystem.getInstance();
        commerceSystem.init();
        UserController userController = UserController.getInstance();
        String storeOwnerName = commerceSystem.addGuest().getResult();
        String costumerName = UserController.getInstance().addGuest().getResult();

        MockNotifier mock = new MockNotifier();
        mock.addConnection("user1", null);
        mock.addConnection("user2", null);
        Publisher.getInstance().setNotifier(mock);

        userController.register(storeOwnerName, "user1", "user1");
        userController.register(costumerName, "user2", "user2");

        userController.login(storeOwnerName, "user1", "user1");
        userController.login(costumerName, "user2", "user2");

        Response<Integer> storeRes = userController.openStore("user1", "eggStore");
        Store store = StoreController.getInstance().getStoreById(storeRes.getResult());
        productDTO = new ProductClientDTO("Eggs", productId,storeRes.getResult(),13.5, null, null, null, 0,0);
        store.addProduct(productDTO, 100);

        commerceSystem.bidOffer("user2", 200, storeRes.getResult(), 10);
        //commerceSystem.logout("user2");

        commerceSystem.bidManagerReply("user1", "user2", 200, storeRes.getResult(), -1);

        //Assert.assertEquals(1, userController.getUserByName("user2").getPendingMessages().size());
        Assert.assertEquals(OfferState.APPROVED, userController.getUserByName("user2").getOffers().get(200).getState());
    }

    @Test
    public void bidUserReplySuccessfulTest(){
        int productId = 200;
        ProductClientDTO productDTO;
        CommerceSystem commerceSystem = CommerceSystem.getInstance();
        commerceSystem.init();
        UserController userController = UserController.getInstance();
        String storeOwnerName = commerceSystem.addGuest().getResult();
        String costumerName = UserController.getInstance().addGuest().getResult();

        MockNotifier mock = new MockNotifier();
        mock.addConnection("user1", null);
        mock.addConnection("user2", null);
        Publisher.getInstance().setNotifier(mock);

        userController.register(storeOwnerName, "user1", "user1");
        userController.register(costumerName, "user2", "user2");

        userController.login(storeOwnerName, "user1", "user1");
        userController.login(costumerName, "user2", "user2");

        Response<Integer> storeRes = userController.openStore("user1", "eggStore");
        Store store = StoreController.getInstance().getStoreById(storeRes.getResult());
        productDTO = new ProductClientDTO("Eggs", productId,storeRes.getResult(),13.5, null, null, null, 0,0);
        store.addProduct(productDTO, 100);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        commerceSystem.bidOffer("user2", 200, storeRes.getResult(), 10);
        commerceSystem.bidManagerReply("user1", "user2", 200, storeRes.getResult(), -1);
        boolean isPurchaseFailure = commerceSystem.bidUserReply("user2", 200, storeRes.getResult(), paymentDetails, supplyDetails).isFailure();
        Assert.assertEquals(false, isPurchaseFailure);
    }

    @Test
    public void bidUserReplyDeclinedTest(){
        int productId = 200;
        ProductClientDTO productDTO;
        CommerceSystem commerceSystem = CommerceSystem.getInstance();
        commerceSystem.init();
        UserController userController = UserController.getInstance();
        String storeOwnerName = commerceSystem.addGuest().getResult();
        String costumerName = UserController.getInstance().addGuest().getResult();

        MockNotifier mock = new MockNotifier();
        mock.addConnection("user1", null);
        mock.addConnection("user2", null);
        Publisher.getInstance().setNotifier(mock);

        userController.register(storeOwnerName, "user1", "user1");
        userController.register(costumerName, "user2", "user2");

        userController.login(storeOwnerName, "user1", "user1");
        userController.login(costumerName, "user2", "user2");

        Response<Integer> storeRes = userController.openStore("user1", "eggStore");
        Store store = StoreController.getInstance().getStoreById(storeRes.getResult());
        productDTO = new ProductClientDTO("Eggs", productId,storeRes.getResult(),13.5, null, null, null, 0,0);
        store.addProduct(productDTO, 100);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        commerceSystem.bidOffer("user2", 200, storeRes.getResult(), 10);
        commerceSystem.bidManagerReply("user1", "user2", 200, storeRes.getResult(), -2);
        Response<Boolean> purchaseRes = commerceSystem.bidUserReply("user2", 200, storeRes.getResult(), paymentDetails, supplyDetails);

        Assert.assertTrue(purchaseRes.isFailure());
        //Assert.assertEquals("Your offer was declined", purchaseRes.getErrMsg());

    }

    @Test
    public void bidUserReplyWithCounterPriceTest(){
        PaymentSystemAdapter.getInstance().setMockFlag();
        ProductSupplyAdapter.getInstance().setMockFlag();
        int productId = 200;
        ProductClientDTO productDTO;
        CommerceSystem commerceSystem = CommerceSystem.getInstance();
        commerceSystem.init();
        UserController userController = UserController.getInstance();
        String storeOwnerName = commerceSystem.addGuest().getResult();
        String costumerName = UserController.getInstance().addGuest().getResult();

        MockNotifier mock = new MockNotifier();
        mock.addConnection("user1", null);
        mock.addConnection("user2", null);
        Publisher.getInstance().setNotifier(mock);

        userController.register(storeOwnerName, "user1", "user1");
        userController.register(costumerName, "user2", "user2");

        userController.login(storeOwnerName, "user1", "user1");
        userController.login(costumerName, "user2", "user2");

        Response<Integer> storeRes = userController.openStore("user1", "eggStore");
        Store store = StoreController.getInstance().getStoreById(storeRes.getResult());
        productDTO = new ProductClientDTO("Eggs", productId,storeRes.getResult(),13.5, null, null, null, 0,0);
        store.addProduct(productDTO, 100);

        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "204444449");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        commerceSystem.bidOffer("user2", 200, storeRes.getResult(), 10);
        commerceSystem.bidManagerReply("user1", "user2", 200, storeRes.getResult(), 20);
        Response<Boolean> purchaseRes = commerceSystem.bidUserReply("user2", 200, storeRes.getResult(), paymentDetails, supplyDetails);

        Assert.assertFalse(purchaseRes.isFailure());
        Assert.assertEquals(20.0, userController.getUserByName("user2").getPurchaseHistory().getPurchases().get(0).getTotalPrice(), 0);
    }
}
