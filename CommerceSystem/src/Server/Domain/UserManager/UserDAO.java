package Server.Domain.UserManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserDAO {

    private Map<String, String> registeredUsers;
    private Map<String, List<String>> testManagers;
    private Map<String, List<String>> testOwners;
    private Map<String, ShoppingCart> shoppingCarts;
    private Map<String, PurchaseHistory> purchaseHistories;

    private ReadWriteLock lock;
    private Lock writeLock;
    private Lock readLock;

    // private Map<String, Map<String, Role>> userRoles;


    private UserDAO(){

        this.registeredUsers = new ConcurrentHashMap<>();
        this.testManagers = new ConcurrentHashMap<>();
        this.testOwners = new ConcurrentHashMap<>();
        this.shoppingCarts = new ConcurrentHashMap<>();
        this.purchaseHistories = new ConcurrentHashMap<>();

        lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
        readLock = lock.readLock();
    }

    public UserDTO getUser(String name){
        List<String> storesOwned = testOwners.get(name);
        if (storesOwned == null)
            storesOwned = new LinkedList<>();
        List<String> storesManaged = testManagers.get(name);
        if (storesManaged == null)
            storesManaged = new LinkedList<>();
        ShoppingCart shoppingCart = shoppingCarts.get(name);
        if (shoppingCart == null){
            shoppingCart = new ShoppingCart();
        }
        PurchaseHistory purchaseHistory = purchaseHistories.get(name);
        if (purchaseHistory == null){
            purchaseHistory = new PurchaseHistory();
        }
        return new UserDTO(name, storesManaged, storesOwned, shoppingCart, purchaseHistory);
    }

    private static class CreateSafeThreadSingleton {
        private static final UserDAO INSTANCE = new UserDAO();
    }

    public static UserDAO getInstance()
    {
        return UserDAO.CreateSafeThreadSingleton.INSTANCE;
    }

    public void registerUser(String name, String password){
        registeredUsers.put(name, password);
    }

    public boolean isUniqueName(String name) {
        return !this.registeredUsers.containsKey(name);
    }

    public boolean validUser(String name, String password) {
        readLock.lock();
        if(registeredUsers.get(name) != null) {
            boolean isValid = registeredUsers.get(name).equals(password);
            readLock.unlock();
            return isValid;
        }
        readLock.unlock();
        return false;
    }

//    public Map<String, Role> getRegisteredRoles(String name){
//        if(registeredUsers.containsKey(name)){
//            return userRoles.get(name);
//        }
//        //@TODO else(loadFromDB)
//        return new ConcurrentHashMap<>();
//    }
}
