package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.StoreController;

import java.util.*;

public class ShoppingBasket {

    // the store these products are belong to
    private final int storeID;
    private List<Product> products;
    // map between productID and its amount in the basket
    private Map<Integer, Integer> pAmount;
    private double totalPrice;

    public ShoppingBasket(int storeID){
        this.storeID = storeID;
        this.products = new Vector<>();
        this.pAmount = new HashMap<>();
        this.totalPrice = 0;
    }

    public Response<Boolean> addProduct(Product product){
        Response<Boolean> res;
        int productID = product.getProductID();

        if(product.getStoreID() != storeID){ //double check
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

            totalPrice += product.getPrice();
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

            totalPrice -= product.getPrice();
            res = new Response<>(true, false, "Product "+product.getName()+" removed from shopping basket");
        }

        return res;
    }

    public Map<ProductDTO, Integer> getProducts() {
        Map<ProductDTO, Integer> basketProducts = new HashMap<>();

        for(Product product: products){
            basketProducts.put(product.getProductDTO(), pAmount.get(product.getProductID()));
        }

        return basketProducts;
    }

    public int getStoreID() {
        return storeID;
    }

    public Response<Boolean> updateProductQuantity(int productID, int amount) {
        Response<Boolean> res;
        int prevAmount;
        Response<Product> productRes;

        if(amount < 0){
            res = new Response<>(false, true, "amount can't be negative");
        }
        else if(!pAmount.containsKey(productID)){
            res = new Response<>(false, true, "The product doesn't exists in the given basket");
        }
        else{
            prevAmount =pAmount.get(productID);
            pAmount.put(productID, amount);
            productRes = StoreController.getInstance().getProduct(storeID, productID);

            if(productRes.isFailure()){
                res = new Response<>(false, true, productRes.getErrMsg());
            }
            else {
                if(pAmount.get(productID) == 0)
                    products.remove(productRes.getResult());

                totalPrice += (amount - prevAmount)*productRes.getResult().getPrice();

                res = new Response<>(true, false, "Product "+productRes.getResult().getName()+" amount updated");
            }
        }

        return res;
    }

//    public Response<Boolean> addReview(int productID, String review) {
//
//        for(Product product: products){
//            if(product.getProductID() == productID){
//                product.addReview(review);
//                return new Response<>(true, false, "Review has been added");
//            }
//        }
//
//        return new Response<>(false, true, "Failure");
//    }

    public double getTotalPrice(){
        return totalPrice;
    }

    @Override
    public String toString() {
        Map<ProductDTO, Integer> products = getProducts();
        String result = "ShoppingBasket id " + storeID + ":\n";

        for(ProductDTO product: products.keySet()){
            result += product.toString() + "Amount :" + products.get(product) +"\n";
        }

        return result;
    }
}
