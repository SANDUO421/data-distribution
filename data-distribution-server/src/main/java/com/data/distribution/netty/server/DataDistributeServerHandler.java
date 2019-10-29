package com.data.distribution.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

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

    /**
     * 定义channel组,存放客户端信息（保存channel对象）
     * <p>
     * 注意： Java中多个实例的static变量会共享同一块内存区域，也就是多个对象共享一个类的同一个静态成员变量
     * <p>
     * 两个JVM之间并不会共享数据。（static变量的线程间共享，进程间不共享）
     */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 管理一个全局map，保存连接进服务端的通道数量
     */
    private static final ConcurrentHashMap<ChannelId, ChannelHandlerContext> CHANNEL_MAP = new ConcurrentHashMap<>();
    /**
     * 用户名
     */
    private static final String USERNAME = "tourist";
    /**
     * 密码
     */
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
        // log.info("加载客户端报文......");
        /**
         *  下面可以解析数据，保存数据，生成返回报文，将需要返回报文写入write函数
         *
         */
        Channel channel = ctx.channel();
        //响应客户端
        //ctx.channel().writeAndFlush("server already accept your message " + msg);
        //TODO 随后加密
        channelGroup.forEach(ch -> {
            if (ch != channel) {
                ch.writeAndFlush(msg);
            } else {
                ch.writeAndFlush("【自己消息】" + msg + "\r\n");
            }

        });

    }

    /**
     * 连接建立
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("【服务器】-" + channel.remoteAddress() + "加入\r\n");
        channelGroup.writeAndFlush("【服务器】-" + channel.remoteAddress() + "加入\r\n");
        channelGroup.add(channel);
    }

    /**
     * 离开，会自定从channelGroup中删除对应的channel
     * 例如  手机 飞行模式或者强制关机 是不会调用此方法
     * 针对这种情况必须有心跳
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("【服务器】-" + channel.remoteAddress() + "离开\r\n");
        channelGroup.writeAndFlush("【服务器】-" + channel.remoteAddress() + "离开\r\n");
        System.out.println(channelGroup.size());
    }
    ///**
    // * @param msg       需要发送的消息内容
    // * @param channelId 连接通道唯一id
    // * @author sanduo on 2019/4/28 16:10
    // * @DESCRIPTION: 服务端给客户端发送消息
    // * @return: void
    // */
    //public void channelWrite(ChannelId channelId, Object msg) throws Exception {
    //
    //    ChannelHandlerContext ctx = CHANNEL_MAP.get(channelId);
    //
    //    if (ctx == null) {
    //        log.info("通道【" + channelId + "】不存在");
    //        return;
    //    }
    //
    //    if (msg == null && msg == "") {
    //        log.info("服务端响应空的消息");
    //        return;
    //    }
    //
    // /*   //将客户端的信息直接返回写入ctx
    //    ctx.write(msg);
    //    //刷新缓存区
    //    ctx.flush();*/
    //
    //    //或者
    //    InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
    //
    //    if (msg != null) {
    //        String[] info = msg.toString().split(",");
    //        if (USERNAME.equalsIgnoreCase(info[0]) && PASSWORD.equalsIgnoreCase(info[1])) {
    //            log.info("登录成功");
    //            log.info("开发发送数据！");
    //            /**
    //             * 1.起一个线程，接收数据转发。
    //             * 2.传递ChannelHandlerContext进去
    //             * 3.发送
    //             *
    //             * 优化：定义一个线程池
    //             */
    //            dataGetService.getByRegisterCode(ctx);
    //            //while (true) {
    //            //    String result = dataGetService.getByRegisterCode("11111111");
    //            //    ctx.writeAndFlush("server already accept your message " + "Hello World!----" + result + "\r\n");
    //            //    //Thread.sleep(2000);
    //            //    count++;
    //            //    if (count > 10000) {
    //            //        break;
    //            //    }
    //            //}
    //            log.info("数据发送完成......");
    //        } else {
    //            ctx.writeAndFlush("用户名或者密码错误");
    //        }
    //
    //    } else {
    //        ctx.writeAndFlush("生输入用户名,密码,格式【user,pwd】\r\n");
    //    }
    //
    //}

    /**
     * 从客户端收到新的数据、读取完成时调用
     *
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws IOException {
        //System.out.println("channelReadComplete");
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
        log.error("服务端发生异常【" + cause.getMessage() + "】");
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
        log.info(ctx.channel().remoteAddress() + "上线");
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
        log.info(ctx.channel().remoteAddress() + "下线");
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
        log.error("READ 超时！");
        InetSocketAddress inSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = inSocket.getAddress().getHostAddress();
        //超时时断开连接
        ctx.close();
        System.out.println("userEventTriggered:" + clientIp);
    }


}
