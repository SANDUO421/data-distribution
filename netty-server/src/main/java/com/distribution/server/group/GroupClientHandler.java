package com.distribution.server.group;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class GroupClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        dataLinkedQueue.add((ByteBuf) msg);
//        System.out.println("========================");
//        ByteBuf data=(ByteBuf) msg;
//        ByteBuf copy = data.copy();
        GroupServerHandler.channelGroup.writeAndFlush(msg);
//        System.out.println("end");
//        System.out.println(new Date() + ": 客户端读到数据 -> " + copy.toString(Charset.forName("utf-8")));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==================channelInactive=============");
        // 关闭，等待重连
        ctx.close();
        GroupNettyServer.connect(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
