package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ShoppingBasket {

    // the store these products are belong to
    private final int storeID;
    private Map<Integer, ProductDTO> products;
    // map between productID and its amount in the basket
    private Map<Integer, Integer> pAmount;
    private double totalPrice;
    private ReadWriteLock lock;

    public ShoppingBasket(int storeID){
        this.storeID = storeID;

        this.products = new ConcurrentHashMap<>();
        this.pAmount = new ConcurrentHashMap<>();
        this.totalPrice = 0;
        this.lock = new ReentrantReadWriteLock();
    }

    public Response<Boolean> addProduct(ProductDTO product){
        Response<Boolean> res;
        int productID = product.getProductID();

        if(product.getStoreID() != storeID){ //double check
            res = new Response<>(false, true, "CRITICAL: Product "+product.getName()+" isn't from store");
        }
        else{

            lock.writeLock().lock();
            if(!pAmount.containsKey(productID)){
                products.put(productID, product);
                pAmount.put(productID, 1);
            }
            else{
                pAmount.put(productID, pAmount.get(productID) + 1);
            }

            totalPrice += product.getPrice();
            lock.writeLock().unlock();

            res = new Response<>(true, false, "ShoppingBasket: Product "+product.getName()+" added to shopping basket");
        }

        return res;
    }

    public Response<Boolean> removeProduct(int productID) {
        Response<Boolean> res;
        ProductDTO product;

        lock.readLock().lock();
        product = products.get(productID);
        lock.readLock().unlock();

        if(product == null){
            res = new Response<>(false, true, "ShoppingBasket: The given product doesn't exists");
        }
        else{
            lock.writeLock().lock();

            pAmount.put(productID, pAmount.get(productID) - 1);

            if(pAmount.get(productID) == 0)
                products.remove(productID);

            totalPrice -= product.getPrice();
            res = new Response<>(true, false, "ShoppingBasket: Product "+product.getName()+" removed from shopping basket");

            lock.writeLock().unlock();
        }

        return res;
    }

    public Map<ProductDTO, Integer> getProducts() {
        Map<ProductDTO, Integer> basketProducts = new HashMap<>();

        lock.readLock().lock();
        for(ProductDTO product: products.values()){
            basketProducts.put(product, pAmount.get(product.getProductID()));
        }
        lock.readLock().unlock();

        return basketProducts;
    }

    public int getStoreID() {
        return storeID;
    }

    public Response<Boolean> updateProductQuantity(int productID, int amount) {
        Response<Boolean> res;
        int prevAmount;
        ProductDTO product;

        if(amount < 0){
            res = new Response<>(false, true, "ShoppingBasket: The amount can't be negative");
        }
        else if(!pAmount.containsKey(productID)){
            res = new Response<>(false, true, "ShoppingBasket: The product doesn't exists in the given basket");
        }
        else{
            lock.writeLock().lock();

            prevAmount =pAmount.get(productID);
            pAmount.put(productID, amount);
            product = products.get(productID);

            if(product == null){
                res = new Response<>(false, true, "ShoppingBasket: The given product doesn't exists");
            }
            else {
                if(pAmount.get(productID) == 0)
                    products.remove(productID);

                totalPrice += (amount - prevAmount)*product.getPrice();

                res = new Response<>(true, false, "ShoppingBasket: Product "+product.getName()+" amount updated");
            }

            lock.writeLock().unlock();
        }

        return res;
    }

    public double getTotalPrice(){
        return totalPrice;
    }

    @Override
    public String toString() {
        Map<ProductDTO, Integer> products = getProducts();
        StringBuilder result = new StringBuilder("ShoppingBasket id " + storeID + ":\n");

        for(ProductDTO product: products.keySet()){
            result.append(product.toString()).append("Amount :").append(products.get(product)).append("\n");
        }

        return result.toString();
    }

    public int numOfProducts() {
        int length;

        lock.readLock().lock();
        length = products.size();
        lock.readLock().unlock();

        return length;
    }

    public int getProductAmount(int productID){
        int amount;

        lock.readLock().lock();
        amount = pAmount.get(productID);
        lock.readLock().unlock();

        return amount;
    }
}
