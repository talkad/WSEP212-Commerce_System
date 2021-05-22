package Server.Communication.MessageHandler;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.UserManager.PermissionsEnum;
import Server.Service.CommerceService;
import com.google.gson.Gson;

import java.util.Properties;

public class StoreManagerHandler extends Handler{

    private CommerceService service;

    public StoreManagerHandler(Handler nextHandler){
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
            case "addProductsToStore" -> {
                String username = data.getProperty("username");
                String productDTO = data.getProperty("productDTO");
                String amount = data.getProperty("amount");
                ProductClientDTO product = gson.fromJson(productDTO, ProductClientDTO.class);
                product = new ProductClientDTO(product.getName(), product.getStoreID(), product.getPrice(), product.getCategories(), product.getKeywords());

                response = service.addProductsToStore(username, product, Integer.parseInt(amount));
            }
            case "removeProductsFromStore" ->{
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");
                String productID = data.getProperty("productID");
                String amount = data.getProperty("amount");

                response = service.removeProductsFromStore(username, Integer.parseInt(storeID), Integer.parseInt(productID), Integer.parseInt(amount));
            }
            case "updateProductInfo" -> {
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");
                String productID = data.getProperty("productID");
                String newPrice = data.getProperty("newPrice");
                String newName = data.getProperty("newName");

                response = service.updateProductInfo(username, Integer.parseInt(storeID), Integer.parseInt(productID), Double.parseDouble(newPrice), newName);
            }
            case "getPurchasePolicy" -> {
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");

                response = service.getPurchasePolicy(username, Integer.parseInt(storeID));
            }
            case "getDiscountPolicy" ->{
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");

                response = service.getDiscountPolicy(username, Integer.parseInt(storeID));
            }
            case "appointStoreOwner" ->{
                String appointerName = data.getProperty("appointerName");
                String appointeeName = data.getProperty("appointeeName");
                String storeID = data.getProperty("storeID");

                response = service.appointStoreOwner(appointerName, appointeeName, Integer.parseInt(storeID));
            }
            case "removeOwnerAppointment" ->{
                String appointerName = data.getProperty("appointerName");
                String appointeeName = data.getProperty("appointeeName");
                String storeID = data.getProperty("storeID");

                response = service.removeOwnerAppointment(appointerName, appointeeName, Integer.parseInt(storeID));
            }
            case "appointStoreManager" ->{
                String appointerName = data.getProperty("appointerName");
                String appointeeName = data.getProperty("appointeeName");
                String storeID = data.getProperty("storeID");

                response = service.appointStoreManager(appointerName, appointeeName, Integer.parseInt(storeID));
            }
            case "addPermission" ->{
                String permitting = data.getProperty("permitting");
                String storeID = data.getProperty("storeID");
                String permitted = data.getProperty("permitted");
                String permission = data.getProperty("permission");

                response = service.addPermission(permitting, Integer.parseInt(storeID), permitted, PermissionsEnum.valueOf(permission));
            }
            case "removePermission" ->{
                String permitting = data.getProperty("permitting");
                String storeID = data.getProperty("storeID");
                String permitted = data.getProperty("permitted");
                String permission = data.getProperty("permission");

                response = service.removePermission(permitting, Integer.parseInt(storeID), permitted, PermissionsEnum.valueOf(permission));
            }
            case "removeManagerAppointment" ->{
                String appointerName = data.getProperty("appointerName");
                String appointeeName = data.getProperty("appointeeName");
                String storeID = data.getProperty("storeID");

                response = service.removeManagerAppointment(appointerName, appointeeName, Integer.parseInt(storeID));
            }
            case "getStoreWorkersDetails" ->{
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");

                response = service.getStoreWorkersDetails(username, Integer.parseInt(storeID));
            }
            case "getPurchaseDetails" ->{
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");

                response = service.getPurchaseDetails(username, Integer.parseInt(storeID));
            }
            case "getUserPermissions" ->{
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");

                response = service.getUserPermissions(username, Integer.parseInt(storeID));
            }
            case "getTotalStoreRevenue" -> {
                String username = data.getProperty("username");
                String storeID = data.getProperty("storeID");

                response = service.getTotalStoreRevenue(username, Integer.parseInt(storeID));
            }
            case "bidManagerReply" -> {
                String username = data.getProperty("username");
                String offeringUsername = data.getProperty("offeringUsername");
                String productID = data.getProperty("productID");
                String storeID = data.getProperty("storeID");
                String bidReply = data.getProperty("bidReply");

                System.out.println(offeringUsername);
                System.out.println(storeID);
                System.out.println(bidReply);
                System.out.println(Double.parseDouble(bidReply));

                response = service.bidManagerReply(username, offeringUsername, Integer.parseInt(productID), Integer.parseInt(storeID), Double.parseDouble(bidReply));
            }
            default -> response = super.handle(input);
        }

        return response;
    }

}
