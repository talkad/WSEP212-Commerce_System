package Domain.ShoppingManager;

import Domain.CommonClasses.Response;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Inventory {

    // map productID to the amount of it
    private Map<Integer, Integer> pAmount;
    private Map<Integer, Product> products;

    public Inventory() {
        pAmount = new HashMap<>();
        products = new HashMap<>();
    }

    public synchronized void addProducts(Product product, int amount) {
        int productID = product.getProductID();
        Integer result = pAmount.putIfAbsent(productID, 0);  // result will be null if not exists

        pAmount.put(productID, pAmount.get(productID) + amount);

        if (result == null) {
            products.put(productID, product);
        }
    }

    public Collection<Product> getInventory(){
        return products.values();
    }
}