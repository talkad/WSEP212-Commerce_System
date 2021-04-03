package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;

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

    public Response<Boolean> updateProductQuantity(Product product, int amount) {
        Response<Boolean> res;
        int productID = product.getProductID();

        if(amount < 0){
            res = new Response<>(false, true, "amount can't be negative");
        }
        else if(!products.contains(product)){
            res = new Response<>(false, true, product.getName()+" is not in the basket");
        }
        else{
            pAmount.put(productID, amount);

            if(pAmount.get(productID) == 0)
                products.remove(product);

            res = new Response<>(true, false, "Product "+product.getName()+" amount updated");
        }

        return res;
    }

    public Response<Boolean> isProductExists(int productID){
        if(pAmount.containsKey(productID))
            return new Response<>(true, false, "Success");

        return new Response<>(false, true, "This product is absent");
    }

    public Response<Boolean> addReview(int productID, String review) {

        for(Product product: products){
            if(product.getProductID() == productID){
                product.addReview(review);
                return new Response<>(true, false, "Review has been added");
            }
        }

        return new Response<>(false, true, "Failure");
    }
}
