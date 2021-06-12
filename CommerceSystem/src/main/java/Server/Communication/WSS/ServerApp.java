package Server.Communication.WSS;

import Server.Service.CommerceService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import Server.Domain.CommonClasses.Response;

public class ServerApp {

    static final int PORT = 8080;

    public static void main(String[] args) {

        Response<Boolean> initRes;

        // for testing


        initRes = CommerceService.getInstance().init();

        if(!initRes.isFailure()){

            // Configure the bootstrap
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup(20);

            // Load the certificates and initiate the SSL context
            //        SSLHandlerProvider.initSSLContext();

            new Response<>(true, false, "Server successfully initiated");

            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new ServerInitializer())
                        .childOption(ChannelOption.AUTO_READ, true)
                        .bind(PORT).sync().channel().closeFuture().sync();

            } catch (InterruptedException e) {
                new Response<>(false, true, "Server failed to boot up (CRITICAL)");
//                e.printStackTrace();
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }
        else{
            new Response<>(false, true, "System initialization failed - the server isn't responding (CRITICAL)");
        }

    }
}
