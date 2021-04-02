package Server.Domain.UserManager;


import Server.Domain.CommonClasses.Response;
import Server.Domain.ExternalComponents.PaymentSystem;
import Server.Domain.ExternalComponents.ProductSupply;
import Server.Domain.ShoppingManager.Product;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserController {
    private AtomicInteger availableId;
    private Map<String, User> connectedUsers;

    PaymentSystemAdapter externalPayment;
    ProductSupplyAdapter externalDelivery;

    private ReadWriteLock lock;
    private Lock writeLock;
    private Lock readLock;

    private UserController(){
        this.availableId = new AtomicInteger(1);
        this.connectedUsers = new ConcurrentHashMap<>();

        this.externalPayment = new PaymentSystemAdapter(new PaymentSystem()); /* communication with external payment system */
        this.externalDelivery = new ProductSupplyAdapter(new ProductSupply()); /* communication with external delivery system */

        lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
        readLock = lock.readLock();
    }

    private static class CreateSafeThreadSingleton {
        private static final UserController INSTANCE = new UserController();
    }

    public static UserController getInstance() {
        return UserController.CreateSafeThreadSingleton.INSTANCE;
    }

    public Response<Boolean> updateProductQuantity(String username, Product product, int amount) {
        return connectedUsers.get(username).updateProductQuantity(product, amount);
    }

    public Response<Boolean> addProductReview(String username, int productID, String review) {
        return connectedUsers.get(username).addProductReview(productID, review);
    }

    public Response<Boolean> addProductsToStore(String username, int storeID, Product product, int amount) {
        return connectedUsers.get(username).addProductsToStore(storeID, product, amount);
    }

    public Response<Boolean> removeProductsFromStore(String username, int storeID, Product product, int amount) {
        return connectedUsers.get(username).removeProductsFromStore(storeID, product, amount);
    }

    public Response<Boolean> updateProductPrice(String username, int storeID, int productID, int newPrice) {
        return connectedUsers.get(username).updateProductPrice(storeID, productID, newPrice);
    }

    private Response<String> addGuest(){
        String guestName = "Guest" + availableId.getAndIncrement();
        connectedUsers.put(guestName, new User());
        return new Response<>(guestName, false, "idk"); //@TODO FIGURE OUT ERR MSG
    }

    public Response<Boolean> register(String prevName, String name, String password){
        //@TODO prevent invalid usernames (Guest...)
        Response<Boolean> result = connectedUsers.get(prevName).register();
        if(!result.isFailure()) {
            readLock.lock();
            result = UserDAO.getInstance().userExists(name);
            if (!result.isFailure()) {
                UserDAO.getInstance().registerUser(name, password);
                result = new Response<>(true, false, "");
            }
            readLock.unlock();
        }
        return result;
    }

    public Response<String> login(String prevName, String name, String password){
        if(UserDAO.getInstance().validUser(name, password)){
            connectedUsers.remove(prevName);
            UserDTO userDTO = UserDAO.getInstance().getUser(name);
            connectedUsers.put(name, new User(userDTO)); //@TODO what is user?
            return new Response<>(name, false,"no error");
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

    //@TODO appointments need to connect to store's appointment tree
    public Response<Boolean> appointOwner(String userName, String newOwner, int storeId) {
        Response<Boolean> result = connectedUsers.get(userName).appointOwner(newOwner, storeId);
        if(!result.isFailure()){
            writeLock.lock();
            if(this.connectedUsers.containsKey(newOwner)){
                connectedUsers.get(newOwner).addStoresOwned(storeId);
            }
            writeLock.unlock();
        }
        return result;
    }

    public Response<Boolean> appointManager(String userName, String newManager, int storeId) {
        Response<Boolean> result = connectedUsers.get(userName).appointManager(newManager, storeId);
        if(!result.isFailure()){
            writeLock.lock();
            if(this.connectedUsers.containsKey(newManager)){
                connectedUsers.get(newManager).addStoresManaged(storeId, new LinkedList<>()); //@TODO list of permissions
            }
            writeLock.unlock();
        }
        return result;
    }

    public Response<Boolean> removeOwnerAppointment(String appointerName, String appointeeName, int storeID) {
        User appointee = new User(UserDAO.getInstance().getUser(appointeeName));
        if(appointee.isOwner(storeID)){

        }
        writeLock.lock();
        if(this.connectedUsers.containsKey(appointerName)) {
            this.connectedUsers.get(appointerName).removeOwnerAppointment(appointeeName, storeID);
        }
        UserDAO.getInstance().removeOwnerAppointment(appointerName, appointeeName, storeID);
        List<String> appointments = UserDAO.getInstance().getAppointments(appointeeName, storeID).getResult();
        if(appointments != null){
            for(String name : appointments){
                removeOwnerAppointment(appointeeName, name, storeID);
            }
        }
        writeLock.unlock();
        return new Response<>(true, false, "");
    }

    public Response<Boolean> removeManagerAppointment(String appointerName, String appointeeName, int storeID) {
        Response<String> initialRemoval = this.connectedUsers.get(appointerName).removeManagerAppointment(appointeeName, storeID);
    }

}

