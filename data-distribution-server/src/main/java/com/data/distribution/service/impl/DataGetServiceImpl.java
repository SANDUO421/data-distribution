package com.data.distribution.service.impl;

import com.data.distribution.service.DataGetService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 三多
 * @Time 2019/10/28
 */
@Service
@Slf4j
public class DataGetServiceImpl implements DataGetService {

    /**
     * 设置线程池：
     * 线程池核心线程数
     * 线程池最大线程数
     * 空闲线程存活时间（过期回收）
     * LinkedBlockingQueue: 阻塞队列
     * 拒绝处理策略：
     * <pre>
     *      ThreadPoolExecutor.AbortPolicy()：被拒绝后抛出RejectedExecutionException异常
     *      ThreadPoolExecutor.CallerRunsPolicy()：被拒绝后给调用线程池的线程处理
     *      ThreadPoolExecutor.DiscardOldestPolicy()：被拒绝后放弃队列中最旧的未处理的任务
     *      ThreadPoolExecutor.DiscardPolicy()：被拒绝后放弃被拒绝的任务(当前新添加的任务)
     * </pre>
     */
    private final ExecutorService executorService
            = new ThreadPoolExecutor(10, 100, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.DiscardOldestPolicy());

    /**
     * 根据注码获取运公司数据
     *
     * @param ctx
     */
    @Override
    public void getByRegisterCode(ChannelHandlerContext ctx) {
        executorService.submit(new SocketThread(ctx));
        //new SocketThread(ctx).start();
    }

    private static class SocketThread extends Thread {
        private volatile Socket socket = null;
        private volatile DataInputStream inputStream = null;
        private volatile ChannelHandlerContext ctx = null;

        private SocketThread(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {

            while (true) {
                try {
                    if (socket == null || socket.isClosed()) {
                        // 连接socket
                        socket = new Socket("192.168.1.213", 9999);
                        inputStream = new DataInputStream(socket.getInputStream());
                    }
                    String result = inputStream.readUTF();
                    //System.out.println(result);
                    //发送数据
                    ctx.writeAndFlush(result);
                } catch (IOException e) {
                    try {
                        inputStream.close();
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }


            }
        }
    }

}

