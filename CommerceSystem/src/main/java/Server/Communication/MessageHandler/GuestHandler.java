package Server.Communication.MessageHandler;

import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import Server.Service.CommerceService;
import com.google.gson.Gson;

import java.util.Properties;

public class GuestHandler extends Handler{

    private CommerceService service;

    public GuestHandler(Handler nextHandler){
        super(nextHandler);
        this.service = CommerceService.getInstance();
    }

    @Override
    public Response<?> handle(String input) {
        Response<?> response;
        Gson gson = new Gson();
        Properties data = gson.fromJson(input, Properties.class);

        String action = data.getProperty("action");

        switch (action) {
            case "startup" -> {
                response = service.addGuest();
            }
            case "logout" -> {
                String username = data.getProperty("username");

                response = service.logout(username);
            }
            case "register" ->{
                String identifier = data.getProperty("identifier");
                String username = data.getProperty("username");
                String password = data.getProperty("pwd");

                response = service.register(identifier, username, password);
            }
            case "login" -> {
                String identifier = data.getProperty("identifier");
                String username = data.getProperty("username");
                String password = data.getProperty("pwd");

                response = service.login(identifier, username, password);
            }
//            case "getContent" -> response = service.getContent();
            case "searchByStoreName" ->{
                String storeName = data.getProperty("storeName");

                response = service.searchByStoreName(storeName);
            }
            case "searchByProductName" ->{
                String productName = data.getProperty("productName");

                response = service.searchByProductName(productName);
            }
            case "searchByProductCategory" ->{
                String category = data.getProperty("category");

                response = service.searchByProductCategory(category);
            }
            case "searchByProductKeyword" ->{
                String keyword = data.getProperty("keyword");

                response = service.searchByProductKeyword(keyword);
            }
            case "addToCart" ->{
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");
                String productID = data.getProperty("productID");

                response = service.addToCart(username, Integer.parseInt(storeID), Integer.parseInt(productID));
            }
            case "removeFromCart" ->{
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");
                String productID = data.getProperty("productID");

                response = service.removeFromCart(username, Integer.parseInt(storeID), Integer.parseInt(productID));
            }
            case "getCartDetails" ->{
                String username = data.getProperty("username");

                response = service.getCartDetails(username);
            }
            case "updateProductQuantity" ->{
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");
                String productID = data.getProperty("productID");
                String amount = data.getProperty("amount");

                response = service.updateProductQuantity(username, Integer.parseInt(storeID), Integer.parseInt(productID), Integer.parseInt(amount));
            }
            case "directPurchase" ->{
                String username = data.getProperty("username");
                String paymentDetails = data.getProperty("paymentDetails");
                String supplyDetails = data.getProperty("supplyDetails");

                response = service.directPurchase(username, gson.fromJson(paymentDetails, PaymentDetails.class), gson.fromJson(supplyDetails, SupplyDetails.class));
            }
            case "bidOffer" ->{
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");
                String productID = data.getProperty("productID");
                String priceOffer = data.getProperty("priceOffer");

                response = service.bidOffer(username, Integer.parseInt(productID), Integer.parseInt(storeID), Double.parseDouble(priceOffer));
           }
            case "bidUserReply" ->{
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");
                String productID = data.getProperty("productID");
                String paymentDetails = data.getProperty("paymentDetails");
                String supplyDetails = data.getProperty("supplyDetails");

                response = service.bidUserReply(username, Integer.parseInt(productID), Integer.parseInt(storeID), gson.fromJson(paymentDetails, PaymentDetails.class), gson.fromJson(supplyDetails, SupplyDetails.class));
            }
            case "getStore" ->{
                String storeID = data.getProperty("storeID");

                response = service.getStore( Integer.parseInt(storeID));
            }
            case "getStoreOwned" ->{
                String username = data.getProperty("username");

                response = service.getStoreOwned(username);
            }
            case "getMyStores" ->{
                String username = data.getProperty("username");

                response = service.getMyStores(username);
            }
            default -> response = super.handle(input);
        }

        return response;
    }
}
