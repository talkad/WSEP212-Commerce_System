package Server.Domain.UserManager.ExternalSystemsAdapters.ExternalSystemsMock;

import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;

public class PaymentSystemMock {

    private PaymentSystemMock(){

    }

    // Inner class to provide instance of class
    private static class CreateThreadSafeSingleton
    {
        private static final PaymentSystemMock INSTANCE = new PaymentSystemMock();
    }

    public static PaymentSystemMock getInstance()
    {
        return PaymentSystemMock.CreateThreadSafeSingleton.INSTANCE;
    }

    public int pay(PaymentDetails paymentDetails){
//        if(paymentDetails.getId().length() != 9)
//            return -1;
        return 10000;
    }

    public int cancelPay(int transactionID){
//        if(transactionID >= 50000)
//            return -1;
        return 1;
    }

}
