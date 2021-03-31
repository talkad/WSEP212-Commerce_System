package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.StoreController;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User{

    private UserState state;
    private List<String> storesOwned;
    private List<String> storesManaged;
    private String name;
    private ShoppingCart shoppingCart;
    private PurchaseHistory purchaseHistory;

    private ReadWriteLock lock;
    private Lock writeLock;
    private Lock readLock;

    public User(){
        this.state = new Guest();

        lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
        readLock = lock.readLock();

        this.storesOwned = null;
        this.storesManaged = null;
        this.shoppingCart = new ShoppingCart();
        this.purchaseHistory = null;
    }

    public User(String name){
        lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
        readLock = lock.readLock();

        UserDTO userDTO = UserDAO.getInstance().getUser(name);
        this.storesOwned = userDTO.getStoresOwned();
        this.storesManaged = userDTO.getStoresManaged();
        this.name = name;
        this.shoppingCart = userDTO.getShoppingCart();
        this.purchaseHistory = userDTO.getPurchaseHistory();
        // @TODO roles = loadfromdb
    }

    public void changeState(FunctionName role){
        switch (role){
            case GUEST:
                state = new Guest();
                break;
//            case REGISTERED:
//                state = new Registered(); //@TODO Login info???
//                break;
        }
    }

    public boolean register(String name, String password) {
        if(!state.allowed(FunctionName.REGISTER, this.name)){
            return false;
        }
        boolean result = false;
        readLock.lock();
        if(UserDAO.getInstance().isUniqueName(name)) {
            UserDAO.getInstance().registerUser(name, password);
            result = true;
        }
        readLock.unlock();
        return result;
    }

    public boolean login(String name, String password){
        return UserDAO.getInstance().validUser(name, password);
    }

    public Response<Boolean> addToCart(Product product) {
        return this.shoppingCart.addProduct(product);
    }

    public List<Map<Product, Integer>> getShoppingCartContents() {
        List<Map<Product, Integer>> shoppingCartContents = new LinkedList<>();
        //@TODO sort out return type
        return shoppingCartContents;
    }

    public Response<Boolean> removeProduct(Product product) {
        return this.shoppingCart.removeProduct(product);
    }

    public void logout() {
        //@TODO final actions before logout
    }

    public boolean createStore(String storeName) {
        if (!this.state.allowed(FunctionName.CREATE_STORE, this.name)) {
            return false;
        }
        boolean result;
        result = StoreController.createStore(storeName);  //@TODO situate store creation process
        if(result) {
            this.storesOwned.add(storeName);
        }
        return result;
    }

    public List<Purchase> getPurchaseHistoryContents() {
        return this.purchaseHistory.getPurchases();
    }
}
