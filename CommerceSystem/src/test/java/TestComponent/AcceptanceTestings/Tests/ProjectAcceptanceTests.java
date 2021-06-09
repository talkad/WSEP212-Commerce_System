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
            DALService.getInstance().useTestDatabase();
            DALService.getInstance().startDB();
            DALService.getInstance().resetDatabase();
            bridge = Driver.getBridge();
            bridge.init();
            notifier = Driver.getNotifier();
            PaymentSystemAdapter.getInstance().setMockFlag();
            ProductSupplyAdapter.getInstance().setMockFlag();
        }
    }
}
