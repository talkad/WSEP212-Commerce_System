package TestComponent.AcceptanceTestings.Tests;

import Server.DAL.DALControllers.DALProxy;
import Server.Domain.UserManager.CommerceSystem;
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
            CommerceSystem.getInstance().configInit("successconfigfile.json");
            //DALService.getInstance().startDB();
            DALProxy.getInstance().resetDatabase();
            CommerceSystem.getInstance().initState("initfile");

            bridge = Driver.getBridge();
            notifier = Driver.getNotifier();
            PaymentSystemAdapter.getInstance().setMockFlag();
            ProductSupplyAdapter.getInstance().setMockFlag();
        }
    }
}
