package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.StoreController;

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

    public Response<Boolean> addProduct(int storeID, int productID){
        ShoppingBasket basket;
        Response<Boolean> res;
        Response<Product> productRes = StoreController.getInstance().getProduct(storeID, productID);

        if(productRes.isFailure()){
            res = new Response<>(false, true, productRes.getErrMsg());
        }
        else {
            writeLock.lock();
            baskets.putIfAbsent(storeID, new ShoppingBasket(storeID));
            basket = baskets.get(storeID);
            res = basket.addProduct(productRes.getResult());
            writeLock.unlock();
        }

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
    public Map<Integer, Map<ProductDTO, Integer>> getBaskets(){
        Map<Integer, Map<ProductDTO, Integer>> products = new HashMap<>();
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

    public Response<Boolean> addReview(int productID, String review) {
        ShoppingBasket currBasket = null;

        for (ShoppingBasket basket : baskets.values()) {
            if (basket.isProductExists(productID).getResult()) {
                currBasket = basket;
                break;
            }
        }

        if (currBasket == null)
            return new Response<>(false, true, "This product doesn't exists in cart");
        return currBasket.addReview(productID, review);
    }

    public double getTotalPrice(){
        double totalPrice = 0;

        for(ShoppingBasket basket: baskets.values())
            totalPrice += basket.getTotalPrice();

        return totalPrice;
    }

    @Override
    public String toString() {
        return "ShoppingCart{" + "\n" +
                "baskets=" + baskets.toString() + "\n" +
                '}';
    }
}
