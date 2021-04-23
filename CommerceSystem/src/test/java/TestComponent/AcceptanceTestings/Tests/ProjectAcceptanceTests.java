package TestComponent.AcceptanceTestings.Tests;

import Server.Service.IService;
import TestComponent.AcceptanceTestings.Bridge.Driver;

public abstract class ProjectAcceptanceTests {

    protected static IService bridge;

    public void setUp(){
        bridge = Driver.getBridge();
        bridge.init();
    }
}
