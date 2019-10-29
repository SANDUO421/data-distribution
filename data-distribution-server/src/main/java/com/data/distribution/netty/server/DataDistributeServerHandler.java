package com.data.distribution.netty.server;

import com.data.distribution.service.DataGetService;
import com.data.distribution.service.impl.DataGetServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据处理
 *
 * @author 三多
 * @Time 2019/10/15
 */
@Slf4j
public class DataDistributeServerHandler extends SimpleChannelInboundHandler<String> {

    private DataGetService dataGetService;

    public DataDistributeServerHandler(){
        super();
        dataGetService = new DataGetServiceImpl();
    }

    /**
     * 管理一个全局map，保存连接进服务端的通道数量
     */
    private static final ConcurrentHashMap<ChannelId, ChannelHandlerContext> CHANNEL_MAP = new ConcurrentHashMap<>();

    private static final String USERNAME = "tourist";
    private static final String PASSWORD = "tourist@#lydsj";

    /**
     * 从客户端收到新的数据时，这个方法会在收到消息时被调用
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("加载客户端报文......");
        log.info("【" + ctx.channel().id() + "】" + " :" + msg);
        log.info("Server receive message:{}", msg);
        /**
         *  下面可以解析数据，保存数据，生成返回报文，将需要返回报文写入write函数
         *
         */

        //响应客户端
        //ctx.channel().writeAndFlush("server already accept your message " + msg);

        //响应客户端
        this.channelWrite(ctx.channel().id(), msg);
    }

    /**
     * @param msg       需要发送的消息内容
     * @param channelId 连接通道唯一id
     * @author sanduo on 2019/4/28 16:10
     * @DESCRIPTION: 服务端给客户端发送消息
     * @return: void
     */
    public void channelWrite(ChannelId channelId, Object msg) throws Exception {

        ChannelHandlerContext ctx = CHANNEL_MAP.get(channelId);

        if (ctx == null) {
            log.info("通道【" + channelId + "】不存在");
            return;
        }

        if (msg == null && msg == "") {
            log.info("服务端响应空的消息");
            return;
        }

     /*   //将客户端的信息直接返回写入ctx
        ctx.write(msg);
        //刷新缓存区
        ctx.flush();*/

        //或者
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();

        if (msg != null) {
            String[] info = msg.toString().split(",");
            if (USERNAME.equalsIgnoreCase(info[0]) && PASSWORD.equalsIgnoreCase(info[1])) {
                log.info("登录成功");
                log.info("开发发送数据！");
                int count = 0;
                /**
                 * 1.起一个线程，接收数据转发。
                 * 2.传递ChannelHandlerContext进去
                 * 3.发送
                 *
                 * 优化：定义一个线程池
                 */
                dataGetService.getByRegisterCode(ctx);
                //while (true) {
                //    String result = dataGetService.getByRegisterCode("11111111");
                //    ctx.writeAndFlush("server already accept your message " + "Hello World!----" + result + "\r\n");
                //    //Thread.sleep(2000);
                //    count++;
                //    if (count > 10000) {
                //        break;
                //    }
                //}
                log.info("数据发送完成，总共发送了{}次！", count);
            } else {
                ctx.writeAndFlush("用户名或者密码错误");
            }

        } else {
            ctx.writeAndFlush("生输入用户名,密码,格式【user,pwd】\r\n");
        }

    }

    /**
     * 从客户端收到新的数据、读取完成时调用
     *
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws IOException {
        System.out.println("channelReadComplete");
        ctx.flush();
    }

    /**
     * 当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws IOException {
        System.out.println("exceptionCaught");
        log.info("服务端发生异常【" + cause.getMessage() + "】");
        cause.printStackTrace();
        //抛出异常，断开与客户端的连接
        ctx.close();
    }

    /**
     * 客户端与服务端第一次建立连接时 执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().read();
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();
        int clientPort = inSocket.getPort();
        //获取连接通道的唯一标识
        ChannelId channelId = ctx.channel().id();
        //如果map中不包含此链接，就保存此链接
        if (CHANNEL_MAP.containsKey(channelId)) {
            log.info("客户端【" + channelId + "】是连接状态，连接通道数量: " + CHANNEL_MAP.size());
        } else {
            //保存连接
            CHANNEL_MAP.putIfAbsent(channelId, ctx);
            log.info("客户端【" + channelId + "】连接netty服务器[IP:" + clientIp + "--->PORT:" + clientPort + "]");
            log.info("连接通道数量: " + CHANNEL_MAP.size());
        }
        //此处不能使用ctx.close()，否则客户端始终无法与服务端建立连接
        System.out.println("channelActive:" + clientIp + ":" + ctx.name());
    }

    /**
     * 客户端与服务端 断连时 执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception, IOException {
        super.channelInactive(ctx);
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();
        ChannelId channelId = ctx.channel().id();
        //包含此客户端才去删除
        if (CHANNEL_MAP.containsKey(channelId)) {
            //删除连接
            CHANNEL_MAP.remove(channelId);
            System.out.println();
            log.info("客户端【" + channelId + "】退出netty服务器[IP:" + clientIp + "--->PORT:" + inSocket.getPort() + "]");
            log.info("连接通道数量: " + CHANNEL_MAP.size());
        }
        //断开连接时，必须关闭，否则造成资源浪费，并发量很大情况下可能造成宕机
        ctx.close();
        System.out.println("channelInactive:" + clientIp);
    }

    /**
     * 服务端当read超时, 会调用这个方法
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception, IOException {
        super.userEventTriggered(ctx, evt);
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();
        //超时时断开连接
        ctx.close();
        System.out.println("userEventTriggered:" + clientIp);
    }

}
