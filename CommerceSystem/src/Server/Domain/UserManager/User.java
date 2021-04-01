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
    private List<Integer> storesOwned;
    private List<Integer> storesManaged;
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

//    public void changeState(FunctionName role){
//        switch (role){
//            case GUEST:
//                state = new Guest();
//                break;
////            case REGISTERED:
////                state = new Registered(); //@TODO Login info???
////                break;
//        }
//    }

    public Response<Boolean> register(String name, String password) {
        Response<Boolean> result = new Response<>(false, true, "Username is not unique");
        if(!state.allowed(FunctionName.REGISTER, this.name)){
            return new Response<>(false, true, "User not allowed to register");
        }
        readLock.lock();
        if(UserDAO.getInstance().isUniqueName(name)) {
            UserDAO.getInstance().registerUser(name, password);
            result = new Response<>(true, false, "");
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

    public Response<Integer> openStore(String storeName) {
        Response<Integer> result;
        if (!this.state.allowed(FunctionName.OPEN_STORE, this.name)) {
            return new Response<>(-1, true, "Not allowed to add store");
        }

        result = StoreController.getInstance().openStore(storeName);  //@TODO situate store creation process
        if(!result.isFailure()) {
            this.storesOwned.add(result.getResult());
        }
        return result;
    }

    public List<Purchase> getPurchaseHistoryContents() {
        return this.purchaseHistory.getPurchases();
    }
}
