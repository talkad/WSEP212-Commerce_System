package Server.Communication;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.UserManager.Permissions;
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

                response = service.addProductsToStore(username, gson.fromJson(productDTO, ProductDTO.class), Integer.parseInt(amount));
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
                String storeId = data.getProperty("storeId");
                String permitted = data.getProperty("permitted");
                String permission = data.getProperty("permission");

                response = service.addPermission(permitting, Integer.parseInt(storeId), permitted, gson.fromJson(permission, Permissions.class));
            }
            case "removePermission" ->{
                String permitting = data.getProperty("permitting");
                String storeId = data.getProperty("storeId");
                String permitted = data.getProperty("permitted");
                String permission = data.getProperty("permission");

                response = service.removePermission(permitting, Integer.parseInt(storeId), permitted, gson.fromJson(permission, Permissions.class));
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
            default -> response = new Response<>(false, true, "Action " + action + " does not exists");
        }

        return response.isFailure()? super.handle(input): response;
    }

}
