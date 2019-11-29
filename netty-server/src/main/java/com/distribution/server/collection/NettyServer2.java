package com.distribution.server.collection;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NettyServer2 {

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });
        bootstrap.connect("localhost", 2005);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY,false)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new Server2Handler());
                    }
                });
        serverBootstrap.bind(8888);

        new Thread(() -> {
            while (true) {
                ConcurrentLinkedQueue<ByteBuf> dataLinkedQueue = ClientHandler.dataLinkedQueue;
                while (!dataLinkedQueue.isEmpty()) {
                    ByteBuf data = dataLinkedQueue.poll();
                    // 1. 获取二进制抽象 ByteBuf

                    // 2. 准备数据，指定字符串的字符集为 utf-8
//                byte[] bytes = "你好，闪电侠!".getBytes(Charset.forName("utf-8"));
                    Iterator<ChannelHandlerContext> clientLinkedQueue = Server2Handler.clientLinkedQueue.iterator();
                    if (Server2Handler.clientLinkedQueue.size() > 0) {
                        System.out.println(Server2Handler.clientLinkedQueue.size());
                    }
                    while (clientLinkedQueue.hasNext()) {
                        System.out.println("send data");
                        ChannelHandlerContext clientCtx = clientLinkedQueue.next();

//                        ByteBuf data = clientCtx.alloc().buffer();
//                        data.writeBytes(bytes);
                        ByteBuf copy = data.copy();
                        clientCtx.channel().writeAndFlush(copy);
//                        System.out.println(new Date() + ": 客户端读到数据 -> " + data.toString(Charset.forName("utf-8")));
                    }
                }
            }
        }).start();
    }

}
