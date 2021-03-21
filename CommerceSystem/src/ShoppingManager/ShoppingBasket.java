package ShoppingManager;

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

    public void removeProduct(Product product){
        products.remove(product);
    }

    public List<Product> getProducts(){
        return products;
    }
}
