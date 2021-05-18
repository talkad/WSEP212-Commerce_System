package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.DTOs.UserDTO;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserDAO {

    private Map<String, String> registeredUsers;
    private Map<String, Map<Integer, List<Permissions>>> testManagers;
    private Map<String, List<Integer>> testOwners;
    private Map<String, ShoppingCart> shoppingCarts;
    private Map<String, PurchaseHistory> purchaseHistories;
    private Map<String, Appointment> appointments;
    private Map<String, Map<Integer, Offer>> offers;
    private Map<String, PendingMessages> pendingMessages;
    private List<String> admins;

    private ReadWriteLock registeredLock;
    private Lock registeredWriteLock;
    private Lock registeredReadLock;

    private ReadWriteLock managersLock;
    private Lock managersWriteLock;
    private Lock managersReadLock;

    private ReadWriteLock ownersLock;
    private Lock ownersWriteLock;
    private Lock ownersReadLock;

    private ReadWriteLock cartsLock;
    private Lock cartsWriteLock;
    private Lock cartsReadLock;

    private ReadWriteLock historiesLock;
    private Lock historiesWriteLock;
    private Lock historiesReadLock;

    private ReadWriteLock appointmentsLock;
    private Lock appointmentsWriteLock;
    private Lock appointmentsReadLock;

    private ReadWriteLock offersLock;
    private Lock offersWriteLock;
    private Lock offersReadLock;

    private ReadWriteLock pendingLock;
    private Lock pendingWriteLock;
    private Lock pendingReadLock;

    private ReadWriteLock adminsLock;
    private Lock adminsWriteLock;
    private Lock adminsReadLock;

    private UserDAO(){

        this.registeredUsers = new ConcurrentHashMap<>();
        this.testManagers = new ConcurrentHashMap<>();
        this.testOwners = new ConcurrentHashMap<>();
        this.shoppingCarts = new ConcurrentHashMap<>();
        this.purchaseHistories = new ConcurrentHashMap<>();
        this.pendingMessages = new ConcurrentHashMap<>();
        this.appointments = new ConcurrentHashMap<>();
        this.offers = new ConcurrentHashMap<>();
        this.admins = new Vector<>();
        this.admins.add("shaked");

        registeredLock = new ReentrantReadWriteLock();
        registeredWriteLock = registeredLock.writeLock();
        registeredReadLock = registeredLock.readLock();

        managersLock = new ReentrantReadWriteLock();
        managersWriteLock = managersLock.writeLock();
        managersReadLock = managersLock.readLock();

        ownersLock = new ReentrantReadWriteLock();
        ownersWriteLock = ownersLock.writeLock();
        ownersReadLock = ownersLock.readLock();

        cartsLock = new ReentrantReadWriteLock();
        cartsWriteLock = cartsLock.writeLock();
        cartsReadLock = cartsLock.readLock();

        historiesLock = new ReentrantReadWriteLock();
        historiesWriteLock = historiesLock.writeLock();
        historiesReadLock = historiesLock.readLock();

        appointmentsLock = new ReentrantReadWriteLock();
        appointmentsWriteLock = appointmentsLock.writeLock();
        appointmentsReadLock = appointmentsLock.readLock();

        adminsLock = new ReentrantReadWriteLock();
        adminsWriteLock = adminsLock.writeLock();
        adminsReadLock = adminsLock.readLock();

        offersLock = new ReentrantReadWriteLock();
        offersWriteLock = offersLock.writeLock();
        offersReadLock = offersLock.readLock();

        pendingLock = new ReentrantReadWriteLock();
        pendingWriteLock = pendingLock.writeLock();
        pendingReadLock = pendingLock.readLock();
    }

    public UserDTO getUser(String name){
        UserDTO user = null;
        registeredReadLock.lock();
        ownersReadLock.lock();
        managersReadLock.lock();
        cartsReadLock.lock();
        historiesReadLock.lock();
        appointmentsReadLock.lock();
        offersReadLock.lock();
        pendingReadLock.lock();
        if(registeredUsers.containsKey(name)) {
            List<Integer> storesOwned = testOwners.get(name);
            if (storesOwned == null)
                storesOwned = new Vector<>();
            Map<Integer, List<Permissions>> storesManaged = testManagers.get(name);
            if (storesManaged == null)
                storesManaged = new ConcurrentHashMap<>();
            ShoppingCart shoppingCart = shoppingCarts.get(name);
            if (shoppingCart == null) {
                shoppingCart = new ShoppingCart();
            }
            PurchaseHistory purchaseHistory = purchaseHistories.get(name);
            if (purchaseHistory == null) {
                purchaseHistory = new PurchaseHistory();
            }
            PendingMessages pendindMSG = pendingMessages.get(name);
            if (pendindMSG == null) {
                pendindMSG = new PendingMessages();
            }
            Appointment appointment = appointments.get(name);
            if (appointment == null) {
                appointment = new Appointment();
            }
            Map<Integer, Offer> offer = offers.get(name);
            if (offer == null) {
                offer = new ConcurrentHashMap<>();
            }
            user = new UserDTO(name, storesManaged, storesOwned, shoppingCart, purchaseHistory, appointment, offer, pendindMSG);
        }
        pendingReadLock.unlock();
        offersReadLock.unlock();
        appointmentsReadLock.unlock();
        historiesReadLock.unlock();
        cartsReadLock.unlock();
        managersReadLock.unlock();
        ownersReadLock.unlock();
        registeredReadLock.unlock();
        return user;
    }

    private static class CreateSafeThreadSingleton {
        private static final UserDAO INSTANCE = new UserDAO();
    }

    public static UserDAO getInstance()
    {
        return UserDAO.CreateSafeThreadSingleton.INSTANCE;
    }

    public void registerUser(String name, String password){
        registeredWriteLock.lock();
        this.registeredUsers.put(name, password);
        registeredWriteLock.unlock();

        managersWriteLock.lock();
        this.testManagers.put(name, new ConcurrentHashMap<>());
        managersWriteLock.unlock();

        ownersWriteLock.lock();
        this.testOwners.put(name, new Vector<>());
        ownersWriteLock.unlock();

        cartsWriteLock.lock();
        this.shoppingCarts.put(name, new ShoppingCart());
        cartsWriteLock.unlock();

        historiesWriteLock.lock();
        this.purchaseHistories.put(name, new PurchaseHistory());
        historiesWriteLock.unlock();

        offersWriteLock.lock();
        this.offers.put(name, new ConcurrentHashMap<>());
        offersWriteLock.unlock();

        pendingWriteLock.lock();
        this.pendingMessages.put(name, new PendingMessages());
        pendingWriteLock.unlock();
    }

    public boolean userExists(String name) {
        return this.registeredUsers.containsKey(name);
    }

    public boolean validUser(String name, String password) {
        registeredReadLock.lock();
        boolean isValid = false;
        if(registeredUsers.get(name) != null) {
            isValid = registeredUsers.get(name).equals(password);
        }
        registeredReadLock.unlock();
        return isValid;
    }

    public Response<Boolean> addStoreOwned(String name, int storeId){
        Response<Boolean> result = new Response<>(false, true, "user doesn't exist");
        ownersWriteLock.lock();
        if(userExists(name)){
            this.testOwners.get(name).add(storeId);
            result = new Response<>(true, false, "Store added to owner's list");
        }
        ownersWriteLock.unlock();
        return result;
    }

    public Response<Boolean> addStoreManaged(String name, int storeId) {
        Response<Boolean> result = new Response<>(false, true, "user doesn't exist");
        managersWriteLock.lock();
        if(userExists(name)){
            List<Permissions> permissions = new Vector<>();
            permissions.add(Permissions.RECEIVE_STORE_WORKER_INFO);
            this.testManagers.get(name).put(storeId, permissions);
            result = new Response<>(true, false, "Store added to manager's list");
        }
        managersWriteLock.unlock();
        return result;
    }

    public Response<List<String>> getAppointments(String appointeeName, int storeID) {
        Response<List<String>> result;
        appointmentsReadLock.lock();
        if(this.appointments.containsKey(appointeeName)){
            result = this.appointments.get(appointeeName).getAppointees(storeID);
        }
        else {
            result = new Response<>(new Vector<>(), true, "User doesn't exist");
        }
        appointmentsReadLock.unlock();
        return result;
    }

    public void addAppointment(String name, int storeId, String appointee) {
        appointmentsWriteLock.lock();
        if(!this.appointments.containsKey(name)){
            this.appointments.put(name, new Appointment());
        }
        this.appointments.get(name).addAppointment(storeId, appointee);
        appointmentsWriteLock.unlock();
    }

    public void removeAppointment(String appointerName, String appointeeName, int storeID) {
        appointmentsWriteLock.lock();
        if(this.appointments.containsKey(appointerName)) {
            this.appointments.get(appointerName).removeAppointment(storeID, appointeeName);
        }
        appointmentsWriteLock.unlock();
    }

    public void removeRole(String appointeeName, int storeID) {
        managersWriteLock.lock();
        if(testManagers.containsKey(appointeeName) && testManagers.get(appointeeName).containsKey(storeID)) {
            testManagers.get(appointeeName).remove(storeID);
            managersWriteLock.unlock();
        }
        else {
            managersWriteLock.unlock();
            ownersWriteLock.lock();
            if(testOwners.containsKey(appointeeName) && testOwners.get(appointeeName).contains(storeID)) {
                testOwners.get(appointeeName).remove(Integer.valueOf(storeID));
            }
            ownersWriteLock.unlock();
        }
    }

    public void addPermission(int storeId, String permitted, Permissions permission) {
        managersWriteLock.lock();
        if(this.testManagers.containsKey(permitted)) {
            this.testManagers.get(permitted).get(storeId).add(permission);
        }
        managersWriteLock.unlock();
    }

    public void removePermission(int storeId, String permitted, Permissions permission) {
        managersWriteLock.lock();
        if(this.testManagers.containsKey(permitted)) {
            this.testManagers.get(permitted).get(storeId).remove(permission);
        }
        managersWriteLock.unlock();
    }

    public boolean ownedOrManaged(int storeId, String newOwnerOrManager){
        boolean result = false;
        managersReadLock.lock();
        if(this.testManagers.containsKey(newOwnerOrManager)){
            result = this.testManagers.get(newOwnerOrManager).containsKey(storeId);
            if (result) {
                managersReadLock.unlock();
                return result;
            }
        }
        managersReadLock.unlock();
        ownersReadLock.lock();
        if(this.testOwners.containsKey(newOwnerOrManager)){
            result = this.testOwners.get(newOwnerOrManager).contains(storeId);
        }
        ownersReadLock.unlock();
        return result;
    }

    public Response<Boolean> addOffer(String name, int productID, int storeID, double priceOffer, OfferState state){
        Response<Boolean> result = new Response<>(false, true, "user doesn't exist");
        offersWriteLock.lock();
        if(userExists(name)){
            this.offers.get(name).put(productID, new Offer(productID, storeID, priceOffer, state));
            result = new Response<>(true, false, "Offer added to the user's list");
        }
        offersWriteLock.unlock();
        return result;
    }

    public Response<Boolean> removeOffer(String name, int productID){
        Response<Boolean> result = new Response<>(false, true, "user doesn't exist");
        offersWriteLock.lock();
        if(userExists(name)){
            this.offers.get(name).remove(productID);
            result = new Response<>(true, false, "Offer removed from the user's list");
        }
        offersWriteLock.unlock();
        return result;
    }

    public Response<Boolean> changeStatus(String offeringUsername, int productID, double bidReply, OfferState state){
        Response<Boolean> result = new Response<>(false, true, "user doesn't exist");
        offersWriteLock.lock();
        if(userExists(offeringUsername)){
            if(bidReply > 0){
                this.offers.get(offeringUsername).get(productID).setOfferReply(bidReply);
            }
            this.offers.get(offeringUsername).get(productID).setState(state);
            result = new Response<>(true, false, "Successfully updated offer status.");
        }
        offersWriteLock.unlock();
        return result;
    }

    public boolean isAdmin(String name){
        return this.admins.contains(name);
    }
}
