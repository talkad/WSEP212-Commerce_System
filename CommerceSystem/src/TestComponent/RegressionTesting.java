package TestComponent;

import TestComponent.AcceptanceTestings.Tests.SystemTests;
import TestComponent.AcceptanceTestings.Tests.VisitorCustomerTests;
import TestComponent.UnitTesting.MyFirstTest;
import TestComponent.UnitTesting.ShopComponentTests.InventoryTest;
import TestComponent.UnitTesting.ShopComponentTests.ProductTest;
import TestComponent.UnitTesting.ShopComponentTests.StoreControllerTest;
import TestComponent.UnitTesting.ShopComponentTests.StoreTest;
import TestComponent.UnitTesting.UserComponentTest.ShoppingBasketTest;
import TestComponent.UnitTesting.UserComponentTest.ShoppingCartTest;
import TestComponent.UnitTesting.UserComponentTest.UserControllerTest;
import TestComponent.UnitTesting.UserComponentTest.UserTests;
import org.junit.internal.TextListener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.*;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.Suite;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


class RegressionTesting {

    public static void resultReport(Result result) {
        System.out.println("Finished. Result: Failures: " +
                result.getFailureCount() + ". Ignored: " +
                result.getIgnoreCount() + ". Tests run: " +
                result.getRunCount() + ". Time: " +
                result.getRunTime() + "ms.");
    }


    public static void main(String[] args) {
        //Class cls= MyTestSuite.class;


//        JUnitCore jUnitCore = new JUnitCore();
//        CustomRunListener customRunListener = new CustomRunListener();
//        jUnitCore.addListener(customRunListener);

        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(System.out));
        junit.run(StoreControllerTest.class);

        Class[] shopUnitTests = {ProductTest.class, InventoryTest.class, StoreTest.class, StoreControllerTest.class};
        Class[] userUnitTests = {ShoppingBasketTest.class, ShoppingCartTest.class, UserControllerTest.class,
                UserTests.class};


//        Result result = JUnitCore.runClasses(MyTestSuite.class);
//        resultReport(result);
////        JUnitCore junit = new JUnitCore();
//        junit.addListener(new TextListener(System.out));
//        junit.runClasses(MyFirstTest.class);

//        JUnitCore junit = new JUnitCore();
//        junit.addListener(new TextListener(System.out));
//        Result result = junit.run(MyTestSuite.class);
//        resultReport(result);

//        Result result = JUnitCore.runClasses(ShoppingBasketTest.class);
//        resultReport(result);
//
//        JUnitCore junit = new JUnitCore();
//        junit.addListener(new TextListener(System.out));
//
        System.out.println("Test Shop Component");
        for(Class test: shopUnitTests){
            System.out.println("Class "+test.getName()+ " Testing ...");

            Result result = JUnitCore.runClasses(test);
            resultReport(result);

//            if(result.getFailureCount() > 0)
//                break;
        }

//        System.out.println("Test User Component");
//        for(Class test: userUnitTests){
//            System.out.println("Class "+test.getName()+ " Testing ...");
//
//            Result result = JUnitCore.runClasses(test);
//            resultReport(result);
//
////            if(result.getFailureCount() > 0)
////                break;
//        }

    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses({ProductTest.class, StoreControllerTest.class})
    public class MyTestSuite {
    }

}
