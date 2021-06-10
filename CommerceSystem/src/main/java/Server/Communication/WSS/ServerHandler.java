package Server.Communication.WSS;

import Server.Domain.CommonClasses.Response;
import Server.Service.CommerceService;
import Server.Service.Notifier;
import Server.Service.Notify;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class ServerHandler  extends SimpleChannelInboundHandler<Object> {

    private Notify notifier;

//   @Override
//    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
//        // ping and pong frames already handled
//
//        if (frame instanceof TextWebSocketFrame) {
//            // Send the uppercase string back.
//            String request = ((TextWebSocketFrame) frame).text();
//            ctx.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase(Locale.US)));
//           } else {
//                  String message = "unsupported frame type: " + frame.getClass().getName();
//                  throw new UnsupportedOperationException(message);
//        }
//      }

    public ServerHandler() {
        notifier = Notifier.getInstance();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof  FullHttpRequest)
            return;
//        else if(msg instanceof WebSocketFrame)


    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        notifier.removeConnection(ctx);
        ctx.close();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        Response<String> response = CommerceService.getInstance().addGuest();//todo adds an additional guest?

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

//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request){
//        Gson gson = new Gson();
//
//        // Handle a bad request.
//        if (!request.decoderResult().isSuccess()) {
//            ctx.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), BAD_REQUEST, ctx.alloc().buffer()));
//            return;
//        }
//
//        // Allow only GET methods.
//        if (!GET.equals(request.method())) {
//            ctx.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), FORBIDDEN, ctx.alloc().buffer()));
//            return;
//        }
//
//        ByteBuf requestBytes = request.content();
//        StringBuilder requestContent = new StringBuilder();
//
//        for (int i = 0; i < requestBytes.capacity(); i ++) {
//            byte b = requestBytes.getByte(i);
//            requestContent.append((char) b);
//        }
//
//        Response<?> response = CommerceHandler.getInstance().handle(requestContent.toString());
//
//        if(!response.isFailure()) {
//            ByteBuf content = Unpooled.copiedBuffer(gson.toJson(response), CharsetUtil.UTF_8);
//            ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content));
//        }
//    }

}