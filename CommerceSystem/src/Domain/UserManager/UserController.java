package Domain.UserManager;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserController {
    private AtomicInteger availableId;
    private Map<String, User> connectedUsers;
    private UserController(){
        this.availableId = new AtomicInteger(1);
        this.connectedUsers = new ConcurrentHashMap<>();
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

    private void login(String name, String password, String prevName){
        if(connectedUsers.get(prevName).login(name, password)){
            connectedUsers.remove(prevName);
            connectedUsers.put(name, new User(name)); //@TODO what is user?
        }
        else {
            //@TODO ERROR MSG FAILED TO LOGIN
        }
    }

    public String logout(String name) {
        connectedUsers.remove(name);
        return addGuest();
    }
    public boolean createStore(String userName, String storeName) {
        return connectedUsers.get(userName).createStore(storeName);


//
//        import Domain.ExternalComponents.PaymentSystem;
//import Domain.ExternalComponents.ProductSupply;
//
//public class UserController {
//    PaymentSystemAdapter externalPayment;
//    ProductSupplyAdapter externalDelivery;
//
//    public UserController (){
//        this.externalPayment = new PaymentSystemAdapter(new PaymentSystem()); /* communication with external payment system */
//        this.externalDelivery = new ProductSupplyAdapter(new ProductSupply()); /* communication with external delivery system */
//    }

    }
}

