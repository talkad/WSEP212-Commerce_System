package TestComponent.IntegrationTestings.Mocks;

import Server.Communication.WSS.Notify;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MockNotifier implements Notify {

    private Map<String, List<String>> messages;
    private Map<ChannelHandlerContext, String> connections;

    public MockNotifier(){
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
    public void replaceIdentifier(String prevIdentifier, String newIdentifier) {
        List<String> msg = messages.remove(prevIdentifier);
        messages.put(newIdentifier, msg);
    }

    @Override
    public void notify(String identifier, String msg) {
        messages.get(identifier).add(msg);
    }

    public List<String> getMessages(String username){
        return messages.get(username);
    }
}
