package Domain.ShoppingManager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Product {

    private int productID;
    private int storeID;
    private String name;
    private AtomicReference<Double> price;
    private List<String> categories; // maybe?

    private Lock updateName;
    private Lock updateCategories;

    public Product(int productID, int storeID, String name, double price){
        this.productID = productID;
        this.storeID = storeID;
        this.name = name;
        this.price = new AtomicReference<>(price);
        this.categories = Collections.synchronizedList(new LinkedList<>());

        updateName = new ReentrantLock();
        updateCategories = new ReentrantLock();
    }

    public void updatePrice(double newPrice){
        double currentPrice;

        do {
            currentPrice = price.get();
        }while (!price.compareAndSet(currentPrice, newPrice));
    }

    public int getStoreID(){
        return storeID;
    }

    public String getName() {
        return name;
    }

    public ProductDTO getDTO() {
        return new ProductDTO(name, price.get(), categories, storeID);
    }
}
