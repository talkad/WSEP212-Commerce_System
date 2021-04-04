package TestComponent.AcceptanceTestings.Bridge;

import Server.Service.CommerceService;
import Server.Service.IService;

public abstract class Driver {

    public static IService getBridge(){
        ProxyBridge bridge = new ProxyBridge(); // proxy bridge

        CommerceService real = new CommerceService(); // real bridge
        bridge.serRealBridge(real);

        return bridge;
    }
}
