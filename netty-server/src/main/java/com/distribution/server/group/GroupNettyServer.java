package com.distribution.server.group;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @description: 数据分发最终版
 * @author: sanduo
 * @date: 2019/11/28 14:14
 * @version: 1.0
 */
public class GroupNettyServer {

    public static volatile boolean flag = true;

    private static Bootstrap bootstrap = new Bootstrap();

    public static void connect() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup1 = new NioEventLoopGroup();

        NioEventLoopGroup workerGroup2 = new NioEventLoopGroup();

        bootstrap
                .group(workerGroup2)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, false)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new GroupClientHandler());
                    }
                });
        bootstrap.connect("192.168.100.4", 2005).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("连接成功!");
            } else {
                System.err.println("连接失败!");
                Thread.sleep(1000);
            }
        });

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(bossGroup, workerGroup1)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, false)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new GroupServerHandler());
                    }
                });
        serverBootstrap.bind(9888);
    }

    public static void connect(Channel channel) {
        if (channel != null && channel.isActive()) {
            return;
        }
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect("192.168.100.4", 2005).sync();
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("连接成功!");
                } else {
                    System.err.println("连接失败!");
                    Thread.sleep(1000);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("server starting...");
        GroupNettyServer.connect();
    }


}