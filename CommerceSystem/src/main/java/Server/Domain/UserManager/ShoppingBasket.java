package Server.Domain.UserManager;

import Server.DAL.PairDTOs.ProductIntPair;
import Server.DAL.DomainDTOs.ShoppingBasketDTO;
import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.DTOs.ProductClientDTO;
import Server.Domain.ShoppingManager.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ShoppingBasket {

    // the store these products are belong to
    private final int storeID;
    private Map<Integer, Product> products;
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

    public ShoppingBasket(ShoppingBasketDTO shoppingBasketDTO){
        this.storeID = shoppingBasketDTO.getStoreID();
        this.products = new ConcurrentHashMap<>();
        this.pAmount = new ConcurrentHashMap<>();
        this.totalPrice = shoppingBasketDTO.getTotalPrice();
        this.lock = new ReentrantReadWriteLock();

        List<ProductIntPair> productsList = shoppingBasketDTO.getProducts();
        if(productsList != null){
            for(ProductIntPair pair : productsList){
                this.pAmount.put(pair.getFirst().getProductID(), pair.getSecond());
                this.products.put(pair.getFirst().getProductID(), new Product(pair.getFirst()));
            }
        }
    }

    public ShoppingBasketDTO toDTO(){
        List<ProductIntPair> productsList = new Vector<>();
        for(int key : this.pAmount.keySet()){
            productsList.add(new ProductIntPair(this.products.get(key).toDTO(), this.pAmount.get(key)));
        }

        return new ShoppingBasketDTO(this.storeID, productsList, this.totalPrice);
    }

    public Response<Boolean> addProduct(ProductClientDTO product){
        Response<Boolean> res;
        int productID = product.getProductID();

        if(product.getStoreID() != storeID){ //double check
            res = new Response<>(false, true, "CRITICAL: Product "+product.getName()+" isn't from store");
        }
        else{

            lock.writeLock().lock();
            if(!pAmount.containsKey(productID)){
                products.put(productID, Product.createProduct(product));
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
        Product product;

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

    public Map<Product, Integer> getProducts() {
        Map<Product, Integer> basketProducts = new HashMap<>();

        lock.readLock().lock();
        for(Product product: products.values()){
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
        Product product;

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
        Map<Product, Integer> products = getProducts();
        StringBuilder result = new StringBuilder("ShoppingBasket id " + storeID + ":\n");

        for(Product product: products.keySet()){
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
