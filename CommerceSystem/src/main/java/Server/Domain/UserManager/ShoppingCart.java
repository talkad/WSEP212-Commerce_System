package Server.Domain.UserManager;

import Server.DAL.ShoppingBasketDTO;
import Server.DAL.ShoppingCartDTO;
import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.StoreController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
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

    public ShoppingCart(ShoppingCartDTO shoppingCartDTO){
        this.baskets = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock();

        List<ShoppingBasketDTO> basketsList = shoppingCartDTO.getBaskets();
        if(basketsList != null){
            for(ShoppingBasketDTO basket : basketsList){
                this.baskets.put(basket.getStoreID(), new ShoppingBasket(basket));
            }
        }
    }

    public ShoppingCartDTO toDTO(){
        List<ShoppingBasketDTO> basketsList = new Vector<>();

        for(int key : this.baskets.keySet()){
            basketsList.add(this.baskets.get(key).toDTO());
        }

        return new ShoppingCartDTO(basketsList);
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
            res = new Response<>(false, true, "ShoppingCart: This store basket doesn't exists");
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
    public Map<Integer, Map<ProductClientDTO, Integer>> getBaskets(){
        Map<Integer, Map<ProductClientDTO, Integer>> products = new HashMap<>();

        lock.readLock().lock();

        for(ShoppingBasket basket: baskets.values()){
            products.put(basket.getStoreID() ,basket.getProducts());
        }

        lock.readLock().unlock();

        return products;
    }

    /**
     * get DTO products from cart
     * @return map contains the storeID of a basket and its content
     */
    public Map<ProductClientDTO, Integer> getBasket(int storeID){
        Map<ProductClientDTO, Integer> products;

        lock.readLock().lock();
        products = new HashMap<>(baskets.get(storeID).getProducts());
        lock.readLock().unlock();

        return products;
    }

    public Response<Boolean> updateProductQuantity(int storeID, int productID, int amount) {
        ShoppingBasket basket;

        lock.readLock().lock();
        basket = baskets.get(storeID);
        lock.readLock().unlock();

        if(basket == null)
            return new Response<>(false, true, "ShoppingCart: This basket doesn't exists");

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
