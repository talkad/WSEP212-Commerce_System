package Server.Service;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.StoreController;
import Server.Domain.UserManager.Permissions;
import Server.Domain.UserManager.User;
import Server.Domain.UserManager.UserController;
import Server.Domain.UserManager.UserDAO;

import java.util.List;

public class runner {
    public static void main(String[] args){
        CommerceService commerceService = CommerceService.getInstance();
        commerceService.init();
        commerceService.addGuest();
        // Guest1
        commerceService.register("Guest1", "tal", "kadosh");
        System.out.println(commerceService.logout("Guest1").isFailure());
        System.out.println(commerceService.login("Guest1", "tal", "kadosh").getResult());
        commerceService.logout("tal");
        //Guest2
        System.out.println(commerceService.register("Guest2", "yoni", "pis").isFailure());
        System.out.println(commerceService.login("Guest2", "yoni", "pis").isFailure());

        Response<Integer> res = commerceService.openStore("yoni", "eggStore");
        commerceService.appointStoreOwner("yoni", "tal", res.getResult());
        commerceService.logout("yoni");
        //Guest3
        System.out.println(commerceService.register("Guest3", "jacob", "lol").isFailure());
        System.out.println(commerceService.register("Guest3", "jacob2", "lol").isFailure());
        System.out.println(commerceService.register("Guest3", "jacob3", "lol").isFailure());
        System.out.println(commerceService.register("Guest3", "jacob4", "lol").isFailure());
        System.out.println(commerceService.login("Guest3", "tal", "kadosh").isFailure());
        commerceService.appointStoreOwner("tal", "jacob", res.getResult());
        commerceService.appointStoreOwner("tal", "jacob2", res.getResult());
        commerceService.appointStoreManager("tal", "jacob3", res.getResult());
        System.out.println(commerceService.appointStoreManager("tal", "jacob3", res.getResult()).isFailure() + " aaaaaaaaaaaaaaaa");
        commerceService.addPermission("tal", res.getResult(), "jacob3", Permissions.ADD_PRODUCT_TO_STORE);
        commerceService.removePermission("tal", res.getResult(), "jacob3", Permissions.ADD_PRODUCT_TO_STORE);
        commerceService.addPermission("tal", res.getResult(), "jacob3", Permissions.APPOINT_MANAGER);
        commerceService.removePermission("tal", res.getResult(), "jacob3", Permissions.APPOINT_MANAGER);

        commerceService.logout("tal");
        //Guest4
        System.out.println(commerceService.login("Guest4", "jacob3", "lol").isFailure());
        commerceService.appointStoreManager("jacob3", "jacob4", res.getResult());
        commerceService.logout("jacob3");
        //Guest5
        System.out.println(commerceService.login("Guest5", "yoni", "pis").isFailure());
        //commerceService.removeOwnerAppointment("yoni", "tal", res.getResult());
        for(User user : commerceService.getStoreWorkersDetails("yoni", res.getResult()).getResult()){
            System.out.println(user.getName());
        }
        commerceService.logout("yoni");
        //Guest6


        System.out.println(UserDAO.getInstance().getAppointments("yoni", res.getResult()).getResult().toString());
        System.out.println(UserDAO.getInstance().getAppointments("tal", res.getResult()).getResult().toString());
        System.out.println(UserDAO.getInstance().getAppointments("jacob3", res.getResult()).getResult().toString());

        System.out.println(UserDAO.getInstance().getUser("jacob3").getStoresManaged().get(res.getResult()));
        System.out.println(UserDAO.getInstance().getUser("jacob4").getStoresManaged().get(res.getResult()));
    }
}
