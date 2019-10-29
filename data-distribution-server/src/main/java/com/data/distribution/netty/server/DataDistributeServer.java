package com.data.distribution.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * 数据分发
 *
 * @author 三多
 * @Time 2019/10/15
 */
@Slf4j
@Component
public class DataDistributeServer {

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Channel channel;
    @Value("${netty.port}")
    private int port;

    public ChannelFuture start(int port) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
        return start(inetSocketAddress);
    }

    /**
     * 启动服务端
     *
     * @param socketAddress
     */
    public ChannelFuture start(InetSocketAddress socketAddress) {
        ChannelFuture future = null;


        try {
            //ServerBootstrap 是一个启动NIO服务的辅助启动类
            ServerBootstrap bootstrap = new ServerBootstrap();
            /**
             * <pre>
             * 1.设置group，将bossGroup， workerGroup线程组传递到{@link ServerBootstrap}
             * 2.{@link ServerSocketChannel} 是以NIO的selector为基础进行实现的，用来接收新的连接，这里告诉{@link Channel}
             * 通过{@link NioServerSocketChannel}获取新的连接
             * 3.option是设置 bossGroup，childOption是设置workerGroup,netty 默认数据包传输大小为1024字节, 设置它可以自动调整
             * 下一次缓冲区建立时分配的空间大小，避免内存的浪费(最小,初始化,最大) (根据生产环境实际情况来定)
             * 4.handler:设置bossGroup
             * 5.childHandler:设置 I/O处理类,主要用于网络I/O事件，记录日志，编码、解码消息
             * </pre>
             */
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 10496, 1048576))
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 10496, 1048576))
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new DataDistributeServerInitializer());
            //绑定端口，同步等待成功
            future = bootstrap.bind(socketAddress).syncUninterruptibly();
            channel = future.channel();
        } catch (Exception e) {
            log.error("netty start error", e);
        } finally {
            if (future != null && future.isSuccess()) {
                log.info("Netty server listening".concat(socketAddress.getHostName()).concat(" on port ") + socketAddress.getPort()
                        + " and ready for connections..."
                );
            } else {
                log.error("Netty server start up Failure！");
            }
        }
        return future;
    }

    /**
     * 关闭，释放资源
     */
    public void destroy() {
        log.info("Shutdown Netty Server...");
        if (channel != null) {
            //等待服务器监听端口关闭
            channel.closeFuture().syncUninterruptibly();
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        log.info("Shutdown Netty Server Success");
    }

}
