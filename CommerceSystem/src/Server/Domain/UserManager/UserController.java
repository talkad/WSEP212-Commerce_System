package Server.Domain.UserManager;


import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.StoreController;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserController {
    private AtomicInteger availableId;
    private Map<String, User> connectedUsers;
    private PurchaseController purchaseController;
    private PaymentSystemAdapter externalPayment;
    private ProductSupplyAdapter externalDelivery;

    private ReadWriteLock lock;
    private Lock writeLock;
    private Lock readLock;

    private UserController(){
        this.availableId = new AtomicInteger(1);
        this.connectedUsers = new ConcurrentHashMap<>();
        this.purchaseController = PurchaseController.getInstance();
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
        writeLock.lock();   // TODO check if write lock needed here
        connectedUsers.remove(logoutGuest);
        writeLock.unlock();
        return new Response<>(name, false, "disconnected user successfully");
    }

    private static class CreateSafeThreadSingleton {
        private static final UserController INSTANCE = new UserController();
    }

    public static UserController getInstance() {
        return UserController.CreateSafeThreadSingleton.INSTANCE;
    }

    public Response<Boolean> updateProductQuantity(String username, int storeID, int productID, int amount) {
        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();
            return user.updateProductQuantity(storeID, productID, amount);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Boolean> addProductReview(String username, int storeID, int productID, String review) {
        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();
            return user.addProductReview(storeID, productID, review);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Boolean> addProductsToStore(String username, ProductDTO productDTO, int amount) {
        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();
            return user.addProductsToStore(productDTO, amount);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Boolean> removeProductsFromStore(String username, int storeID, int productID, int amount) {
        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();
            return user.removeProductsFromStore(storeID, productID, amount);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Boolean> updateProductInfo(String username, int storeID, int productID, double newPrice, String newName) {
        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();
            return user.updateProductInfo(storeID, productID, newPrice, newName);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<String> addGuest(){
        String guestName = "Guest" + availableId.getAndIncrement();
        connectedUsers.put(guestName, new User());
        return new Response<>(guestName, false, "added guest");
    }

    public Response<Boolean> register(String prevName, String name, String password){
        //@TODO prevent invalid usernames (Guest...)
        readLock.lock();
        if(connectedUsers.containsKey(prevName)) {
            User user = connectedUsers.get(prevName);
            readLock.unlock();
            Response<Boolean> result = user.register();
            if (!result.isFailure()) {
                //readLock.lock();  // TODO check if needed
                result = UserDAO.getInstance().userExists(name);
                if (!result.getResult()) {
                    UserDAO.getInstance().registerUser(name, password);
                    result = new Response<>(true, false, "");
                }
                //readLock.unlock();
            }
            return result;
        }
        else {
            readLock.unlock();
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<String> login(String prevName, String name, String password){
        if (connectedUsers.containsKey(prevName)) {
            if (UserDAO.getInstance().validUser(name, password)) {
                writeLock.lock();
                connectedUsers.remove(prevName);
                UserDTO userDTO = UserDAO.getInstance().getUser(name);
                connectedUsers.put(name, new User(userDTO));
                writeLock.unlock();
                return new Response<>(name, false, "no error");
            } else {
                return new Response<>(name, true, "Failed to login user");
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<Boolean> addToCart(String userName, int storeID, int productID){
        readLock.lock();
        if(connectedUsers.containsKey(userName)) {
            User user = connectedUsers.get(userName);
            readLock.unlock();
            return user.addToCart(storeID, productID);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Map<Integer ,Map<ProductDTO, Integer>>> getShoppingCartContents(String userName){
        readLock.lock();
        if(connectedUsers.containsKey(userName)) {
            User user = connectedUsers.get(userName);
            readLock.unlock();
            return user.getShoppingCartContents();
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Boolean> removeProduct(String userName, int storeID, int productID){
        readLock.lock();
        if(connectedUsers.containsKey(userName)) {
            User user = connectedUsers.get(userName);
            readLock.unlock();
            return user.removeProduct(storeID, productID);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");

    }

    public Response<String> logout(String name) {
        Response<String> response;
        writeLock.lock();
        if(connectedUsers.containsKey(name)) {
            if (!connectedUsers.get(name).logout().isFailure()) {
                connectedUsers.remove(name);
                response = addGuest();
            } else {
                response = new Response<>(name, true, "User not permitted to logout");
            }
            writeLock.unlock();
            return response;
        }
        writeLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Integer> openStore(String userName, String storeName) {
        readLock.unlock();
        if(connectedUsers.containsKey(userName)) {
            User user = connectedUsers.get(userName);
            readLock.unlock();
            return user.openStore(storeName);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }


    public Response<List<PurchaseDTO>> getPurchaseHistoryContents(String userName){
        if(connectedUsers.containsKey(userName)) {
            User user = connectedUsers.get(userName);
            readLock.unlock();
            return user.getPurchaseHistoryContents();
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Boolean> appointOwner(String userName, String newOwner, int storeId) {
        readLock.lock();
        if(connectedUsers.containsKey(userName)) {
            User user = connectedUsers.get(userName);
            readLock.unlock();
            Response<Boolean> result = user.appointOwner(newOwner, storeId);
            if (!result.isFailure()) {
                writeLock.lock();
                if (this.connectedUsers.containsKey(newOwner)) {
                    connectedUsers.get(newOwner).addStoresOwned(storeId); //@TODO what about his permissions
                }
                writeLock.unlock();
            }
            return result;
        }
        else{
            readLock.unlock();
            return new Response<>(null, true, "User not connected");
        }
    }

    public Response<Boolean> appointManager(String userName, String newManager, int storeId) {
        readLock.lock();
        if(connectedUsers.containsKey(userName)) {
            User user = connectedUsers.get(userName);
            readLock.unlock();
            Response<Boolean> result = user.appointManager(newManager, storeId);
            if (!result.isFailure()) {
                writeLock.lock();
                List<Permissions> permissions = new Vector<>();
                permissions.add(Permissions.RECEIVE_STORE_WORKER_INFO);
                if (this.connectedUsers.containsKey(newManager)) {
                    connectedUsers.get(newManager).addStoresManaged(storeId, permissions); //@TODO list of permissions
                }
                writeLock.unlock();
            }
            return result;
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Boolean> removeOwnerAppointment(String appointerName, String appointeeName, int storeID){
        writeLock.lock();
        if(connectedUsers.containsKey(appointerName)) {
            //writeLock.lock();
            Response<Boolean> response;
            User appointer = new User(UserDAO.getInstance().getUser(appointerName));
            response = appointer.appointedAndAllowed(storeID, appointeeName, Permissions.REMOVE_OWNER_APPOINTMENT);
            if (!response.isFailure()) {
                User appointee = new User(UserDAO.getInstance().getUser(appointeeName));
                if (appointee.isOwner(storeID)) {
                    if (this.connectedUsers.containsKey(appointerName)) {
                        this.connectedUsers.get(appointerName).removeAppointment(appointeeName, storeID);
                    }
                    if (this.connectedUsers.containsKey(appointeeName)) {
                        this.connectedUsers.get(appointeeName).removeRole(storeID);
                    }
                    Response<List<String>> appointments = UserDAO.getInstance().getAppointments(appointeeName, storeID);
                    UserDAO.getInstance().removeAppointment(appointerName, appointeeName, storeID);
                    UserDAO.getInstance().removeRole(appointeeName, storeID);

                    List<String> names = new Vector<>(appointments.getResult());
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
        writeLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Boolean> removeManagerAppointment(String appointerName, String appointeeName, int storeID) {
        writeLock.lock();
        if(connectedUsers.containsKey(appointerName)) {
            //writeLock.lock();
            Response<Boolean> response;
            User appointer = new User(UserDAO.getInstance().getUser(appointerName));
            response = appointer.appointedAndAllowed(storeID, appointeeName, Permissions.REMOVE_MANAGER_APPOINTMENT);
            if (!response.isFailure()) {
                User appointee = new User(UserDAO.getInstance().getUser(appointeeName));
                if (appointee.isManager(storeID)) {
                    if (this.connectedUsers.containsKey(appointerName)) {
                        this.connectedUsers.get(appointerName).removeAppointment(appointeeName, storeID);
                    }
                    if (this.connectedUsers.containsKey(appointeeName)) {
                        this.connectedUsers.get(appointeeName).removeRole(storeID);
                    }
                    Response<List<String>> appointments = UserDAO.getInstance().getAppointments(appointeeName, storeID);
                    UserDAO.getInstance().removeAppointment(appointerName, appointeeName, storeID);
                    UserDAO.getInstance().removeRole(appointeeName, storeID);

                    List<String> names = new Vector<>(appointments.getResult());
                    for (String name : names) {
                        removeAppointmentRec(appointeeName, name, storeID);
                    }
                    response = new Response<>(true, false, appointments.getErrMsg());
                } else {
                    response = new Response<>(false, true, "Attempted to remove not a store manager");
                }
            }
            writeLock.unlock();
            return response;
        }
        writeLock.unlock();
        return new Response<>(null, true, "User not connected");
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

        List<String> names = new Vector<>(appointments);
        for(String name : names){
            removeAppointmentRec(appointeeName, name, storeID);                                 // recursive call
        }
    }

    public Response<Boolean> addPermission(String permitting, int storeId, String permitted, Permissions permission){
        readLock.lock();
        if(connectedUsers.containsKey(permitting)) {
            User user = connectedUsers.get(permitting);
            readLock.unlock();
            Response<Boolean> response = user.addPermission(storeId, permitted, permission);
            if (!response.isFailure()) {
                writeLock.lock();   // TODO read or write lock?
                if (connectedUsers.containsKey(permitted)) {
                    connectedUsers.get(permitted).addSelfPermission(storeId, permission);
                }
                writeLock.unlock();
            }
            return response;
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Boolean> removePermission(String permitting, int storeId, String permitted, Permissions permission){
        readLock.lock();
        if(connectedUsers.containsKey(permitting)) {
            User user = connectedUsers.get(permitting);
            readLock.unlock();
            Response<Boolean> response = user.removePermission(storeId, permitted, permission);
            if (!response.isFailure()) {
                writeLock.lock();
                if (connectedUsers.containsKey(permitted)) {
                    connectedUsers.get(permitted).removeSelfPermission(storeId, permission);
                }
                writeLock.unlock();
            }
            return response;
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }


    public Response<List<PurchaseDTO>> getUserPurchaseHistory(String adminName, String username) {
        if(connectedUsers.containsKey(adminName)) {
            User user = connectedUsers.get(adminName);
            readLock.unlock();
            return user.getUserPurchaseHistory(username);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Map<ProductDTO, Integer>> getStorePurchaseHistory(String adminName, int storeID) {
        readLock.lock();
        if(connectedUsers.containsKey(adminName)) {
            User user = connectedUsers.get(adminName);
            readLock.unlock();
            return user.getStorePurchaseHistory(storeID);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public void adminBoot() {
        String admin = "shaked";
        UserDAO.getInstance().registerUser(admin, Integer.toString("jacob".hashCode()));//TODO make this int and g through security scramble password
        UserDTO userDTO = UserDAO.getInstance().getUser(admin);
        connectedUsers.put(admin, new User(userDTO));
    }


    public Response<PurchaseDTO> getPurchaseDetails(String username, int storeID) {
        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();
            return user.getPurchaseDetails(storeID);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<List<User>> getStoreWorkersDetails(String username,int storeID) {
        if(!connectedUsers.get(username).getStoreWorkersDetails(storeID).isFailure()){
            String ownerName = StoreController.getInstance().getStoreOwnerName(storeID);
            List<User> result = new Vector<>();
            result.add(new User(UserDAO.getInstance().getUser(ownerName)));
            List<String> appointees = UserDAO.getInstance().getAppointments(username, storeID).getResult();
            List<String> names = new Vector<>(appointees);
            for(String name : names){
                appointees.addAll(getAppointeesNamesRec(name, storeID));
            }
            for(String name : appointees){
                result.add(new User(UserDAO.getInstance().getUser(name)));
            }
            return new Response<>(result, false, "Workers found");
        }
        return new Response<>(null, true, "User not permitted to get worker info");
    }

    private List<String> getAppointeesNamesRec(String workerName, int storeID) {
        List<String> appointees = UserDAO.getInstance().getAppointments(workerName, storeID).getResult();

        if(appointees != null && !appointees.isEmpty()){
            List<String> names = new Vector<>(appointees);
            for(String name : names){

                appointees.addAll(getAppointeesNamesRec(name, storeID));
            }
            return appointees;
        }
        return new Vector<>();
    }

    public Response<Boolean> purchase (int bankAccount, User user){
        return purchaseController.handlePayment(bankAccount, user);
    }

    public Map<String, User> getConnectedUsers() {
        return this.connectedUsers;
    }
}

