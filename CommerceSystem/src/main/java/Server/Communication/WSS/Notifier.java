package Server.Communication.WSS;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Notifier{

    private Map<String, ChannelHandlerContext> connections;

    private Notifier()
    {
        connections = new ConcurrentHashMap<>();
    }

    private static class CreateSafeThreadSingleton {
        private static final Notifier INSTANCE = new Notifier();
    }

    public static Notifier getInstance() {
        return CreateSafeThreadSingleton.INSTANCE;
    }

    public void addConnection(String identifier, ChannelHandlerContext ctx){
        connections.put(identifier, ctx);
    }

    public void removeConnection(ChannelHandlerContext ctx){
        connections.values().remove(ctx);
    }

    public void replaceIdentifier(String prevIdentifier, String newIdentifier) {
        ChannelHandlerContext ctx = connections.remove(prevIdentifier);
        connections.put(newIdentifier, ctx);
    }

    public void notify(String identifier, String msg){
        ByteBuf content;
        ChannelHandlerContext channel = connections.get(identifier);

        if(channel != null) {
            content = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
            channel.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content));
        }
    }
}
