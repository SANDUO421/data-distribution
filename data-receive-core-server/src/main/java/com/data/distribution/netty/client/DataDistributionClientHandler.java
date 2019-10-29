package com.data.distribution.netty.client;

import com.data.distribution.service.DataGetService;
import com.data.distribution.service.impl.DataGetServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 数据处理
 *
 * @author 三多
 * @Time 2019/10/15
 */
@Slf4j
public class DataDistributionClientHandler extends SimpleChannelInboundHandler<String> {

    private DataGetService dataGetService;

    public DataDistributionClientHandler() {
        super();
        dataGetService = new DataGetServiceImpl();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        ctx.channel().attr(AttributeKey.valueOf("Attribute_key")).set(msg);
        /**
         * 转发核心网的数据到客户端
         */
        //响应客户端
        this.channelWrite(ctx, msg);
    }

    /**
     * @param msg 需要发送的消息内容
     * @param ctx ChannelHandlerContext
     * @author sanduo on 2019/11/1 16:10
     * @DESCRIPTION: 服务端给客户端发送消息
     * @return: void
     */
    private void channelWrite(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("登录成功");
        log.info("开发发送数据！");
        /**
         * 1.起一个线程，接收数据转发。
         * 2.传递ChannelHandlerContext进去
         * 3.发送
         *
         * 优化：定义一个线程池
         */
        dataGetService.getByRegisterCode(ctx);

        log.info("数据发送完成......");

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
