package Domain.ExternalComponents;

/**
 * This class represents external payment system
 * We only communicates with this class using the PaymentSystemAdapter
 */
public class PaymentSystem {
    /*
    Actual payment from the external system, requires amount of cash to spend from
    the given bankAccount
     */
    public boolean pay (int amount, int bankAccount){
        return true; /* as to version 1, this payment will always work */
    }
}
