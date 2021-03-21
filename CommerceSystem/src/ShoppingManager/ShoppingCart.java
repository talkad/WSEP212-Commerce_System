package ShoppingManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ShoppingCart {

    // the user that this cart is belong to
    private String username;
    // map storeID to its relevant shopping basket
    private Map<Integer, ShoppingBasket> baskets;

    private ReadWriteLock lock;
    private Lock writeLock;
    private Lock readLock;

    public ShoppingCart(String username){
        this.username = username;
        this.baskets = new ConcurrentHashMap<>();

        lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
        readLock = lock.readLock();
    }

    public void addProduct(int storeID, Product product){
        writeLock.lock();
        // ...
        writeLock.unlock();
    }

    public void removeProduct(int storeID, Product product){
        writeLock.lock();
        // ...
        writeLock.unlock();
    }

    public void getProducts(){
        readLock.lock();
        // ...
        readLock.unlock();
    }

}
