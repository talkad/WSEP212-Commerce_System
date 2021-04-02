package TestComponent.AcceptanceTestings.Tests;

import Server.Service.IService;
import TestComponent.AcceptanceTestings.Bridge.Driver;

public abstract class ProjectAcceptanceTests {
    private IService bridge;

    //TODO: insert set up information

    public void setUp(){
        this.bridge = Driver.getBridge();
        //TODO: set up
    }
}
