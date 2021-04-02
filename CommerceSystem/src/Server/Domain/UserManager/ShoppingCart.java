package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ShoppingCart {

    // map storeID to its relevant shopping basket
    private Map<Integer, ShoppingBasket> baskets;
    private ReadWriteLock lock;
    private Lock writeLock;
    private Lock readLock;

    public ShoppingCart(){
        this.baskets = new ConcurrentHashMap<>();

        lock = new ReentrantReadWriteLock();
        writeLock = lock.writeLock();
        readLock = lock.readLock();
    }

    public Response<Boolean> addProduct(Product product){
        ShoppingBasket basket;
        Response<Boolean> res;
        int storeID = product.getStoreID();

        writeLock.lock();
        baskets.putIfAbsent(storeID, new ShoppingBasket(storeID));
        basket = baskets.get(storeID);
        res = basket.addProduct(product);
        writeLock.unlock();

        return res;
    }

    public Response<Boolean> removeProduct(Product product){
        ShoppingBasket basket;
        Response<Boolean> res;
        int storeID = product.getStoreID();

        writeLock.lock();
        if(!baskets.containsKey(storeID)){
            res = new Response<>(false, true, "This store basket doesn't exists");
        }
        else{
            basket = baskets.get(storeID);
            res = basket.removeProduct(product);
        }
        writeLock.unlock();

        return res;
    }

    /**
     * get DTO products from cart
     * @return map contains the storeID of a basket and its content
     */
    public Map<Integer, Map<Product, Integer>> getBaskets(){
        Map<Integer, Map<Product, Integer>> products = new HashMap<>();
        readLock.lock();

        for(ShoppingBasket basket: baskets.values()){
            products.put(basket.getStoreID() ,basket.getProducts());
        }

        readLock.unlock();

        return products;
    }

    public Response<Boolean> updateProductQuantity(Product product, int amount) {
        ShoppingBasket basket;

        readLock.lock();
        basket = baskets.get(product.getProductID());
        readLock.unlock();

        if(basket == null)
            return new Response<>(false, true, "This basket doesn't exists");

        return basket.updateProductQuantity(product, amount);
    }
}
