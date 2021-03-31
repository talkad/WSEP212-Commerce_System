package Server.Domain.UserManager;

import Server.Domain.ExternalComponents.PaymentSystem;

/**
 * This class in the only class who communicates with the external payment system,
 * and should get one as parameter when created
 */
public class PaymentSystemAdapter {
    PaymentSystem externalSystem;

    public PaymentSystemAdapter(PaymentSystem externalSystem){
        this.externalSystem = externalSystem;
    }

    /*
    pay --amount-- shekels taken from --bankAccount--
     */
    public boolean pay (int amount, int bankAccount){
        return externalSystem.pay(amount, bankAccount);
    }
}
