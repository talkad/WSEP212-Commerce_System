package Server.Communication.WSS;

import Server.Communication.MessageHandler.CommerceHandler;
import Server.Communication.WSS2.HttpRequestHandler;
import Server.Domain.CommonClasses.Response;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class FrameHandler  extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object event) throws Exception {
        if(event == WebSocketServerProtocolHandler.HandshakeComplete.class){
            ctx.pipeline().remove(HttpRequestHandler.class);
            ctx.writeAndFlush(new TextWebSocketFrame("Client "+ ctx.channel() + "joined"));
        }
        else{
            super.userEventTriggered(ctx, event);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        Gson gson = new Gson();
        String content = msg.text();

        Response<?> result = CommerceHandler.getInstance().handle(content);

        ctx.writeAndFlush(new TextWebSocketFrame(gson.toJson(result)));
    }
}
