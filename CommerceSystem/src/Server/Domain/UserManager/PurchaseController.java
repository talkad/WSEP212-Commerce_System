package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.StoreController;

public class PurchaseController {
        private StoreController storeController;
        private PaymentSystemAdapter paymentSystemAdapter;
        private static volatile PurchaseController purchaseController = null;

        private PurchaseController() {
                this.storeController = StoreController.getInstance();
                PaymentSystemAdapter paymentSys = PaymentSystemAdapter.getInstance();
        }

        public static PurchaseController getInstance() {
                if (purchaseController == null) {
                        synchronized (PurchaseController.class) {
                                if (purchaseController == null)
                                        purchaseController = new PurchaseController();
                        }
                }
                return purchaseController;
        }


        public Response<Boolean> handlePayment(int bankAccount, User user) {
                Response<PurchaseDTO> res = storeController.purchase(user.getShoppingCart());
                if (res == null)
                        return new Response<>(false, true, "Something went wrong at one of the stores.");
                if (paymentSystemAdapter.pay(user.getShoppingCart().getTotalPrice(), bankAccount)) {
                        user.getPurchaseHistoryContents().add(res.getResult());
                        return new Response<>(true, false, "Payment successfully made.");
                }
                return new Response<>(false, true, "Payment was unsuccessful.");
        }
}
