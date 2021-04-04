package Server.Domain.ShoppingManager;

import Server.Domain.CommonClasses.Response;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Inventory {

    // map productID to the amount of it
    private Map<Integer, Integer> pAmount;
    // map productID to Product
    private Map<Integer, Product> products;

    private final ReadWriteLock lock;

    public Inventory(){
        pAmount = new HashMap<>();
        products = new HashMap<>();
        lock = new ReentrantReadWriteLock();
    }


    public void addProducts(ProductDTO productDTO, int amount){
        int productID;
        Integer result;
        Product product = Product.createProduct(productDTO);

        lock.writeLock().lock();

        productID = product.getProductID();
        result = pAmount.putIfAbsent(productID, 0);  // result will be null if not exists

        pAmount.put(productID, pAmount.get(productID) + amount);

        if(result == null){
            products.put(productID, product);
        }
        lock.writeLock().unlock();
    }

    public Response<Boolean> removeProducts(int productID, int amount){
        Response<Boolean> res;
        int newAmount;

        lock.writeLock().lock();

        if(!pAmount.containsKey(productID)){
            res = new Response<>(false, true, "This product doesn't exist");
        }
        else {
            newAmount = pAmount.get(productID) - amount;

            if (newAmount > 0) {
                pAmount.put(productID, newAmount);
                res = new Response<>(true, false, "Product amount updated successfully");
            }
            else if (newAmount == 0) {
                pAmount.remove(productID);
                products.remove(productID);
                res = new Response<>(true, false, "Product is out of stock");
            }
            else {
                res = new Response<>(false, true, "Amount to remove exceeds amount in stock");
            }
        }

        lock.writeLock().unlock();
        return res;
    }

    public Collection<Product> getInventory(){
        Collection<Product> result;

        lock.readLock().lock();
        result = products.values();
        lock.readLock().unlock();

        return result;
    }

    public int getProductAmount(int productID){
        Integer result;

        lock.readLock().lock();
        result = pAmount.get(productID);
        lock.readLock().unlock();

        return result == null ? 0 : result;
    }

    public Response<Boolean> updateProductInfo(int productID, double newPrice, String newName) {
        Product product;

        lock.readLock().lock();
        product = products.get(productID);
        lock.readLock().unlock();

        if (product == null)
            return new Response<>(false, true, "This product doesn't exists");

        product.updatePrice(newPrice);
        product.updateName(newName);
        return new Response<>(true, false, "Updated info successfully");
    }

    public Response<Product> getProduct(int productID) {
        Product product;

        lock.readLock().lock();
        product = products.get(productID);
        lock.readLock().unlock();

        if(product == null){
            return new Response<>(null, true, "This product doesn't exists in the specified store");
        }
        else{
            return new Response<>(product, false, "success");
        }
    }

    public Response<Boolean> addProductReview(int productID, String review) {
        Response<Boolean> res;
        Product product;

        lock.writeLock().lock();
        product = products.get(productID);

        if(product == null){
            res = new Response<>(false, true, "This product doesn't exists");
        }
        else{
            res = product.addReview(review);
        }
        lock.writeLock().unlock();

        return res;
    }
}