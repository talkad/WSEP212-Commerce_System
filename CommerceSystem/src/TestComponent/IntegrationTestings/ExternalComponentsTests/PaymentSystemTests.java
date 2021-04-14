package TestComponent.IntegrationTestings.ExternalComponentsTests;

import Server.Domain.UserManager.PaymentSystemAdapter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PaymentSystemTests {
    PaymentSystemAdapter payment;
    @Before
    public void setUp(){
        payment = PaymentSystemAdapter.getInstance();
    }

    @Test
    public void externalPaymentTestPass(){
        boolean b = payment.canPay(10,"4580-1111-1111-1111");
        Assert.assertTrue(b);
    }

    @Test
    public void externalPaymentTestFail(){
        boolean b = payment.canPay(-10,"4580-1111-1111-1111");
        Assert.assertFalse(b);
    }
}
