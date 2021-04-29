package Server.Communication.WSS;

import io.netty.channel.ChannelHandlerContext;

public interface Notify {

    void addConnection(String identifier, ChannelHandlerContext ctx);

    void removeConnection(ChannelHandlerContext ctx);

    void notify(String identifier, String msg);
}
