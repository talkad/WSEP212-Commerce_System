package Server.Domain.UserManager;

import Server.DAL.*;
import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DiscountPolicy;
import Server.Domain.ShoppingManager.DiscountRules.DiscountRule;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.PurchasePolicy;
import Server.Domain.ShoppingManager.PurchaseRules.PurchaseRule;
import Server.Domain.ShoppingManager.StoreController;
import Server.Domain.UserManager.DTOs.BasketClientDTO;
import Server.Domain.UserManager.DTOs.PurchaseClientDTO;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import Server.Domain.UserManager.Purchase.PurchaseController;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserController {
    private AtomicInteger availableId;
    private PurchaseController purchaseController;
    private Map<String, User> connectedUsers;
    private Security security;
    private ReadWriteLock lock;
    private Lock writeLock;
    private Lock readLock;
    private Statistics stats;



    private UserController() {
        this.availableId = new AtomicInteger(1);

        this.purchaseController = PurchaseController.getInstance();
        this.security = Security.getInstance();
        this.connectedUsers = new ConcurrentHashMap<>();

        this.stats = Statistics.getInstance();

        lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
        readLock = lock.readLock();
    }

    public Response<String> removeGuest(String name) {

        String logoutGuest = name;
        // TODO may need to readd
//        if(UserDAO.getInstance().userExists(name)){
//            logoutGuest = logout(name).getResult();
//        }

        connectedUsers.remove(logoutGuest);

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

    public Response<Boolean> addProductsToStore(String username, ProductClientDTO productDTO, int amount) {

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
        User user = new User();
        user.setName(guestName);
        connectedUsers.put(guestName, user);
        stats.incDailyGuestCounter();
        return new Response<>(guestName, false, "added guest");
    }

    public Response<Boolean> register(String prevName, String name, String password){
        readLock.lock();
        if(connectedUsers.containsKey(prevName)) {
            User user = connectedUsers.get(prevName);
            readLock.unlock();
            if (!name.startsWith("Guest")){
                Response<Boolean> result = user.register();
                if (!result.isFailure()) {
                    writeLock.lock();  // TODO check if needed (prevents multiple registration)
                    if (DALService.getInstance().getAccount(name) == null) {
                        //UserDAO.getInstance().registerUser(name, security.sha256(password));
                        DALService.getInstance().addAccount(new AccountDTO(name, security.sha256(password)));
                        UserDTO userDTO = new UserDTO();
                        userDTO.setName(name);
                        userDTO.setState(UserStateEnum.REGISTERED);
                        DALService.getInstance().insertUser(userDTO);
                        writeLock.unlock();
                        result = new Response<>(true, false, "Registration occurred");
                    } else {
                        writeLock.unlock();
                        return new Response<>(false, true, "username already exists");
                    }
                }
                return result;
            }
            else return new Response<>(false, true, "error: cannot register user starting with the name Guest");
        }
        else {
            readLock.unlock();
            return new Response<>(null, true, "User not connected");
        }
    }
    
    private void checkCounterToInc(User user){
        if(user.isAdmin()){
            stats.incDailyAdminCounter();
        }
        else if(user.isOwner()){
            stats.incDailyOwnerCounter();
        }
        else if(user.isManager()){
            stats.incDailyManagerCounter();
        }
        else{
            stats.incDailyRegisteredCounter();
        }
    }

    public Response<String> login(String prevName, String name, String password){
        User user;

        if (connectedUsers.containsKey(prevName)) {
            if (prevName.startsWith("Guest")){
                if (this.isValidUser(name, security.sha256(password))) {
                    writeLock.lock();
                    connectedUsers.remove(prevName);
                    UserDTO userDTO = DALService.getInstance().getUser(name);
                    if(userDTO == null)
                        userDTO = new User(name).toDTO();
                    user = new User(userDTO);
                    checkCounterToInc(user);
                    connectedUsers.put(name, user);
                    writeLock.unlock();

//                    Notifier.getInstance().replaceIdentifier(prevName, name);
                    user.sendPendingNotifications();

                    return new Response<>(name, false, "Logged in successfully");
                } else {
                    return new Response<>(prevName, true, "Failed to login user");
                }
            }
            else {
                return new Response<>(null, true, "error: user must disconnect before trying to login");
            }
        }
        else {
            return new Response<>(null, true, "User not connected");
        }
    }

    public boolean isValidUser(String username, String password){
        AccountDTO accountDTO = DALService.getInstance().getAccount(username);
        return (accountDTO != null) && username.equals(accountDTO.getUsername()) && password.equals(accountDTO.getPassword());
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

    public Response<List<BasketClientDTO>> getShoppingCartContents(String userName){
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
                DALService.getInstance().insertUser(connectedUsers.get(name).toDTO());
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
        Response<Integer> response;

        readLock.lock();
        if(connectedUsers.containsKey(userName)) {
            User user = connectedUsers.get(userName);
            readLock.unlock();

            response = user.openStore(storeName);

            return response;
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }


    public Response<List<PurchaseClientDTO>> getPurchaseHistoryContents(String userName){
        readLock.lock();
        if(connectedUsers.containsKey(userName)) {
            User user = connectedUsers.get(userName);
            readLock.unlock();
            return user.getPurchaseHistoryContents();
        }
        readLock.unlock();
        return new Response<>(new LinkedList<>(), true, "User not connected");
    }

    public Response<Boolean> appointOwner(String userName, String newOwner, int storeId) {
        readLock.lock();
        if(connectedUsers.containsKey(userName)) {
            User user = connectedUsers.get(userName);
            readLock.unlock();
            writeLock.lock();
            Response<Boolean> result = user.appointOwner(newOwner, storeId);
            if (!result.isFailure()) {
                if (this.connectedUsers.containsKey(newOwner)) {
                    connectedUsers.get(newOwner).addStoresOwned(storeId);
                }
                // subscribe to get notifications
                //Publisher.getInstance().subscribe(storeId, newOwner);
            }
            writeLock.unlock();
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
            writeLock.lock();
            Response<Boolean> result = user.appointManager(newManager, storeId);
            if (!result.isFailure()) {
                List<PermissionsEnum> permissions = new Vector<>();
                permissions.add(PermissionsEnum.RECEIVE_STORE_WORKER_INFO);
                if (this.connectedUsers.containsKey(newManager)) {
                    connectedUsers.get(newManager).addStoresManaged(storeId, permissions);
                }
            }
            writeLock.unlock();
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
            UserDTO appointerDTO = DALService.getInstance().getUser(appointerName);
            User appointer = new User(appointerDTO);
            response = appointer.appointedAndAllowed(storeID, appointeeName, PermissionsEnum.REMOVE_OWNER_APPOINTMENT);
            if (!response.isFailure()) {
                UserDTO appointeeDTO = DALService.getInstance().getUser(appointeeName);
                User appointee = new User(appointeeDTO);
                if (appointee.isOwner(storeID)) {
                    if (this.connectedUsers.containsKey(appointerName)) {
                        this.connectedUsers.get(appointerName).removeAppointment(appointeeName, storeID);
                        appointer = this.connectedUsers.get(appointerName);
                        appointer.removeAppointment(appointeeName, storeID);
                        appointerDTO = appointer.toDTO();
                    }
                    else{
                        appointer.removeAppointment(appointeeName, storeID);
                        appointerDTO = appointer.toDTO();
                    }
                    if (this.connectedUsers.containsKey(appointeeName)) {
                        this.connectedUsers.get(appointeeName).removeRole(storeID);
                        appointee = this.connectedUsers.get(appointeeName);
                        appointee.removeRole(storeID);
                        appointeeDTO = appointee.toDTO();
                    }
                    else{
                        appointee.removeRole(storeID);
                        appointeeDTO = appointee.toDTO();
                    }

                    List<UserDTO> userDTOS = new Vector<>();
                    userDTOS.add(appointerDTO);
                    userDTOS.add(appointeeDTO);
                    DALService.getInstance().saveUsers(userDTOS);

                    Appointment appointments = new Appointment(appointeeDTO.getAppointments());
                    Response<List<String>> apptsList = appointments.getAppointees(storeID);
                    if(!apptsList.isFailure()) {
                        List<String> names = new Vector<>(apptsList.getResult());
                        for (String name : names) {
                            removeAppointmentRec(appointeeName, name, storeID);                                 // recursive call
                        }
                    }

                    //Publisher.getInstance().notify(appointeeName, "Your ownership canceled at store "+ appointeeName);
                    response = new Response<>(true, false, "Removed appointment successfully");
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
            UserDTO appointerDTO = DALService.getInstance().getUser(appointerName);
            User appointer = new User(appointerDTO);
            response = appointer.appointedAndAllowed(storeID, appointeeName, PermissionsEnum.REMOVE_MANAGER_APPOINTMENT);
            if (!response.isFailure()) {
                UserDTO appointeeDTO = DALService.getInstance().getUser(appointeeName);
                User appointee = new User(appointeeDTO);
                if (appointee.isManager(storeID)) {
                    if (this.connectedUsers.containsKey(appointerName)) {
                        this.connectedUsers.get(appointerName).removeAppointment(appointeeName, storeID);
                        appointer = this.connectedUsers.get(appointerName);
                        appointer.removeAppointment(appointeeName, storeID);
                        appointerDTO = appointer.toDTO();
                    }
                    else{
                        appointer.removeAppointment(appointeeName, storeID);
                        appointerDTO = appointer.toDTO();
                    }
                    if (this.connectedUsers.containsKey(appointeeName)) {
                        this.connectedUsers.get(appointeeName).removeRole(storeID);
                        appointee = this.connectedUsers.get(appointeeName);
                        appointee.removeRole(storeID);
                        appointeeDTO = appointee.toDTO();
                    }
                    else{
                        appointee.removeRole(storeID);
                        appointeeDTO = appointee.toDTO();
                    }

                    List<UserDTO> userDTOS = new Vector<>();
                    userDTOS.add(appointerDTO);
                    userDTOS.add(appointeeDTO);
                    DALService.getInstance().saveUsers(userDTOS);

                    Appointment appointments = new Appointment(appointeeDTO.getAppointments());
                    Response<List<String>> apptsList = appointments.getAppointees(storeID);
                    if(!apptsList.isFailure()) {
                        List<String> names = new Vector<>(apptsList.getResult());
                        for (String name : names) {
                            removeAppointmentRec(appointeeName, name, storeID);                                 // recursive call
                        }
                    }

                    //Publisher.getInstance().notify(appointeeName, "Your ownership canceled at store "+ appointeeName);
                    response = new Response<>(true, false, "Removed appointment successfully");
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
        UserDTO appointerDTO = DALService.getInstance().getUser(appointerName);
        UserDTO appointeeDTO = DALService.getInstance().getUser(appointeeName);
        User appointer = new User(appointerDTO);
        User appointee = new User(appointeeDTO);
        if(this.connectedUsers.containsKey(appointerName)) {                                    // if the user is connected:
            appointer = this.connectedUsers.get(appointerName);   // remove his appointee from his appointment list
        }
        if(this.connectedUsers.containsKey(appointeeName)){
            appointee = this.connectedUsers.get(appointeeName);                         // remove his appointee's role from the appointee's list
        }
        //List<String> appointments = UserDAO.getInstance().getAppointments(appointeeName, storeID).getResult();
//        appointer.removeAppointment(appointeeName, storeID);         // remove appointee from the appointers list
        Appointment appointment = appointer.getAppointments();
        appointment.removeAppointment(storeID, appointerName);
        appointer.setAppointments(appointment);

        appointee.notifyManagementCancellation(storeID);

        appointee.removeRole(storeID);                               // remove appointee's role from his list
        appointerDTO = appointer.toDTO();
        appointeeDTO = appointee.toDTO();

        List<UserDTO> userDTOS = new Vector<>();
        userDTOS.add(appointerDTO);
        userDTOS.add(appointeeDTO);
        DALService.getInstance().saveUsers(userDTOS);

        Appointment appointments = new Appointment(appointeeDTO.getAppointments());
        Response<List<String>> apptsList = appointments.getAppointees(storeID);
        if(!apptsList.isFailure()) {
            List<String> names = new Vector<>(apptsList.getResult());
            for (String name : names) {
                removeAppointmentRec(appointeeName, name, storeID);                                 // recursive call
            }
        }
    }

    public Response<Boolean> addPermission(String permitting, int storeId, String permitted, PermissionsEnum permission){
        readLock.lock();
        if(connectedUsers.containsKey(permitting)) {
            User user = connectedUsers.get(permitting);
            readLock.unlock();
            if(permission == PermissionsEnum.DAILY_VISITOR_STATISTICS || permission == PermissionsEnum.RECEIVE_GENERAL_REVENUE || permission == PermissionsEnum.RECEIVE_GENERAL_HISTORY){
                return new Response<>(false, true, "User not allowed to add this permission");
            }
            writeLock.lock();
            Response<Boolean> response = user.addPermission(storeId, permitted, permission);
            // todo why was this commented out?
            if (!response.isFailure()) {
                if (connectedUsers.containsKey(permitted)) {
                    connectedUsers.get(permitted).addSelfPermission(storeId, permission);
                }
            }
            writeLock.unlock();
            return response;
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Boolean> removePermission(String permitting, int storeId, String permitted, PermissionsEnum permission){
        readLock.lock();
        if(connectedUsers.containsKey(permitting)) {
            User user = connectedUsers.get(permitting);
            readLock.unlock();
            writeLock.lock();
            Response<Boolean> response = user.removePermission(storeId, permitted, permission);
            if (!response.isFailure() && connectedUsers.containsKey(permitted)) {
                connectedUsers.get(permitted).removeSelfPermission(storeId, permission);
            }
            writeLock.unlock();
            return response;
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<List<PurchaseClientDTO>> getUserPurchaseHistory(String adminName, String username) {
        readLock.lock();
        if(connectedUsers.containsKey(adminName)) {
            User user = connectedUsers.get(adminName);
            readLock.unlock();
            return user.getUserPurchaseHistory(username);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Collection<PurchaseClientDTO>> getStorePurchaseHistory(String adminName, int storeID) {
        readLock.lock();
        if(connectedUsers.containsKey(adminName)) {
            User user = connectedUsers.get(adminName);
            readLock.unlock();
            return user.getStorePurchaseHistory(storeID);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public void adminBoot(String username, String password) {
        DALService.getInstance().addAccount(new AccountDTO(username, security.sha256(password)));
        DALService.getInstance().addAdmin(new AdminAccountDTO(username));
        User user = new User(username);
        user.setState(new Admin());
        DALService.getInstance().insertUser(user.toDTO());
        connectedUsers.put(username, user);
    }


    public Response<Collection<PurchaseClientDTO>> getPurchaseDetails(String username, int storeID) {
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
            result.add(new User(DALService.getInstance().getUser(ownerName)));
            List<String> appointees = connectedUsers.get(username).getAppointments().getAppointees(storeID).getResult();
            List<String> names = new Vector<>(appointees);
            for(String name : names){
                appointees.addAll(getAppointeesNamesRec(name, storeID));
            }
            for(String name : appointees){
                result.add(new User(DALService.getInstance().getUser(name)));
            }
            return new Response<>(result, false, "Workers found");
        }
        return new Response<>(null, true, "User not permitted to get worker info");
    }

    private List<String> getAppointeesNamesRec(String workerName, int storeID) {
        List<String> appointees = new User(DALService.getInstance().getUser(workerName)).getAppointments().getAppointees(storeID).getResult();

        if(appointees != null && !appointees.isEmpty()){
            List<String> names = new Vector<>(appointees);
            for(String name : names){

                appointees.addAll(getAppointeesNamesRec(name, storeID));
            }
            return appointees;
        }
        return new Vector<>();
    }

    public Response<Boolean> purchase(String username, PaymentDetails paymentDetails, SupplyDetails supplyDetails){
        Response<List<PurchaseClientDTO>> purchaseRes;

        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);

            purchaseRes = purchaseController.handlePayment(user.getShoppingCart(), paymentDetails, supplyDetails);

            readLock.unlock();

            if(purchaseRes.isFailure())
                return new Response<>(false, true, purchaseRes.getErrMsg());

            return user.purchase(purchaseRes.getResult());
        }
        readLock.unlock();
        return new Response<>(null, true, "User is not connected");
    }

    public Map<String, User> getConnectedUsers() {
        return this.connectedUsers;
    }

    public User getUserByName(String username){
        return new User(DALService.getInstance().getUser(username));
    }

    public boolean isConnected(String username){
        return connectedUsers.containsKey(username);
    }

    public Response<List<String>> getUserPermissions(String username, int storeID){
        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();
            return user.getPermissions(storeID);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Boolean> addDiscountRule(String username, int storeID, DiscountRule discountRule){
        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();

            return user.addDiscountRule(storeID, discountRule);
        }
        readLock.unlock();
        return new Response<>(false, true, "User not connected");
    }

    public Response<Boolean> addPurchaseRule(String username, int storeID, PurchaseRule purchaseRule) {
        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();

            return user.addPurchaseRule(storeID, purchaseRule);
        }
        readLock.unlock();
        return new Response<>(false, true, "User not connected");
    }

    public Response<Boolean> removeDiscountRule(String username, int storeID, int discountRuleID)
    {
        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();

            return user.removeDiscountRule(storeID, discountRuleID);
        }
        readLock.unlock();
        return new Response<>(false, true, "User not connected");
    }

    public Response<Boolean> removePurchaseRule(String username, int storeID, int purchaseRuleID)
    {
        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();

            return user.removePurchaseRule(storeID, purchaseRuleID);
        }
        readLock.unlock();
        return new Response<>(false, true, "User not connected");
    }

    public Response<PurchasePolicy> getPurchasePolicy(String username, int storeID) {
        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();

            return user.getPurchasePolicy(storeID);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<DiscountPolicy> getDiscountPolicy(String username, int storeID) {
        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();

            return user.getDiscountPolicy(storeID);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Double> getTotalSystemRevenue(String username) {
        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();

            return user.getTotalSystemRevenue();
        }
        readLock.unlock();
        return new Response<>(-1.0, true, "User not connected");
    }

    public Response<Double> getTotalStoreRevenue(String username, int storeID) {
        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();

            return user.getTotalStoreRevenue(storeID);
        }
        readLock.unlock();
        return new Response<>(-1.0, true, "User not connected");
    }

    public Response<Boolean> bidManagerReply(String username, String offeringUsername, int productID, int storeID, double bidReply) {
        readLock.lock();
        Response<Boolean> res;
        if(connectedUsers.containsKey(username)) {//todo add to if connected as well
            User user = connectedUsers.get(username);
            readLock.unlock();
            res = user.changeOfferStatus(getUserByName(offeringUsername), productID, storeID, bidReply);
            if(!res.isFailure() && connectedUsers.containsKey(offeringUsername)){
                connectedUsers.get(offeringUsername).changeSelfOffer(productID, bidReply);
            }
            return res;
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Boolean> bidOffer(String username, int productID, int storeID, double priceOffer) {
        if(username.startsWith("Guest"))
            return new Response<>(false, true, "Only registered users can use bid offer functionally");

        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();
            List<String> appointees = new Vector<>();
            return user.bidOffer(productID, storeID, priceOffer, getBidPermiters(StoreController.getInstance().getStoreOwnerName(storeID), storeID));
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    private List<String> getBidPermiters(String storeOwnerName, int storeId) {
        List<String> approvals = new Vector<>();
        approvals.add(storeOwnerName);
        List<String> appointees = UserController.getInstance().getUserByName(storeOwnerName).getAppointments().getAppointees(storeId).getResult();
        if(appointees != null){
            for (String appointee: appointees){
                User u = UserController.getInstance().getUserByName(appointee);
                if(u.getStoresOwned().contains(storeId) || (u.getStoresManaged().containsKey(storeId) && u.getStoresManaged().get(storeId).contains(PermissionsEnum.REPLY_TO_BID))){
                    approvals.add(appointee);
                    recBidPermiters(appointee, storeId, approvals);
                }
            }
        }
        return approvals;
    }

    private void recBidPermiters(String name, int storeId, List<String> approvals){
        Appointment appointees = UserController.getInstance().getUserByName(name).getAppointments();
        if(appointees != null){
             List<String> appointeesList = appointees.getAppointees(storeId).getResult();
             if(appointeesList != null) {
                 for (String appointee : appointeesList) {
                     User u = UserController.getInstance().getUserByName(appointee);
                     if (u.getStoresOwned().contains(storeId) || (u.getStoresManaged().containsKey(storeId) && u.getStoresManaged().get(storeId).contains(PermissionsEnum.REPLY_TO_BID))) {
                         approvals.add(appointee);
                         recBidPermiters(appointee, storeId, approvals);
                     }
                 }
             }
        }
    }

    public Response<Boolean> bidUserReply(String username, int productID, int storeID, PaymentDetails paymentDetails, SupplyDetails supplyDetails) {
        readLock.lock();
        if(connectedUsers.containsKey(username)) {//todo add to if connected as well
            User user = connectedUsers.get(username);
            readLock.unlock();

            Offer offer = user.getOffers().get(productID);

            if(offer == null)
                return new Response<>(false, true, "The given product doesn't exists in offers");

            if(offer.getState() != OfferState.APPROVED)
                return new Response<>(false, true, "Your offer is not yet approved");

            Response<PurchaseClientDTO> purchase = PurchaseController.getInstance().purchaseProduct(productID, storeID, paymentDetails, supplyDetails, offer.getOfferReply());

            if(purchase.isFailure())
                return new Response<>(false, true, purchase.getErrMsg());

            return user.bidUserReply(purchase.getResult(), storeID);
        }

        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    // get all stores that the user is managing and owning
    public Response<List<Integer>> getStoreOwned(String username){
        readLock.lock();
        if(connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();
            return user.getStoresOwnedAndManaged();
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<List<Integer>> getDailyStatistics(String username, LocalDate date) {

        readLock.lock();
        if (connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();
            return user.getDailyStatistics(date);
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }

    public Response<Boolean> isAdmin(String username) {
        readLock.lock();
        if (connectedUsers.containsKey(username)) {
            User user = connectedUsers.get(username);
            readLock.unlock();

            return new Response<>(user.isAdmin(), !user.isAdmin(), "get is Admin");
        }
        readLock.unlock();
        return new Response<>(null, true, "User not connected");
    }
}
