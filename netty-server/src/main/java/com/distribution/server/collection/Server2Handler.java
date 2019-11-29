package com.distribution.server.collection;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Server2Handler extends ChannelInboundHandlerAdapter {

    public volatile static ConcurrentLinkedQueue<ChannelHandlerContext> clientLinkedQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ConcurrentLinkedQueue<ByteBuf> dataLinkedQueue = ClientHandler.dataLinkedQueue;
//        new Thread(()->{
//            while(true){
//                while (!dataLinkedQueue.isEmpty()){
//                    ByteBuf data = ClientHandler.dataLinkedQueue.poll();
//                    ctx.channel().writeAndFlush(data);
//                }
//            }
//        }).start();

        clientLinkedQueue.add(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        clientLinkedQueue.remove(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        clientLinkedQueue.remove(ctx);
    }
}
