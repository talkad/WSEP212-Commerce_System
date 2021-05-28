package TestComponent.IntegrationTestings.ExternalComponentsTests;

import Server.DAL.DALService;
import Server.Domain.CommonClasses.Response;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentDetails;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentSystemAdapter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PaymentSystemTests {
    PaymentSystemAdapter payment;
    @Before
    public void setUp(){
        payment = PaymentSystemAdapter.getInstance();
        DALService.getInstance().useTestDatabase();
        DALService.getInstance().resetDatabase();
    }

    @Test
    public void externalPaymentTestPass(){
        Response<Integer> res;

        PaymentDetails details = new PaymentDetails("2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        res = payment.pay(details);

        Assert.assertTrue(!res.isFailure() && res.getResult() >= 10000 && res.getResult() <= 100000);
    }

//    @Test
//    public void externalPaymentTestFail(){ // the external systems always respond with positive result
//        Response<Integer> res;
//
//        PaymentDetails details = new PaymentDetails("asd", "-3", "", "", "", "");
//        res = payment.pay(details);
//
//        Assert.assertTrue(res.isFailure() && res.getResult() < 0);
//    }

    @Test
    public void externalCancelPaymentTestPass(){
        Response<Integer> res;

        res = payment.cancelPay("4568");

        Assert.assertTrue(!res.isFailure() && res.getResult() == 1);
    }

    // run this test offline
    @Test
    public void offlineTestFailure(){
        Response<Integer> res;

        res = payment.cancelPay("4568");

        Assert.assertTrue(res.getErrMsg().contains("pay cancellation transaction failed due to error in handshake (CRITICAL)"));
    }
}
