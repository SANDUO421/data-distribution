package com.data.distribution.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
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

    public static void startAndWrite(InetSocketAddress socketAddress, String send) {
        init();
        future = bootstrap.connect(socketAddress).syncUninterruptibly();
        //传递数据给服务端加入群组
        DataDistributionClient.future.channel().writeAndFlush(send);
        DataDistributionClient.future.channel().closeFuture().syncUninterruptibly();
    }
}
