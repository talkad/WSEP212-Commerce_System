package Server.Domain.UserManager;

import Server.Domain.CommonClasses.Response;
import Server.Domain.ShoppingManager.ProductDTO;
import Server.Domain.ShoppingManager.StoreController;

import java.util.List;

public class PurchaseController {
        private StoreController storeController;
        private PaymentSystemAdapter paymentSystemAdapter;
        private ProductSupplyAdapter supplySystemAdapter;
        private Publisher publisher;

        private PurchaseController() {
                this.storeController = StoreController.getInstance();
                this.paymentSystemAdapter = PaymentSystemAdapter.getInstance();
                this.supplySystemAdapter = ProductSupplyAdapter.getInstance();
                this.publisher = Publisher.getInstance();
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
                StringBuilder msg;
                Response<List<PurchaseDTO>> res = storeController.purchase(cart);

                if (res.isFailure())
                        return new Response<>(null, true, res.getErrMsg() + " | doesn't created external connection");

                if(!supplySystemAdapter.canDeliver(location, cart.getBaskets())) {
                        storeController.addProductsToInventories(cart);
                        return new Response<>(null, true, "Delivery failed" + " | created external connection");
                }

                if(!paymentSystemAdapter.canPay(cart.getTotalPrice(), bankAccount)) {
                        storeController.addProductsToInventories(cart);
                        return new Response<>(null, true, "Payment failed" + " | created external connection");
                }

                paymentSystemAdapter.pay(cart.getTotalPrice(), bankAccount);
                supplySystemAdapter.deliver(location, cart.getBaskets());

                // notify to subscribers about purchase
                for(Integer storeID : cart.getBaskets().keySet()){
                        msg = new StringBuilder("Purchase occurred:\n");
                        for(ProductDTO productDTO: cart.getBasket(storeID).keySet()){
                                msg.append("name: ").append(productDTO.getName()).append("amount: ").append(cart.getBasket(storeID).get(productDTO)).append("\n");
                        }

                        publisher.notify(storeID, msg.toString());
                }

                return new Response<>(res.getResult(), false, "The purchase was successful" + " | created external connection");
        }
}
