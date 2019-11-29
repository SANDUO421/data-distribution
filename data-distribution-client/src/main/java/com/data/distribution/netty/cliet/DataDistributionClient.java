package com.data.distribution.netty.cliet;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @author 三多
 * @Time 2019/10/15
 */
@Component
@Slf4j
public class DataDistributionClient {
    private static Bootstrap bootstrap;
    private static ChannelFuture future;
    private static EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    /**
     * 初始化
     */
    private static void init() {
        try {
            log.info("init...");
            bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                                 @Override
                                 protected void initChannel(SocketChannel ch) throws Exception {
                                     //编解码
                                     ChannelPipeline pipeline = ch.pipeline();
                                     pipeline.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
                                     pipeline.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
                                     pipeline.addLast("dataDistributionClientHandler", new DataDistributionClientHandler());
                                 }
                             }
                    );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object startAndWrite(InetSocketAddress socketAddress, String send) {
        init();
        future = bootstrap.connect(socketAddress).syncUninterruptibly();
        //传递数据给服务端
        DataDistributionClient.future.channel().writeAndFlush(send);
        DataDistributionClient.future.channel().closeFuture().syncUninterruptibly();
        return DataDistributionClient.future.channel().attr(AttributeKey.valueOf("Attribute_key")).get();
    }


    public static void main(String[] args) {
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 9888);
        String msg = "123";

        try {
            Object result = DataDistributionClient.startAndWrite(socketAddress, msg);
            log.info("... result:" + result);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (future != null) {
                future.channel().close();
            }
            eventLoopGroup.shutdownGracefully();
            log.info("Closed client！");
        }
    }


}
