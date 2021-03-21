package ShoppingManager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Product {

    private String name;
    private double price;
    private List<String> categories; // maybe?

    private Lock updateName;
    private Lock updateCategories;

    public Product(String name, double price){
        this.name = name;
        this.price = price;
        this.categories = Collections.synchronizedList(new LinkedList<>());

        updateName = new ReentrantLock();
        updateCategories = new ReentrantLock();
    }

    public void updateName(String newName){
        // ...
    }

    public void updatePrice(double newPrice){
        // ...
    }

    public void addCategory(String category){
        // ...
    }

    public void removeCategory(String category){
        // ...
    }

}
