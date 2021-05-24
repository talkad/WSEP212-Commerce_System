package Server.Service;

import Server.Service.DataObjects.ReplyMessage;
import io.netty.channel.ChannelHandlerContext;

public interface Notify {

    void addConnection(String identifier, ChannelHandlerContext ctx);

    void removeConnection(ChannelHandlerContext ctx);

    void notify(String identifier, ReplyMessage msg);
}
