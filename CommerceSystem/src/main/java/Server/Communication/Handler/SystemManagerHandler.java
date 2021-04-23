package Server.Communication.Handler;

import Server.Domain.CommonClasses.Response;
import Server.Service.CommerceService;
import com.google.gson.Gson;
import java.util.Properties;

public class SystemManagerHandler extends  Handler{

    private CommerceService service;

    public SystemManagerHandler(Handler nextHandler){
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
            case "getUserPurchaseHistory" -> {
                String adminName = data.getProperty("adminName");
                String username = data.getProperty("username");

                response = service.getUserPurchaseHistory(adminName, username);
            }
            case "getStorePurchaseHistory" ->{
                String adminName = data.getProperty("adminName");
                String storeID = data.getProperty("storeID");

                response = service.getStorePurchaseHistory(adminName, Integer.parseInt(storeID));
            }
            default -> response = new Response<>(false, true, "Action " + action + " does not exists");
        }

        return response.isFailure()? super.handle(input): response;
    }
}
