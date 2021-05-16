package Server.Domain.ShoppingManager;

import Server.Domain.CommonClasses.Response;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Inventory {

    // map productID to the amount of it
    private Map<Integer, Integer> pAmount;
    // map productID to Product
    private Map<Integer, Product> products;

    private final ReadWriteLock lock;
    private final ReadWriteLock purchaseLock; // changed to regular lock

    public Inventory(){
        pAmount = new HashMap<>();
        products = new HashMap<>();
        lock = new ReentrantReadWriteLock();
        purchaseLock = new ReentrantReadWriteLock();
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
            res = new Response<>(false, true, "Inventory: This product doesn't exist");
        }
        else {
            newAmount = pAmount.get(productID) - amount;

            if (newAmount > 0) {
                pAmount.put(productID, newAmount);
                res = new Response<>(true, false, "Inventory: Product amount updated successfully");
            }
            else if (newAmount == 0) {
                pAmount.remove(productID);
                products.remove(productID);
                res = new Response<>(true, false, "Inventory: Product is out of stock");
            }
            else {
                res = new Response<>(false, true, "Inventory: Amount to remove exceeds the amount in stock");
            }
        }

        lock.writeLock().unlock();
        return res;
    }

    public Response<Boolean> removeProducts(Map<ProductDTO, Integer> shoppingBasket){
        purchaseLock.writeLock().lock();

        for(ProductDTO productDTO: shoppingBasket.keySet()){ // check if no product exceeding current amount in inventory
            if(shoppingBasket.get(productDTO) > getProductAmount(productDTO.getProductID())) {
                purchaseLock.writeLock().unlock();
                return new Response<>(false, true, "Inventory: Product " + productDTO.getName() + " exceeding to inventory capacity");
            }
        }

        for(ProductDTO productDTO: shoppingBasket.keySet()){
            removeProducts(productDTO.getProductID(), shoppingBasket.get(productDTO));
        }

        purchaseLock.writeLock().unlock();

        return new Response<>(true, false, "Inventory: Products removed successfully");
    }

    public Collection<Product> getProducts(){
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
            return new Response<>(false, true, "Inventory: This product doesn't exists");

        product.updateName(newName);

        if(newPrice > 0)
            product.updatePrice(newPrice);
        else
            return new Response<>(false, true, "Price must be positive");

        return new Response<>(true, false, "Inventory: Updated info successfully");
    }

    public Response<Product> getProduct(int productID) {
        Product product;

        lock.readLock().lock();
        product = products.get(productID);
        lock.readLock().unlock();

        if(product == null){
            return new Response<>(null, true, "Inventory: This product doesn't exists in the specified store");
        }
        else{
            return new Response<>(product, false, "Inventory: Product found");
        }
    }

    public Response<Boolean> addProductReview(int productID, Review review) {
        Response<Boolean> res;
        Product product;

        lock.readLock().lock();
        product = products.get(productID);
        lock.readLock().unlock();

        if(product == null){
            res = new Response<>(false, true, "Inventory: This product doesn't exists");
        }
        else{
            res = product.addReview(review);
        }

        return res;
    }

    public List<ProductDTO> getProductsDTO() {
        List<ProductDTO> result = new LinkedList<>();

        lock.readLock().lock();

        for(Product product: products.values()) {
            result.add(product.getProductDTO());
        }

        lock.readLock().unlock();

        return result;
    }

}