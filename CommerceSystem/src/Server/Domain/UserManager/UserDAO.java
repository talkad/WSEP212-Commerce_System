package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    private List<String> admins;

    private ReadWriteLock lock;
    private Lock writeLock;
    private Lock readLock;

    private UserDAO(){

        this.registeredUsers = new ConcurrentHashMap<>();
        this.testManagers = new ConcurrentHashMap<>();
        this.testOwners = new ConcurrentHashMap<>();
        this.shoppingCarts = new ConcurrentHashMap<>();
        this.purchaseHistories = new ConcurrentHashMap<>();
        this.appointments = new ConcurrentHashMap<>();
        this.admins = new LinkedList<>();
        this.admins.add("shaked");

        lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
        readLock = lock.readLock();
    }

    public UserDTO getUser(String name){
        if(!registeredUsers.containsKey(name))
            return null;
        List<Integer> storesOwned = testOwners.get(name);
        if (storesOwned == null)
            storesOwned = new LinkedList<>();
        Map<Integer, List<Permissions>> storesManaged = testManagers.get(name);
        if (storesManaged == null)
            storesManaged = new ConcurrentHashMap<>();
        ShoppingCart shoppingCart = shoppingCarts.get(name);
        if (shoppingCart == null){
            shoppingCart = new ShoppingCart();
        }
        PurchaseHistory purchaseHistory = purchaseHistories.get(name);
        if (purchaseHistory == null){
            purchaseHistory = new PurchaseHistory();
        }
        Appointment appointment = appointments.get(name);
        if(appointment == null){
            appointment = new Appointment();
        }
        return new UserDTO(name, storesManaged, storesOwned, shoppingCart, purchaseHistory, appointment);
    }

    private static class CreateSafeThreadSingleton {
        private static final UserDAO INSTANCE = new UserDAO();
    }

    public static UserDAO getInstance()
    {
        return UserDAO.CreateSafeThreadSingleton.INSTANCE;
    }

    public void registerUser(String name, String password){
        this.registeredUsers.put(name, password);
        this.testManagers.put(name, new ConcurrentHashMap<>());
        this.testOwners.put(name, new LinkedList<>());
        this.shoppingCarts.put(name, new ShoppingCart());
        this.purchaseHistories.put(name, new PurchaseHistory());
    }

    public Response<Boolean> userExists(String name) {
        return new Response<>(this.registeredUsers.containsKey(name), !this.registeredUsers.containsKey(name), "username already exists");
    }

    public boolean validUser(String name, String password) {
        readLock.lock();
        boolean isValid = false;
        if(registeredUsers.get(name) != null) {
            isValid = registeredUsers.get(name).equals(password);
        }
        readLock.unlock();
        return isValid;
    }

    public Response<Boolean> addStoreOwned(String name, int storeId){
        Response<Boolean> result = new Response<>(false, true, "user doesn't exist");
        writeLock.lock();
        if(userExists(name).getResult()){
            this.testOwners.get(name).add(storeId);
            result = new Response<>(true, false, "Store added to owner's list");
        }
        writeLock.unlock();
        return result;
    }

    public Response<Boolean> addStoreManaged(String name, int storeId) {
        Response<Boolean> result = new Response<>(false, true, "user doesn't exist");
        writeLock.lock();
        if(userExists(name).getResult()){
            List<Permissions> permissions = new LinkedList<>();
            permissions.add(Permissions.RECEIVE_STORE_WORKER_INFO);
            this.testManagers.get(name).put(storeId, permissions);
            result = new Response<>(true, false, "Store added to manager's list");
        }
        writeLock.unlock();
        return result;
    }

    public Response<List<String>> getAppointments(String appointeeName, int storeID) {
        Response<List<String>> result;
        readLock.lock();
        if(this.appointments.containsKey(appointeeName)){
            result = this.appointments.get(appointeeName).getAppointees(storeID);
        }
        else {
            result = new Response<>(new LinkedList<>(), true, "User doesn't exist");
        }
        readLock.unlock();
        return result;
    }

    public void addAppointment(String name, int storeId, String appointee) {
        if(!this.appointments.containsKey(name)){
            this.appointments.put(name, new Appointment());
        }
        this.appointments.get(name).addAppointment(storeId, appointee);
    }

    public void removeAppointment(String appointerName, String appointeeName, int storeID) {
        writeLock.lock();
        if(this.appointments.containsKey(appointerName)) {
            this.appointments.get(appointerName).removeAppointment(storeID, appointeeName);
        }
        writeLock.unlock();
    }

    public void removeRole(String appointeeName, int storeID) {
        if(testManagers.containsKey(appointeeName) && testManagers.get(appointeeName).containsKey(storeID)) {
            testManagers.get(appointeeName).remove(storeID);
        }
        else if(testOwners.containsKey(appointeeName) && testOwners.get(appointeeName).contains(storeID)) {
            testOwners.get(appointeeName).remove(storeID);
        }
    }

    public void addPermission(int storeId, String permitted, Permissions permission) {
        this.testManagers.get(permitted).get(storeId).add(permission);
    }

    public void removePermission(int storeId, String permitted, Permissions permission) {
        this.testManagers.get(permitted).get(storeId).remove(permission);
    }

    public boolean isAdmin(String name){
        return this.admins.contains(name);
    }
}
