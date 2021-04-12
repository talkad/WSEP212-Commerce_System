package Server.Domain.ExternalComponents;

/**
 * This class represents external payment system
 * We only communicates with this class using the PaymentSystemAdapter
 */
public class PaymentSystem {
    /*
    Actual payment from the external system, requires amount of cash to spend from
    the given bankAccount
     */
    public void pay (double price, String bankAccount){
         /* as to version 1, this payment will always work */
    }

    public boolean canPay(double price, String bankAccount)
    {
        return price > 0 && bankAccount != null && bankAccount.length() > 4  && bankAccount.startsWith("4580");
    }
}
