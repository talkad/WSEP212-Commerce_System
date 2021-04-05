package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.StoreController;

import java.util.List;

public class PurchaseController {
        private StoreController storeController;
        private PaymentSystemAdapter paymentSystemAdapter;

        private PurchaseController() {
                this.storeController = StoreController.getInstance();
                this.paymentSystemAdapter = PaymentSystemAdapter.getInstance();
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
        public Response<List<PurchaseDTO>> handlePayment(int bankAccount, ShoppingCart cart) {
                Response<List<PurchaseDTO>> res = storeController.purchase(cart);
                if (res.isFailure())
                        return new Response<>(null, true, res.getErrMsg());

                if (paymentSystemAdapter.pay(cart.getTotalPrice(), bankAccount)) {
                        return new Response<>(res.getResult(), false, "Payment successfully made.");
                }

                return new Response<>(null, true, "Payment was unsuccessful.");
        }
}
