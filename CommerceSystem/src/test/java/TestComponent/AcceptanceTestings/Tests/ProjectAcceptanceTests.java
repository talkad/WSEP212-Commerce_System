package TestComponent.AcceptanceTestings.Tests;

import Server.DAL.DALService;
import Server.Domain.UserManager.ExternalSystemsAdapters.PaymentSystemAdapter;
import Server.Domain.UserManager.ExternalSystemsAdapters.ProductSupplyAdapter;
import Server.Service.IService;
import TestComponent.AcceptanceTestings.Bridge.Driver;
import TestComponent.AcceptanceTestings.Bridge.ProxyNotifier;

public abstract class ProjectAcceptanceTests {

    protected static IService bridge;
    protected static ProxyNotifier notifier;

    public void setUp(boolean toInit) {
        if (toInit) {
            bridge = Driver.getBridge();
            bridge.init();
            notifier = Driver.getNotifier();
            DALService.getInstance().useTestDatabase();
            DALService.getInstance().resetDatabase();
            PaymentSystemAdapter.getInstance().setMockFlag();
            ProductSupplyAdapter.getInstance().setMockFlag();
        }
    }
}
