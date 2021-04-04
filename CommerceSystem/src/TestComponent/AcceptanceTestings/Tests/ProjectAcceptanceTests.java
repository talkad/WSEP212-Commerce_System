package TestComponent.AcceptanceTestings.Tests;

import Server.Service.IService;
import TestComponent.AcceptanceTestings.Bridge.Driver;

public abstract class ProjectAcceptanceTests {
    protected IService bridge;

    public void setUp(){
        this.bridge = Driver.getBridge();
    }
}
