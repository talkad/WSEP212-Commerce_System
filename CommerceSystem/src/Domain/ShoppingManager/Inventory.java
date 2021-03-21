package Domain.ShoppingManager;

import java.util.List;
import java.util.Map;

public class Inventory {

    // map productID to the amount of it
    private Map<Integer, Integer> pAmount;
    private List<Product> products;

    public Inventory(){

    }

    public synchronized void addProducts(Product product, int amount){

    }

    public synchronized void removeProducts(Product product, int amount){

    }

    public void updateName(int productID, String newName){
        // ...
    }

    public void updatePrice(int productID, double newPrice){
        // ...
    }

    public void addCategory(int productID, String category){
        // ...
    }

    public void removeCategory(int productID, String category){
        // ...
    }

    public Map<Product, Integer> getInventory(){
        return null;
    }
}
