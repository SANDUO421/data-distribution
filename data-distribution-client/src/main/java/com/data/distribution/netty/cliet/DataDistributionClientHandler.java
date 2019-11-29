package com.data.distribution.netty.cliet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 数据处理
 * @author 三多
 * @Time 2019/10/15
 */
@Slf4j
public class DataDistributionClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        ctx.channel().attr(AttributeKey.valueOf("Attribute_key")).set(msg);
        //System.out.println(msg);
        log.info(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.info("服务端发生异常【" + cause.getMessage() + "】");
        ctx.close();
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception, IOException {
        super.channelInactive(ctx);
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();
        //断开连接时，必须关闭，否则造成资源浪费，并发量很大情况下可能造成宕机
        ctx.close();
        System.out.println("channelInactive:" + clientIp);
    }


}
