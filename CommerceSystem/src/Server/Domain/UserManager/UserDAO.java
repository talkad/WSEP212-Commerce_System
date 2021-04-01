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
        this.registeredUsers.put(name, password);
        this.testManagers.put(name, new ConcurrentHashMap<>());
        this.testOwners.put(name, new LinkedList<>());
        this.shoppingCarts.put(name, new ShoppingCart());
        this.purchaseHistories.put(name, new PurchaseHistory());
    }

    public Response<Boolean> userExists(String name) {
        return new Response<>(true, this.registeredUsers.containsKey(name), "username already exists");
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
        if(!userExists(name).isFailure()){
            this.testOwners.get(name).add(storeId);
            result = new Response<>(true, false, "Store added to owner's list");
        }
        writeLock.unlock();
        return result;
    }

    public Response<Boolean> addStoreManaged(String name, int storeId) {
        Response<Boolean> result = new Response<>(false, true, "user doesn't exist");
        writeLock.lock();
        if(!userExists(name).isFailure()){
            this.testManagers.get(name).put(storeId, new LinkedList<>());   //@TODO list of permissions
            result = new Response<>(true, false, "Store added to manager's list");
        }
        writeLock.unlock();
        return result;
    }

//    public Map<String, Role> getRegisteredRoles(String name){
//        if(registeredUsers.containsKey(name)){
//            return userRoles.get(name);
//        }
//        //@TODO else(loadFromDB)
//        return new ConcurrentHashMap<>();
//    }
}
