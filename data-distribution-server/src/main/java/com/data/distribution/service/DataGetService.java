package com.data.distribution.service;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author 三多
 * @Time 2019/10/28
 */
public interface DataGetService {
    /**
     * registerCode:注册码
     *
     * @param ctx
     */
    public void getByRegisterCode(ChannelHandlerContext ctx);
}
