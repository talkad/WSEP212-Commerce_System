package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.StoreController;

import java.util.List;

public class PurchaseController {
        private StoreController storeController;
        private PaymentSystemAdapter paymentSystemAdapter;
        private ProductSupplyAdapter supplySystemAdapter;

        private PurchaseController() {
                this.storeController = StoreController.getInstance();
                this.paymentSystemAdapter = PaymentSystemAdapter.getInstance();
                this.supplySystemAdapter = ProductSupplyAdapter.getInstance(); /* communication with external delivery system */
        }

        private static class CreateSafeThreadSingleton {
                private static final PurchaseController INSTANCE = new PurchaseController();
        }

        public static PurchaseController getInstance() {
                return PurchaseController.CreateSafeThreadSingleton.INSTANCE;
        }

        /**
         * direct purchase
         * if one product in the cart will not be available in store inventory
         * then the purchase will be cancelled.
         * @param bankAccount of the user
         * @param cart of the user
         * @return positive response if the payment occurred successfully.
         */
        public Response<List<PurchaseDTO>> handlePayment(String bankAccount, ShoppingCart cart, String location) {
                boolean isPurchased = false;

                Response<List<PurchaseDTO>> res = storeController.purchase(cart);
                if (res.isFailure())
                        return new Response<>(null, true, res.getErrMsg());

                isPurchased = paymentSystemAdapter.pay(cart.getTotalPrice(), bankAccount);

                if (paymentSystemAdapter.pay(cart.getTotalPrice(), bankAccount)) {
                        if(isPurchased)
                                supplySystemAdapter.deliver(location, cart.getBaskets()); // assume the delivery is always successful

                        return new Response<>(res.getResult(), false, "Payment successfully made.");
                }

                return new Response<>(null, true, "Payment was unsuccessful.");
        }
}
