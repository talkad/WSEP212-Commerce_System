package Server.Communication.WSS;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslHandler;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    public ServerInitializer() {

    }

    @Override
    public void initChannel(SocketChannel ch){

//        SslHandler sslHandler = SSLHandlerProvider.getSSLHandler();

        ChannelPipeline pipeline = ch.pipeline();
//        pipeline.addLast(sslHandler);
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler("/websocket", null, true));
        pipeline.addLast(new ServerHandler());
    }

}