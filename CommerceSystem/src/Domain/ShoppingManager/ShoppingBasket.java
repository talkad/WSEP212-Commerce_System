package Domain.ShoppingManager;

import java.util.LinkedList;
import java.util.List;

public class ShoppingBasket {

    // the store these products are belong to
    private int storeID;
    private List<Product> products;

    public ShoppingBasket(int storeID){
        this.storeID = storeID;
        this.products = new LinkedList<>();
    }

    public void addProduct(Product product){
        //TODO: check that product is from the correct store
        products.add(product);
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
