package com.distribution.server.group;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    public static ConcurrentLinkedQueue<ByteBuf> dataLinkedQueue=new ConcurrentLinkedQueue<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        dataLinkedQueue.add((ByteBuf) msg);
//        System.out.println(new Date() + ": 客户端读到数据 -> " + byteBuf.toString(Charset.forName("utf-8")));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==================channelInactive==============");
    }
}
