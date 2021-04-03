package Server.Service;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.StoreController;
import Server.Domain.UserManager.UserController;
import Server.Domain.UserManager.UserDAO;

import java.util.List;

public class runner {
    public static void main(String[] args){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        commerceService.addGuest();
        commerceService.register("Guest1", "tal", "kadosh");
        System.out.println(commerceService.login("Guest1", "tal", "kadosh").getResult());
        commerceService.logout("tal");
        System.out.println(commerceService.register("Guest2", "yoni", "pis").isFailure());
        System.out.println(commerceService.login("Guest2", "yoni", "pis").isFailure());

        Response<Integer> res = commerceService.openStore("yoni", "eggStore");
        commerceService.appointStoreOwner("yoni", "tal", res.getResult());

        System.out.println(UserDAO.getInstance().getAppointments("yoni", res.getResult()).getResult().toString());
    }
}
