package Server.Communication.WSS;

import Server.Service.Notifier;
import Server.Service.Notify;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String wsUri;
    private Notify notifier;

    public HttpRequestHandler(String wsUri){
        this.wsUri = wsUri;
        this.notifier = Notifier.getInstance();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if(wsUri.equalsIgnoreCase(request.uri())){
            ctx.fireChannelRead(request.retain());
        }
        else{
            if(HttpUtil.is100ContinueExpected(request)){
                send100Continue(ctx);
            }

            HttpResponse response = new DefaultFullHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);

            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; Charset=UTF-8");
            boolean keepAlive = HttpHeaders.isKeepAlive(request);

            if(keepAlive){
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            ctx.write(response);

            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

            if(!keepAlive){
                future.addListener(ChannelFutureListener.CLOSE);
            }

        }
    }

    private static void send100Continue(ChannelHandlerContext ctx){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        notifier.removeConnection(ctx);
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client is connected.");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        notifier.removeConnection(ctx);
        System.out.println("Client disconnected.");
        super.channelUnregistered(ctx);
    }

}
