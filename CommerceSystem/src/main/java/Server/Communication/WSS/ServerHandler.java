package Server.Communication.WSS;

import Server.Communication.MessageHandler.CommerceHandler;
import Server.Domain.CommonClasses.Response;
import Server.Service.CommerceService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;

public class ServerHandler  extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Response<String> response = CommerceService.getInstance().addGuest();

        ctx.writeAndFlush(new TextWebSocketFrame(CommerceHandler.getInstance().handle(response.getResult())));

        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected.");
        super.channelUnregistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request){
        ByteBuf requestBytes = request.content();

        StringBuilder requestContent = new StringBuilder();
        for (int i = 0; i < requestBytes.capacity(); i ++) {
            byte b = requestBytes.getByte(i);
            requestContent.append((char) b);
        }

        ByteBuf content = Unpooled.copiedBuffer(CommerceHandler.getInstance().handle(requestContent.toString()), CharsetUtil.UTF_8);
        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

        ctx.channel().writeAndFlush(response);
    }

}