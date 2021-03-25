package Domain.UserManager;

import Domain.CommonClasses.Response;
import Domain.ShoppingManager.Product;
import Domain.ShoppingManager.ProductDTO;

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

    public Response<Boolean> addProduct(Product product){
        Response<Boolean> res;

        if(product.getStoreID() != storeID){
            res = new Response<>(false, true, "Product "+product.getName()+" isn't from store...");
        }
        else{
            products.add(product);
            res = new Response<>(true, false, "Product "+product.getName()+" added to shopping basket");
        }

        return res;
    }

    public Response<Boolean> removeProduct(Product product) {
        Response<Boolean> res;

        if(!products.contains(product)){
            res = new Response<>(false, true, product.getName()+" is not in the basket");
        }
        else{
            products.remove(product);
            res = new Response<>(true, false, "Product "+product.getName()+" removed from shopping basket");
        }

        return res;
    }

    public List<ProductDTO> getProducts() {
        List<ProductDTO> basketProducts = new LinkedList<>();

        for(Product product: products){
            basketProducts.add(product.getDTO());
        }

        return basketProducts;
    }

}
