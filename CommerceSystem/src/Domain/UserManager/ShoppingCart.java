package Domain.UserManager;

import Domain.CommonClasses.Response;
import Domain.ShoppingManager.Product;
import Domain.ShoppingManager.ProductDTO;

import java.util.LinkedList;
import java.util.List;
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

    public Response<Boolean> addProduct(int storeID, Product product){
        ShoppingBasket basket;
        Response<Boolean> res;

        writeLock.lock();
        baskets.putIfAbsent(storeID, new ShoppingBasket(storeID));
        basket = baskets.get(storeID);
        res = basket.addProduct(product);
        writeLock.unlock();

        return res;
    }

    public Response<Boolean> removeProduct(int storeID, Product product){
        ShoppingBasket basket;
        Response<Boolean> res;

        writeLock.lock();
        if(!baskets.containsKey(storeID)){
            res = new Response<Boolean>(false, true, "This store basket doesn't exists");
        }
        else{
            basket = baskets.get(storeID);
            res = basket.removeProduct(product);
        }
        writeLock.unlock();

        return res;
    }

    public List<ProductDTO> getProducts(){
        List<ProductDTO> products = new LinkedList<>();
        readLock.lock();

        for(ShoppingBasket basket: baskets.values()){
            products.addAll(basket.getProducts());
        }

        readLock.unlock();

        return products;
    }

}
