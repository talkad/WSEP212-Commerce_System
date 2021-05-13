package Server.Service;


import Server.Communication.WSS.DataObjects.NotificationData;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Notifier implements Notify{

    private Map<String, ChannelHandlerContext> connections;

    private Notifier()
    {
        connections = new ConcurrentHashMap<>();
    }

    private static class CreateSafeThreadSingleton {
        private static final Notifier INSTANCE = new Notifier();
    }

    public static Notify getInstance() {
        return CreateSafeThreadSingleton.INSTANCE;
    }

    public void addConnection(String identifier, ChannelHandlerContext ctx){
        connections.put(identifier, ctx);
    }

    public void removeConnection(ChannelHandlerContext ctx){
        connections.values().remove(ctx);
    }

    public void notify(String identifier, String msg){
        Gson gson = new Gson();
        ChannelHandlerContext channel = connections.get(identifier);

        if(channel != null)
            channel.writeAndFlush(new TextWebSocketFrame(gson.toJson(new NotificationData(msg))));

    }
}
