package TestComponent.AcceptanceTestings.Tests;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import Server.Domain.UserManager.PermissionsEnum;
import Server.Domain.UserManager.DTOs.PurchaseClientDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class ParallelismTests extends ProjectAcceptanceTests{

    private int storeID;

    private static boolean initialized = false;

    @Before
    public void setUp(){

        if(!initialized) {
            super.setUp();

            String guestName = bridge.addGuest().getResult();

            bridge.register(guestName, "korra", "123456");
            bridge.login(bridge.addGuest().getResult(), "korra", "123456");
            this.storeID = bridge.openStore("korra", "the legend of korra").getResult();

            // adding a product which is last in stock
            ProductClientDTO product = new ProductClientDTO("air bending", storeID, 10,
                    new LinkedList<String>(Arrays.asList("air", "bending")),
                    new LinkedList<String>(Arrays.asList("bending")));

            bridge.addProductsToStore("korra", product, 1);

            // a product which the owner will try to remove while someone buys
            product = new ProductClientDTO("earth bending", storeID, 50,
                    new LinkedList<String>(Arrays.asList("earth", "bending")),
                    new LinkedList<String>(Arrays.asList("bending")));

            bridge.addProductsToStore("korra", product, 42);

            // creating another 2 users for later use
            bridge.register(guestName, "bolin", "123456");
            bridge.login(bridge.addGuest().getResult(), "bolin", "123456");
            bridge.register(guestName, "tenzin", "123456");
            bridge.login(bridge.addGuest().getResult(), "tenzin", "123456");

            // creating user who's going to be an owner in the store
            bridge.register(guestName, "mako", "123456");
            bridge.login(bridge.addGuest().getResult(), "mako", "123456");
            bridge.appointStoreOwner("korra", "mako", storeID);

            // creating a user who's going to be appointed to the store
            bridge.register(guestName, "asami", "123456");
            bridge.login(bridge.addGuest().getResult(), "asami", "123456");

            // creating a user who's going to be a manager with a permission to view purchase history of a store
            bridge.register(guestName, "jinora", "123456");
            bridge.login(bridge.addGuest().getResult(), "jinora", "123456");
            bridge.appointStoreManager("korra", "jinora", storeID);
            bridge.addPermission("korra", storeID, "jinora", PermissionsEnum.RECEIVE_STORE_HISTORY);

            initialized = true;
        }
    }

    @Test
    public void parallelPurchaseTest() throws InterruptedException {
        // creating two threads which will compete over a product but not starting them yet
        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");


        final int[] buyer1Result = new int[1]; // 0 - didn't get to buy. 1 - bought the product
        Thread buyer1 = new Thread(){
            public void run(){
                Response<List<ProductClientDTO>> searchResponse = bridge.searchByProductName("air bending");
                if(!searchResponse.getResult().isEmpty()) {
                    ProductClientDTO productToAdd = searchResponse.getResult().get(0);
                    Response<Boolean> cartResponse = bridge.addToCart("bolin",
                            productToAdd.getStoreID(), productToAdd.getProductID());
                    if(!cartResponse.isFailure()) {
                        Response<Boolean> purchaseResult =bridge.directPurchase("bolin",
                                paymentDetails, supplyDetails);
                        if(!purchaseResult.isFailure()){
                            buyer1Result[0] = 1;
                        }
                    }
                }
            }
        };

        final int[] buyer2Result = new int[1]; // 0 - didn't get to buy. 1 - bought the product

        Thread buyer2 = new Thread(){
          public void run(){
              Response<List<ProductClientDTO>> searchResponse = bridge.searchByProductName("air bending");
              if(!searchResponse.getResult().isEmpty()) {
                  ProductClientDTO productToAdd = searchResponse.getResult().get(0);
                  Response<Boolean> cartResponse = bridge.addToCart("tenzin",
                          productToAdd.getStoreID(), productToAdd.getProductID());
                  if(!cartResponse.isFailure()) {
                      Response<Boolean> purchaseResult =bridge.directPurchase("tenzin",paymentDetails, supplyDetails);
                      if(!purchaseResult.isFailure()){
                          buyer2Result[0] = 1;
                      }
                  }
              }
          }
        };

        // now that we have our threads ready we'll start them in random manner
        Random random = new Random(System.currentTimeMillis());
        if(random.nextInt(2) == 0){
            buyer1.start();
            buyer2.start();
        }
        else{
            buyer2.start();
            buyer1.start();
        }

        //waiting for them to terminate
        buyer1.join();
        buyer2.join();

        // one needs to succeed while the other to fail
        if((buyer1Result[0] == 0 && buyer2Result[0] == 0) || (buyer1Result[0] == 1 && buyer2Result[0] == 1)){
            System.out.println(buyer1Result[0]);
            System.out.println(buyer2Result[0]);
            Assert.fail();
        }
    }

    @Test
    public void parallelBuyAndRemoveTest() throws InterruptedException {
        PaymentDetails paymentDetails = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        SupplyDetails supplyDetails = new SupplyDetails("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");

        // creating two threads where one tries to buy a product while the other tries to remove it
        final int[] buyerResult = new int[1]; // 0 - didn't get to buy. 1 - bought the product
        Thread buyer = new Thread(){
            public void run(){
                Response<List<ProductClientDTO>> searchResponse = bridge.searchByProductName("earth bending");
                if(!searchResponse.isFailure()) {
                    ProductClientDTO productToAdd = searchResponse.getResult().get(0);
                    Response<Boolean> cartResponse = bridge.addToCart("bolin",
                            productToAdd.getStoreID(), productToAdd.getProductID());
                    if(!cartResponse.isFailure()) {
                        Response<Boolean> purchaseResult = bridge.directPurchase("bolin", paymentDetails, supplyDetails);
                        if(!purchaseResult.isFailure()){
                            buyerResult[0] = 1;
                        }
                    }
                }
            }
        };

        final int[] removerResult = new int[1]; // 0 - couldn't remove. 1 - removed.
        Thread remover = new Thread(){
            public void run(){
                Response<List<ProductClientDTO>> searchResponse = bridge.searchByProductName("earth bending");
                if(!searchResponse.isFailure()) {
                    ProductClientDTO productToRemove = searchResponse.getResult().get(0);
                    Response<Boolean> removeResult = bridge.removeProductsFromStore("korra",
                            productToRemove.getStoreID(), productToRemove.getProductID(), 42);
                    if(!removeResult.isFailure()){
                        removerResult[0] = 1;
                    }
                }
            }
        };

        // now that we have our threads ready we'll start them in random manner
        Random random = new Random(System.currentTimeMillis());
        if(random.nextInt(2) == 0){
            buyer.start();
            remover.start();
        }
        else{
            remover.start();
            buyer.start();
        }

        //waiting for them to terminate
        buyer.join();
        remover.join();

        // one needs to succeed while the other to fail
        if((removerResult[0] == 0 && buyerResult[0] == 0) || (removerResult[0] == 1 && buyerResult[0] == 1)){
            Assert.fail();
        }
    }

    @Test
    public void parallelDoubleAppointment() throws InterruptedException {
        // creating two threads where both try to appoint the same user at the same time
        final int[] appointer1Result = new int[1];
        Thread appointer1 = new Thread(){
          public void run(){
              Response<Boolean> appointmentResult = bridge.appointStoreOwner("korra", "asami",
                      storeID);
              if(!appointmentResult.isFailure()){
                  appointer1Result[0] = 1;
              }
          }
        };

        final int[] appointer2Result = new int[1];
        Thread appointer2 = new Thread(){
            public void run(){
                Response<Boolean> appointmentResult = bridge.appointStoreOwner("mako", "asami",
                        storeID);
                if(!appointmentResult.isFailure()){
                    appointer2Result[0] = 1;
                }
            }
        };

        // now that we have our threads ready we'll start them in random manner
        Random random = new Random(System.currentTimeMillis());
        if(random.nextInt(2) == 0){
            appointer1.start();
            appointer2.start();
        }
        else{
            appointer2.start();
            appointer1.start();
        }

        //waiting for them to terminate
        appointer1.join();
        appointer2.join();
        // one needs to succeed while the other to fail
        if((appointer1Result[0] == 0 && appointer2Result[0] == 0) || (appointer1Result[0] == 1 && appointer2Result[0] == 1)){
            Assert.fail();
        }
    }

    @Test
    public void parallelActionAndRemovePermissionTest() throws InterruptedException {
        // creating two threads where one tries to do an action while the other tries to take his permissions
        final int[] actorResult = new int[1]; // 0 - didn't get to buy. 1 - bought the product
        Thread actor = new Thread(){
            public void run(){
                Response<Collection<PurchaseClientDTO>> historyResult = bridge.getPurchaseDetails("jinora", storeID);
                if(!historyResult.isFailure()){
                    actorResult[0] = 1;
                }
            }
        };

        final int[] removerResult = new int[1]; // 0 - couldn't remove. 1 - removed.
        Thread remover = new Thread(){
            public void run(){
                Response<Boolean> removeResponse = bridge.removePermission("korra", storeID, "jinora",
                        PermissionsEnum.RECEIVE_STORE_HISTORY);
                if(!removeResponse.isFailure()) {
                    removerResult[0] = 1;
                }
            }
        };

        // now that we have our threads ready we'll start them in random manner
        Random random = new Random(System.currentTimeMillis());
        if(random.nextInt(2) == 0){
            actor.start();
            remover.start();
        }
        else{
            remover.start();
            actor.start();
        }

        //waiting for them to terminate
        actor.join();
        remover.join();

        // one needs to succeed while the other to fail
        if((actorResult[0] == 0 && removerResult[0] == 0) || (actorResult[0] == 1 && removerResult[0] == 0)){
            Assert.fail();
        }
    }
}
