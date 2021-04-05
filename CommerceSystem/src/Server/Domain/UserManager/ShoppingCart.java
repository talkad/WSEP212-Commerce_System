package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.StoreController;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ShoppingCart {

    // map storeID to its relevant shopping basket
    private Map<Integer, ShoppingBasket> baskets;
    private ReadWriteLock lock;


    public ShoppingCart(){
        this.baskets = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock();
    }

    public Response<Boolean> addProduct(int storeID, int productID){
        ShoppingBasket basket;
        Response<Boolean> res;
        Response<Product> productRes = StoreController.getInstance().getProduct(storeID, productID);

        if(productRes.isFailure()){
            res = new Response<>(false, true, productRes.getErrMsg());
        }
        else {
            lock.writeLock().lock();

            baskets.putIfAbsent(storeID, new ShoppingBasket(storeID));
            basket = baskets.get(storeID);
            res = basket.addProduct(productRes.getResult().getProductDTO());

            lock.writeLock().unlock();
        }

        return res;
    }

    public Response<Boolean> removeProduct(int storeID, int productID){
        ShoppingBasket basket;
        Response<Boolean> res;
        boolean guard;

        lock.readLock().lock();
        guard = !baskets.containsKey(storeID);
        lock.readLock().unlock();

        if(guard){
            res = new Response<>(false, true, "This store basket doesn't exists");
        }
        else{
            lock.writeLock().lock();

            basket = baskets.get(storeID);
            res = basket.removeProduct(productID);

            if(!res.isFailure() && basket.numOfProducts() == 0) // remove basket if it's empty
                baskets.remove(storeID);

            lock.writeLock().unlock();
        }

        return res;
    }

    /**
     * get DTO products from cart
     * @return map contains the storeID of a basket and its content
     */
    public Map<Integer, Map<ProductDTO, Integer>> getBaskets(){
        Map<Integer, Map<ProductDTO, Integer>> products = new HashMap<>();

        lock.readLock().lock();

        for(ShoppingBasket basket: baskets.values()){
            products.put(basket.getStoreID() ,basket.getProducts());
        }

        lock.readLock().unlock();

        return products;
    }

    public Response<Boolean> updateProductQuantity(int storeID, int productID, int amount) {
        ShoppingBasket basket;

        lock.readLock().lock();
        basket = baskets.get(storeID);
        lock.readLock().unlock();

        if(basket == null)
            return new Response<>(false, true, "This basket doesn't exists");

        return basket.updateProductQuantity(productID, amount);
    }

    public double getTotalPrice(){
        double totalPrice = 0;

        lock.readLock().lock();

        for(ShoppingBasket basket: baskets.values())
            totalPrice += basket.getTotalPrice();

        lock.readLock().unlock();

        return totalPrice;
    }

    @Override
    public String toString() {
        return "ShoppingCart:\n" +
                "baskets:\n" + baskets.toString();
    }

}
