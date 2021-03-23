package Domain.UserManager;

import Domain.ExternalComponents.PaymentSystem;
import Domain.ExternalComponents.ProductSupply;

public class UserController {
    PaymentSystemAdapter externalPayment;
    ProductSupplyAdapter externalDelivery;

    public UserController (){
        this.externalPayment = new PaymentSystemAdapter(new PaymentSystem()); /* communication with external payment system */
        this.externalDelivery = new ProductSupplyAdapter(new ProductSupply()); /* communication with external delivery system */
    }
}
