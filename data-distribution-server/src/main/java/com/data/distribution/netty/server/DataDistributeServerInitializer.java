package com.data.distribution.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * 初始化通道
 *
 * @author 三多
 * @Time 2019/10/15
 */
public class DataDistributeServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //编解码
        // ChannelOutboundHandler，依照逆序执行
        pipeline.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
        // 属于ChannelInboundHandler，依照顺序执行
        pipeline.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
        //自定义数据处理器
        //自定义ChannelInboundHandlerAdapter
        pipeline.addLast("dataDistributeServerHandler", new DataDistributeServerHandler());

    }
}
