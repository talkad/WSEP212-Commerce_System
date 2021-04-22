package Server.Communication;

import Server.Domain.CommonClasses.Response;
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
            case "removeGuest" -> {
                String name = data.getProperty("name");

                response = service.logout(name);
            }
            case "register" ->{
                String identifier = data.getProperty("identifier");
                String username = data.getProperty("username");
                String password = data.getProperty("password");

                response = service.register(identifier, username, password);
            }
            case "login" -> {
                String identifier = data.getProperty("identifier");
                String username = data.getProperty("username");
                String password = data.getProperty("password");

                response = service.login(identifier, username, password);
            }
            case "getContent" -> response = service.getContent();
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

                response = service.getPurchaseHistory(keyword);
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
                String bankAccount = data.getProperty("bankAccount");
                String location = data.getProperty("location");

                response = service.directPurchase(username, bankAccount, location);
            }
            default -> response = new Response<>(false, true, "Action " + action + " does not exists");
        }

        return response.isFailure()? super.handle(input): response;
    }
}
