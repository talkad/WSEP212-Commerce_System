package Server.Domain.UserManager;


import Server.Domain.CommonClasses.Response;
import Server.Domain.ExternalComponents.PaymentSystem;
import Server.Domain.ExternalComponents.ProductSupply;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.ProductDTO;

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

    private PaymentSystemAdapter externalPayment;
    private ProductSupplyAdapter externalDelivery;

    private ReadWriteLock lock;
    private Lock writeLock;
    private Lock readLock;

    private UserController(){
        this.availableId = new AtomicInteger(1);
        this.connectedUsers = new ConcurrentHashMap<>();

        this.externalPayment = PaymentSystemAdapter.getInstance(); /* communication with external payment system */
        this.externalDelivery = ProductSupplyAdapter.getInstance(); /* communication with external delivery system */
        //todo check if successfully connected
        lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
        readLock = lock.readLock();
    }

    public Response<String> removeGuest(String name) {
        String logoutGuest = name;
        if(UserDAO.getInstance().userExists(name).getResult()){
            logoutGuest = logout(name).getResult();
        }
        connectedUsers.remove(logoutGuest);
        return new Response<>(name, false, "disconnected user successfully");
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

    public Response<Boolean> updateProductInfo(String username, int storeID, int productID, double newPrice, String newName) {
        return connectedUsers.get(username).updateProductInfo(storeID, productID, newPrice, newName);
    }

    public Response<String> addGuest(){
        String guestName = "Guest" + availableId.getAndIncrement();
        connectedUsers.put(guestName, new User());
        return new Response<>(guestName, false, "added guest");
    }

    public Response<Boolean> register(String prevName, String name, String password){
        //@TODO prevent invalid usernames (Guest...)
        Response<Boolean> result = connectedUsers.get(prevName).register();
        if(!result.isFailure()) {
            readLock.lock();
            result = UserDAO.getInstance().userExists(name);
            if (!result.getResult()) {
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
            connectedUsers.put(name, new User(userDTO));
            return new Response<>(name, false,"no error");
        }
        else {
           return new Response<>(name, true, "Failed to login user");
        }
    }

    public Response<Boolean> addToCart(String userName, Product product){
        return connectedUsers.get(userName).addToCart(product);
    }

    public Map<Integer ,Map<ProductDTO, Integer>> getShoppingCartContents(String userName){
        return connectedUsers.get(userName).getShoppingCartContents();
    }

    public Response<Boolean> removeProduct(String userName, Product product){
        return connectedUsers.get(userName).removeProduct(product);
    }

    public Response<String> logout(String name) {
        Response<String> response;
        if(!connectedUsers.get(name).logout().isFailure()) {
            connectedUsers.remove(name);
            response = addGuest();
        }
        else {
            response = new Response<>(name, true, "User not permitted to logout");
        }
        return response;
    }

    public Response<Integer> openStore(String userName, String storeName) {
        return connectedUsers.get(userName).openStore(storeName);
    }

    public List<Purchase> getPurchaseHistoryContents(String userName){
        return connectedUsers.get(userName).getPurchaseHistoryContents();
    }

    public Response<Boolean> appointOwner(String userName, String newOwner, int storeId) {
        Response<Boolean> result = connectedUsers.get(userName).appointOwner(newOwner, storeId);
        if(!result.isFailure()){
            writeLock.lock();
            if(this.connectedUsers.containsKey(newOwner)){
                connectedUsers.get(newOwner).addStoresOwned(storeId); //@TODO what about his permissions
            }
            writeLock.unlock();
        }
        return result;
    }

    public Response<Boolean> appointManager(String userName, String newManager, int storeId) {
        Response<Boolean> result = connectedUsers.get(userName).appointManager(newManager, storeId);
        if(!result.isFailure()){
            writeLock.lock();
            List<Permissions> permissions = new LinkedList<>();
            permissions.add(Permissions.RECEIVE_STORE_WORKER_INFO);
            if(this.connectedUsers.containsKey(newManager)){
                connectedUsers.get(newManager).addStoresManaged(storeId, permissions); //@TODO list of permissions
            }
            writeLock.unlock();
        }
        return result;
    }

    public Response<Boolean> removeOwnerAppointment(String appointerName, String appointeeName, int storeID){
        writeLock.lock();
        Response<Boolean> response;
        User appointer = new User(UserDAO.getInstance().getUser(appointerName));
        response = appointer.appointedAndAllowed(storeID, appointeeName, Permissions.REMOVE_OWNER_APPOINTMENT);
        if(!response.isFailure()) {
            User appointee = new User(UserDAO.getInstance().getUser(appointeeName));
            if (appointee.isOwner(storeID)) {
                if(this.connectedUsers.containsKey(appointerName)) {
                    this.connectedUsers.get(appointerName).removeAppointment(appointeeName, storeID);
                }
                if(this.connectedUsers.containsKey(appointeeName)){
                    this.connectedUsers.get(appointeeName).removeRole(storeID);
                }
                Response<List<String>> appointments = UserDAO.getInstance().getAppointments(appointeeName, storeID);
                UserDAO.getInstance().removeAppointment(appointerName, appointeeName, storeID);
                UserDAO.getInstance().removeRole(appointeeName, storeID);

                List<String> names = new LinkedList<>(appointments.getResult());
                for (String name : names) {
                    removeAppointmentRec(appointeeName, name, storeID);
                }
                response = new Response<>(true, false, appointments.getErrMsg());
            } else {
                response = new Response<>(false, true, "Attempted to remove not a store owner");
            }
        }
        writeLock.unlock();
        return response;
    }

    public Response<Boolean> removeManagerAppointment(String appointerName, String appointeeName, int storeID) {
        writeLock.lock();
        Response<Boolean> response;
        User appointer = new User(UserDAO.getInstance().getUser(appointerName));
        response = appointer.appointedAndAllowed(storeID, appointeeName, Permissions.REMOVE_MANAGER_APPOINTMENT);
        if(!response.isFailure()) {
            User appointee = new User(UserDAO.getInstance().getUser(appointeeName));
            if (appointee.isManager(storeID)) {
                if(this.connectedUsers.containsKey(appointerName)) {
                    this.connectedUsers.get(appointerName).removeAppointment(appointeeName, storeID);
                }
                if(this.connectedUsers.containsKey(appointeeName)){
                    this.connectedUsers.get(appointeeName).removeRole(storeID);
                }
                Response<List<String>> appointments = UserDAO.getInstance().getAppointments(appointeeName, storeID);
                UserDAO.getInstance().removeAppointment(appointerName, appointeeName, storeID);
                UserDAO.getInstance().removeRole(appointeeName, storeID);

                List<String> names = new LinkedList<>(appointments.getResult());
                for (String name : names) {
                    removeAppointmentRec(appointeeName, name, storeID);
                }
                response = new Response<>(true, false, appointments.getErrMsg());
            }
            else {
                response = new Response<>(false, true, "Attempted to remove not a store manager");
            }
        }
        writeLock.unlock();
        return response;
    }

    private void removeAppointmentRec(String appointerName, String appointeeName, int storeID) {
        if(this.connectedUsers.containsKey(appointerName)) {                                    // if the user is connected:
            this.connectedUsers.get(appointerName).removeAppointment(appointeeName, storeID);   // remove his appointee from his appointment list
        }
        if(this.connectedUsers.containsKey(appointeeName)){
            this.connectedUsers.get(appointeeName).removeRole(storeID);                         // remove his appointee's role from the appointee's list
        }
        List<String> appointments = UserDAO.getInstance().getAppointments(appointeeName, storeID).getResult();
        UserDAO.getInstance().removeAppointment(appointerName, appointeeName, storeID);         // remove appointee from the appointers list
        UserDAO.getInstance().removeRole(appointeeName, storeID);                               // remove appointee's role from his list

        List<String> names = new LinkedList<>(appointments);
        for(String name : names){
            removeAppointmentRec(appointeeName, name, storeID);                                 // recursive call
        }
    }

    public Response<Boolean> addPermission(String permitting, int storeId, String permitted, Permissions permission){
        Response<Boolean> response = connectedUsers.get(permitting).addPermission(storeId, permitted, permission);
        if(!response.isFailure()) {
            writeLock.lock();
            if (connectedUsers.containsKey(permitted)) {
                connectedUsers.get(permitted).addSelfPermission(storeId, permission);
            }
            writeLock.unlock();
        }
        return response;
    }

    public Response<Boolean> removePermission(String permitting, int storeId, String permitted, Permissions permission){
        Response<Boolean> response = connectedUsers.get(permitting).removePermission(storeId, permitted, permission);
        if(!response.isFailure()) {
            writeLock.lock();
            if (connectedUsers.containsKey(permitted)) {
                connectedUsers.get(permitted).removeSelfPermission(storeId, permission);
            }
            writeLock.unlock();
        }
        return response;
    }

    public Response<List<Purchase>> getUserPurchaseHistory(String adminName, String username) {
        return connectedUsers.get(adminName).getUserPurchaseHistory(username);
    }

    public void adminBoot() {
        String admin = "shaked";
        UserDAO.getInstance().registerUser(admin, Integer.toString("jacob".hashCode()));//TODO make this int and g through security scramble password
        UserDTO userDTO = UserDAO.getInstance().getUser(admin);
        connectedUsers.put(admin, new User(userDTO));
    }

    public Response<Purchase> getPurchaseDetails(String username, int storeID) {
        return connectedUsers.get(username).getPurchaseDetails(storeID);
    }

    public Response<UserDetails> getWorkersDetails(String username, int storeID) {
        return connectedUsers.get(username).getWorkersDetails(storeID);
    }
}

