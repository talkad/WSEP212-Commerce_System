package Server.Domain.UserManager.Purchase;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.Product;
import Server.Domain.ShoppingManager.StoreController;
import Server.Domain.UserManager.DTOs.PurchaseClientDTO;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentSystemAdapter;
import Server.Domain.UserManager.ExternalSystemsAdapters.ProductSupplyAdapter;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import Server.Domain.UserManager.ShoppingCart;

import java.util.List;

public class BidPurchase {

    private StoreController storeController;
    private PaymentSystemAdapter paymentSystemAdapter;
    private ProductSupplyAdapter supplySystemAdapter;

    private BidPurchase() {
        this.storeController = StoreController.getInstance();
        this.paymentSystemAdapter = PaymentSystemAdapter.getInstance();
        this.supplySystemAdapter = ProductSupplyAdapter.getInstance();
    }

    private static class CreateSafeThreadSingleton {
        private static final BidPurchase INSTANCE = new BidPurchase();
    }

    public static BidPurchase getInstance() {
        return BidPurchase.CreateSafeThreadSingleton.INSTANCE;
    }

    public Response<PurchaseClientDTO> handlePayment(int productID, int storeID, PaymentDetails paymentDetails, SupplyDetails supplyDetails, double newPrice) {
        Response<Integer> cancelRes;
        Response<Integer> supplyRes;
        Response<Integer> paymentRes;

        Response<Product> product = storeController.getProduct(storeID, productID);
        Product product1 = Product.createProduct(product.getResult().getProductDTO());
        System.out.println(newPrice);
        if(newPrice > 0)
            product1.setPrice(newPrice);

        Response<PurchaseClientDTO> res = storeController.purchase(product1);

        if (res.isFailure())
            return new Response<>(null, true, res.getErrMsg() + " | didn't create external connection");

        paymentRes = paymentSystemAdapter.pay(paymentDetails);
        if(paymentRes.isFailure()) {
            storeController.addProductsToInventories(product1, storeID);
            return new Response<>(null, true, paymentRes.getErrMsg() + " => "+"Payment failed" + " | created external connection");
        }

        supplyRes = supplySystemAdapter.supply(supplyDetails);
        if(supplyRes.isFailure()) {
            storeController.addProductsToInventories(product1, storeID);

            cancelRes = paymentSystemAdapter.cancelPay(paymentRes.getResult() + "");
            if(cancelRes.isFailure())
                return  new Response<>(null, true, "Delivery failed and you have been charged but the payment cancellation failed | created external connection");

            return new Response<>(null, true, supplyRes.getErrMsg() + " => "+"Delivery failed" + " | created external connection");
        }

        return new Response<>(res.getResult(), false, "The purchase was successful" + " | created external connection");
    }
}
