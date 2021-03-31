package Server.Domain.UserManager;


import Server.Domain.CommonClasses.Response;
import Server.Domain.ExternalComponents.PaymentSystem;
import Server.Domain.ExternalComponents.ProductSupply;
import Server.Domain.ShoppingManager.Product;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserController {
    private AtomicInteger availableId;
    private Map<String, User> connectedUsers;

    PaymentSystemAdapter externalPayment;
    ProductSupplyAdapter externalDelivery;

    private UserController(){
        this.availableId = new AtomicInteger(1);
        this.connectedUsers = new ConcurrentHashMap<>();

        this.externalPayment = new PaymentSystemAdapter(new PaymentSystem()); /* communication with external payment system */
        this.externalDelivery = new ProductSupplyAdapter(new ProductSupply()); /* communication with external delivery system */
    }
    private static class CreateSafeThreadSingleton {
        private static final UserController INSTANCE = new UserController();
    }

    public static UserController getInstance() {
        return UserController.CreateSafeThreadSingleton.INSTANCE;
    }

    private String addGuest(){
        String guestName = "Guest" + availableId.getAndIncrement();
        connectedUsers.put(guestName, new User());
        return guestName;
    }

    public boolean register(String userName, String name, String password){
        //@TODO prevent invalid usernames (Guest...)
        return connectedUsers.get(userName).register(name, password);
    }

    private void login(String name, String password, String prevName){
        if(connectedUsers.get(prevName).login(name, password)){
            connectedUsers.remove(prevName);
            connectedUsers.put(name, new User(name)); //@TODO what is user?
        }
        else {
            //@TODO ERROR MSG FAILED TO LOGIN
        }
    }

    public Response<Boolean> addToCart(String userName, Product product){
        return connectedUsers.get(userName).addToCart(product);
    }

    public List<Map<Product, Integer>> getShoppingCartContents(String userName){
        return connectedUsers.get(userName).getShoppingCartContents();
    }

    public Response<Boolean> removeProduct(String userName, Product product){
        return connectedUsers.get(userName).removeProduct(product);
    }

    public String logout(String name) {
        connectedUsers.get(name).logout();
        connectedUsers.remove(name);
        return addGuest();
    }

    public boolean createStore(String userName, String storeName) {
        return connectedUsers.get(userName).createStore(storeName);
    }

    public List<Purchase> getPurchaseHistoryContents(String userName){
        return connectedUsers.get(userName).getPurchaseHistoryContents();
    }
}

