package Server.Communication.WSS;

import Server.Communication.MessageHandler.CommerceHandler;
import Server.Domain.CommonClasses.Response;
import Server.Service.CommerceService;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;

public class ServerHandler  extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Notifier notifier;


    public ServerHandler() {
        notifier = Notifier.getInstance();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        notifier.removeConnection(ctx);
        ctx.close();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        // todo - aviad may be wrong like always and this code should be in a specific handler for function addGuest
        Response<String> response = CommerceService.getInstance().addGuest();
        ByteBuf content = Unpooled.copiedBuffer(response.getResult(), CharsetUtil.UTF_8);

        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content));
        notifier.addConnection(response.getResult(), ctx);

        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        notifier.removeConnection(ctx);
        System.out.println("Client disconnected.");
        super.channelUnregistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request){
        // Handle a bad request.
        if (!request.decoderResult().isSuccess()) {
            ctx.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), BAD_REQUEST, ctx.alloc().buffer()));
            return;
        }

        // Allow only GET methods.
        if (!GET.equals(request.method())) {
            ctx.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), FORBIDDEN, ctx.alloc().buffer()));
            return;
        }

        ByteBuf requestBytes = request.content();
        StringBuilder requestContent = new StringBuilder();

        for (int i = 0; i < requestBytes.capacity(); i ++) {
            byte b = requestBytes.getByte(i);
            requestContent.append((char) b);
        }

        ByteBuf content = Unpooled.copiedBuffer(CommerceHandler.getInstance().handle(requestContent.toString()), CharsetUtil.UTF_8);
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content));
    }

}