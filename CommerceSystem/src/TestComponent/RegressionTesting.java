package TestComponent;

import TestComponent.UnitTesting.ShopComponentTests.InventoryTest;
import TestComponent.UnitTesting.ShopComponentTests.ProductTest;
import TestComponent.UnitTesting.ShopComponentTests.StoreControllerTest;
import TestComponent.UnitTesting.ShopComponentTests.StoreTest;
import TestComponent.UnitTesting.UserComponentTest.ShoppingBasketTest;
import TestComponent.UnitTesting.UserComponentTest.ShoppingCartTest;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;


class SuiteRunnerFile {

    public static void resultReport(Result result) {
        System.out.println("Finished. Result: Failures: " +
                result.getFailureCount() + ". Ignored: " +
                result.getIgnoreCount() + ". Tests run: " +
                result.getRunCount() + ". Time: " +
                result.getRunTime() + "ms.");
    }


    public static void main(String[] args) {

        Class[] shopUnitTests = {ProductTest.class, InventoryTest.class, StoreTest.class, StoreControllerTest.class};
        Class[] userUnitTests = {ShoppingBasketTest.class, ShoppingCartTest.class};

        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(System.out));

        System.out.println("Test Shop Component");
        for(Class test: shopUnitTests){
            System.out.println("Class "+test.getName()+ " Testing ...");

            Result result = junit.run(test);
            resultReport(result);

            if(result.getFailureCount() > 0)
                break;
        }

        System.out.println("Test User Component");
        for(Class test: userUnitTests){
            System.out.println("Class "+test.getName()+ " Testing ...");

            Result result = junit.run(test);
            resultReport(result);

            if(result.getFailureCount() > 0)
                break;
        }

    }
}
