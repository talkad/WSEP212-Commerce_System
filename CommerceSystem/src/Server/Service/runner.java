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

        Response<Boolean> res = new Response<>(false, true, "err msg");
    }
}
