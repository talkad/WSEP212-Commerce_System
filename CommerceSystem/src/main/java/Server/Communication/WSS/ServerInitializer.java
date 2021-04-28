package Server.Communication.WSS;

import Server.Communication.WSS.HttpRequestHandler;
import Server.Communication.WSS2.TextWebSocketFrameHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    public ServerInitializer() {

    }

    @Override
    public void initChannel(SocketChannel ch){

//        SslHandler sslHandler = SSLHandlerProvider.getSSLHandler();

        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new HttpRequestHandler("/ws"));
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new FrameHandler());

//        pipeline.addLast(sslHandler);
//        pipeline.addLast(new HttpResponseEncoder());
//        pipeline.addLast(new HttpRequestDecoder());
//        pipeline.addLast(new HttpObjectAggregator(65536));
//        pipeline.addLast(new WebSocketServerCompressionHandler());
//        pipeline.addLast(new WebSocketServerProtocolHandler("", null, true));
//        pipeline.addLast(new WebSocketIndexPageHandler(""));
//        pipeline.addLast(new ServerHandler());
    }

}