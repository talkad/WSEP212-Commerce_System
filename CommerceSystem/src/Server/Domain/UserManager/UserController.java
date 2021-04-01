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

    public Response<Boolean> updateProductQuantity(String username, Product product, int amount) {
        return connectedUsers.get(username).updateProductQuantity(product, amount);
    }

    public Response<Boolean> addProductReview(String username, int productID, String review) {
        return connectedUsers.get(username).addProductReview(productID, review);
    }

    private static class CreateSafeThreadSingleton {
        private static final UserController INSTANCE = new UserController();
    }

    public static UserController getInstance() {
        return UserController.CreateSafeThreadSingleton.INSTANCE;
    }

    private Response<String> addGuest(){
        String guestName = "Guest" + availableId.getAndIncrement();
        connectedUsers.put(guestName, new User());
        return new Response<>(guestName, false, "idk"); //@TODO FIGURE OUT ERR MSG
    }

    public Response<Boolean> register(String prevName, String name, String password){
        //@TODO prevent invalid usernames (Guest...)
        return connectedUsers.get(prevName).register(name, password);
    }

    public Response<String> login(String prevName, String name, String password){
        if(connectedUsers.get(prevName).login(name, password)){
            connectedUsers.remove(prevName);
            connectedUsers.put(name, new User(name)); //@TODO what is user?
            return new Response<>(name, false,"null");
        }
        else {
           return new Response<>(name, true, "Failed to login user");
        }
    }

    public Response<Boolean> addToCart(String userName, Product product){
        return connectedUsers.get(userName).addToCart(product);
    }

    public Map<Integer ,Map<Product, Integer>> getShoppingCartContents(String userName){
        return connectedUsers.get(userName).getShoppingCartContents();
    }

    public Response<Boolean> removeProduct(String userName, Product product){
        return connectedUsers.get(userName).removeProduct(product);
    }

    public Response<String> logout(String name) {
        connectedUsers.get(name).logout();
        connectedUsers.remove(name);
        return addGuest();
    }

    public Response<Integer> openStore(String userName, String storeName) {
        return connectedUsers.get(userName).openStore(storeName);
    }

    public List<Purchase> getPurchaseHistoryContents(String userName){
        return connectedUsers.get(userName).getPurchaseHistoryContents();
    }
}

