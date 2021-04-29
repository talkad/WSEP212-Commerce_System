package TestComponent.AcceptanceTestings.Bridge;

import Server.Communication.WSS.Notify;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProxyNotifier implements Notify {

    private Map<String, List<String>> messages;
    private Map<ChannelHandlerContext, String> connections;

    public ProxyNotifier(){
        messages = new HashMap<>();
        connections = new HashMap<>();
    }

    @Override
    public void addConnection(String identifier, ChannelHandlerContext ctx) {
        messages.put(identifier, new LinkedList<>());
        connections.put(ctx, identifier);
    }

    @Override
    public void removeConnection(ChannelHandlerContext ctx) {
        messages.remove(connections.get(ctx));
        connections.remove(ctx);
    }

    @Override
    public void notify(String identifier, String msg) {
        List<String> msgs = messages.get(identifier);

        if(msgs != null)
            messages.get(identifier).add(msg);
        else
            System.out.println("user "+ identifier +  " doesn't exists");
    }

    public List<String> getMessages(String username){
        return messages.get(username);
    }
}
