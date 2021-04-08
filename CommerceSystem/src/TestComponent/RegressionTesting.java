package TestComponent;

import TestComponent.UnitTesting.ShopComponentTests.InventoryTest;
import TestComponent.UnitTesting.ShopComponentTests.ProductTest;
import TestComponent.UnitTesting.ShopComponentTests.StoreControllerTest;
import TestComponent.UnitTesting.ShopComponentTests.StoreTest;
import TestComponent.UnitTesting.UserComponentTest.ShoppingBasketTest;
import TestComponent.UnitTesting.UserComponentTest.ShoppingCartTest;
import TestComponent.UnitTesting.UserComponentTest.UserControllerTest;
import TestComponent.UnitTesting.UserComponentTest.UserTests;
import org.junit.runner.*;

class RegressionTesting {

    public static void resultReport(Result result) {
        System.out.println("Finished. Result: Failures: " +
                result.getFailureCount() + ". Ignored: " +
                result.getIgnoreCount() + ". Tests run: " +
                result.getRunCount() + ". Time: " +
                result.getRunTime() + "ms.");
    }


    public static void main(String[] args) {

        Class[] shopUnitTests = {StoreControllerTest.class, StoreTest.class, InventoryTest.class, ProductTest.class};
        Class[] userUnitTests = {ShoppingCartTest.class, ShoppingBasketTest.class, UserTests.class};

        System.out.println("Test Shop Component");
        for(Class test: shopUnitTests){
            System.out.println("Class "+test.getName()+ " Testing ...");

            Result result = JUnitCore.runClasses(test);
            resultReport(result);
        }

        System.out.println("Test User Component");
        for(Class test: userUnitTests){
            System.out.println("Class "+test.getName()+ " Testing ...");

            Result result = JUnitCore.runClasses(test);
            resultReport(result);
        }
    }
}
