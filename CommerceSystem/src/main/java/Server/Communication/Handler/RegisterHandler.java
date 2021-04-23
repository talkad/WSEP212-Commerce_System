package Server.Communication.Handler;

import Server.Domain.CommonClasses.Response;
import Server.Service.CommerceService;
import com.google.gson.Gson;

import java.util.Properties;

public class RegisterHandler extends Handler{

    private CommerceService service;

    public RegisterHandler(Handler nextHandler){
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
            case "logout" -> {
                String userName = data.getProperty("userName");

                response = service.logout(userName);
            }
            case "openStore" ->{
                String username = data.getProperty("username");
                String storeName = data.getProperty("storeName");

                response = service.openStore(username, storeName);
            }
            case "addProductReview" -> {
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");
                String productID = data.getProperty("productID");
                String review = data.getProperty("review");

                response = service.addProductReview(username, Integer.parseInt(storeID), Integer.parseInt(productID), review);
            }
            case "getPurchaseHistory" ->{
                String username = data.getProperty("username");

                response = service.getPurchaseHistory(username);
            }
            default -> response = new Response<>(false, true, "Action " + action + " does not exists");
        }

        return response.isFailure()? super.handle(input): response;
    }

}
