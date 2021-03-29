package Domain.UserManager;

import Domain.CommonClasses.Response;
import Domain.ShoppingManager.Product;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ShoppingBasket {

    // the store these products are belong to
    private final int storeID;
    private List<Product> products;
    // map between productID and its amount in the basket
    private Map<Integer, Integer> pAmount;

    public ShoppingBasket(int storeID){
        this.storeID = storeID;
        this.products = new LinkedList<>();
        this.pAmount = new HashMap<>();
    }

    public Response<Boolean> addProduct(Product product){
        Response<Boolean> res;
        int productID = product.getProductID();

        if(product.getStoreID() != storeID){
            res = new Response<>(false, true, "Product "+product.getName()+" isn't from store...");
        }
        else{

            if(!pAmount.containsKey(productID)){
                products.add(product);
                pAmount.put(productID, 1);
            }
            else{
                pAmount.put(productID, pAmount.get(productID) + 1);
            }

            res = new Response<>(true, false, "Product "+product.getName()+" added to shopping basket");
        }

        return res;
    }

    public Response<Boolean> removeProduct(Product product) {
        Response<Boolean> res;
        int productID = product.getProductID();

        if(!products.contains(product)){
            res = new Response<>(false, true, product.getName()+" is not in the basket");
        }
        else{
            pAmount.put(productID, pAmount.get(productID) - 1);

            if(pAmount.get(productID) == 0)
                products.remove(product);

            res = new Response<>(true, false, "Product "+product.getName()+" removed from shopping basket");
        }

        return res;
    }

    public Map<Product, Integer> getProducts() {
        Map<Product, Integer> basketProducts = new HashMap<>();

        for(Product product: products){
            basketProducts.put(product, pAmount.get(product.getProductID()));
        }

        return basketProducts;
    }

    public int getStoreID() {
        return storeID;
    }
}
