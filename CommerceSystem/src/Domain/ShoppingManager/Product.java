package Domain.ShoppingManager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Product {

    private int productID;
    private String name;
    private double price;
    private List<String> categories; // maybe?

    private Lock updateName;
    private Lock updateCategories;

    public Product(int productID, String name, double price){
        this.productID = productID;
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
