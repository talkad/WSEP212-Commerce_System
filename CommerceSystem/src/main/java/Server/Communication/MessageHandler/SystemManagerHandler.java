package Server.Communication.MessageHandler;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
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
            case "getTotalSystemRevenue" -> {
                String username = data.getProperty("username");

                response = service.getTotalSystemRevenue(username);
            }
//            case "addDiscountRule" -> {
//                String username = data.getProperty("username");
//                String storeID = data.getProperty("storeID");
//                String type = data.getProperty("type");
//                String discountRuleStr = data.getProperty("discountRule");
//
//                DiscountRule discountRule = parseToDiscountRule(type, discountRuleStr);
//
//                response = service.addDiscountRule(username, Integer.getInteger(storeID), discountRule);
//            }
//            case "addPurchaseRule" -> {
//                String username = data.getProperty("username");
//                String storeID = data.getProperty("storeID");
//                String type = data.getProperty("type");
//                String purchaseRuleStr = data.getProperty("purchaseRule");
//
//                PurchaseRule purchaseRule = parseToPurchaseRule(type, purchaseRuleStr);
//
//                response = service.addPurchaseRule(username, Integer.getInteger(storeID), purchaseRule);
//            }
            default -> response = new Response<>(false, true, "INVALID INPUT: "+input);  // end of the chain of responsibility
        }

        return response;
    }

//    private PurchaseRule parseToPurchaseRule(String type, String purchaseRuleStr) {
//
//    }
//
//    private DiscountRule parseToDiscountRule(String type, String discountRuleStr) {
//
//    }
}
