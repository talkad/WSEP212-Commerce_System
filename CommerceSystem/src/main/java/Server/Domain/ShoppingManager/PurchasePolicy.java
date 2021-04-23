package Server.Domain.ShoppingManager;

import Server.Domain.CommonClasses.Response;

import java.util.Map;

public class PurchasePolicy {
    private int type;

    public PurchasePolicy(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Response<Boolean> isValidPurchase(Map<ProductDTO, Integer> shoppingBasket){
        int productsAmount = 0;
        for(Integer amount : shoppingBasket.values())
            productsAmount += amount;
        return productsAmount > 2 ? new Response<>(true, false, "Qualified of policy demands.") :
                                                      new Response<>(false, true, "Not qualified of policy demands.");

    }
}
