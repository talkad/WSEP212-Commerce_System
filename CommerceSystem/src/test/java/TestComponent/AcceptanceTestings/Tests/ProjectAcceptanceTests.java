package TestComponent.AcceptanceTestings.Tests;

import Server.Service.IService;
import TestComponent.AcceptanceTestings.Bridge.Driver;
import TestComponent.AcceptanceTestings.Bridge.ProxyNotifier;

public abstract class ProjectAcceptanceTests {

    protected static IService bridge;
    protected static ProxyNotifier notifier;

    public void setUp(){
        bridge = Driver.getBridge();
        bridge.init();
        notifier = Driver.getNotifier();
    }
}
