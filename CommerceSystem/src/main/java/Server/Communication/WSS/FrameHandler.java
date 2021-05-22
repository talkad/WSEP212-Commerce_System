package Server.Communication.WSS;

import Server.Communication.MessageHandler.CommerceHandler;
import Server.Domain.CommonClasses.Response;
import Server.Service.DataObjects.ReplyMessage;
import Server.Service.Notifier;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import java.util.Properties;


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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        // check if remove subscription needed
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        Gson gson = new Gson();

        Properties data = gson.fromJson(msg.text(), Properties.class);
        String action = data.getProperty("action");

        if(action.equals("reconnection")) {
            String response = gson.toJson(new ReplyMessage("reconnection", gson.toJson(new Response<>(true, false, "Reconnected successfully")), "reconnection"));
            ctx.writeAndFlush(new TextWebSocketFrame(response));

            Notifier.getInstance().addConnection(data.getProperty("username"), ctx);
        }
        else if(action.equals("startup")) {
            String content = msg.text();
            Response<?> result = CommerceHandler.getInstance().handle(content);

            String response = gson.toJson(new ReplyMessage("startup", gson.toJson(result), "startup"));
            ctx.writeAndFlush(new TextWebSocketFrame(response));

        }
        else {
            String content = msg.text();
            Response<?> result = CommerceHandler.getInstance().handle(content);

            System.out.println("received " + content);

            String response = gson.toJson(new ReplyMessage("response", gson.toJson(result), action));
            ctx.writeAndFlush(new TextWebSocketFrame(response));

            System.out.println("answered " + response);

            if (action.equals("login") && !result.isFailure())
                Notifier.getInstance().addConnection((String) result.getResult(), ctx);
        }
    }

}
