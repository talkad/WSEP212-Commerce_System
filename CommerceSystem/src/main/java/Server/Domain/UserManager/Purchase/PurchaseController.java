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

public class PurchaseController {


        private PurchaseController() { }

        private static class CreateSafeThreadSingleton {
                private static final PurchaseController INSTANCE = new PurchaseController();
        }

        public static PurchaseController getInstance() {
                return PurchaseController.CreateSafeThreadSingleton.INSTANCE;
        }

        public Response<List<PurchaseClientDTO>> handlePayment(ShoppingCart cart, PaymentDetails paymentDetails, SupplyDetails supplyDetails) {

                return DirectPurchase.getInstance().handlePayment(cart, paymentDetails, supplyDetails);
        }

        public Response<PurchaseClientDTO> purchaseProduct(int productID, int storeID, PaymentDetails paymentDetails, SupplyDetails supplyDetails, double newPrice) {

                return BidPurchase.getInstance().handlePayment(productID, storeID, paymentDetails, supplyDetails, newPrice);
        }
}
