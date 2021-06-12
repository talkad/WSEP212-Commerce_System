package Server.Domain.UserManager.Purchase;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.StoreController;
import Server.Domain.UserManager.DTOs.PurchaseClientDTO;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentSystemAdapter;
import Server.Domain.UserManager.ExternalSystemsAdapters.ProductSupplyAdapter;
import Server.Domain.UserManager.ExternalSystemsAdapters.SupplyDetails;
import Server.Domain.UserManager.ShoppingCart;

import java.util.List;

public class DirectPurchase {

    private StoreController storeController;
    private PaymentSystemAdapter paymentSystemAdapter;
    private ProductSupplyAdapter supplySystemAdapter;

    private DirectPurchase() {
        this.storeController = StoreController.getInstance();
        this.paymentSystemAdapter = PaymentSystemAdapter.getInstance();
        this.supplySystemAdapter = ProductSupplyAdapter.getInstance();
    }

    private static class CreateSafeThreadSingleton {
        private static final DirectPurchase INSTANCE = new DirectPurchase();
    }

    public static DirectPurchase getInstance() {
        return DirectPurchase.CreateSafeThreadSingleton.INSTANCE;
    }

    public Response<List<PurchaseClientDTO>> handlePayment(ShoppingCart cart, PaymentDetails paymentDetails, SupplyDetails supplyDetails) {
        Response<List<PurchaseClientDTO>> res = storeController.purchase(cart);
        Response<Integer> paymentRes;
        Response<Integer> cancelRes;
        Response<Integer> supplyRes;

        if (res.isFailure())
            return new Response<>(null, true, "Purchase Failed:\n" + res.getErrMsg() + " | didn't create external connection");

        paymentRes = paymentSystemAdapter.pay(paymentDetails);
        if(paymentRes.isFailure()) {
            storeController.addProductsToInventories(cart);
            return new Response<>(null, true, paymentRes.getErrMsg() + " => " +"Payment failed" + " | created external connection");
        }

        supplyRes = supplySystemAdapter.supply(supplyDetails);
        if(supplyRes.isFailure()) {
            storeController.addProductsToInventories(cart);

            cancelRes = paymentSystemAdapter.cancelPay(paymentRes.getResult() + "");
            if(cancelRes.isFailure())
                return  new Response<>(null, true, "Delivery failed and you have been charged but the payment cancellation failed | created external connection");

            return new Response<>(null, true, supplyRes.getErrMsg() + " => " +"Delivery failed" + " | created external connection");
        }

        StoreController.getInstance().addToHistory(cart);

        return new Response<>(res.getResult(), false, "The purchase was successful" + " | created external connection");
    }
}
