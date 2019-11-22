package com.data.distribution;

import com.data.distribution.config.NettyConfig;
import com.data.distribution.netty.cliet.DataDistributionClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetSocketAddress;

/**
 * 启动类
 * @author 三多
 * @Time 2019/10/15
 */
@SpringBootApplication
@Slf4j
public class DataDistributionClientApp implements CommandLineRunner{
    @Value("${netty.port}")
    private static Integer serverPort;
    @Value("${netty.url}")
    private static String serverUrl;

    public static void main(String[] args) {
        SpringApplication.run(DataDistributionClientApp.class,args);
    }

    @Override
    public void run(String... args) throws Exception {
        InetSocketAddress socketAddress = new InetSocketAddress("192.168.1.207",9888);
        //InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1",9888);
        log.info("连接服务的地址【"+socketAddress.getAddress().getHostAddress()+"】，"+"端口为【"+socketAddress.getPort()+"】");
        Object msg = DataDistributionClient.startAndWrite(socketAddress, "tourist,tourist@#lydsj");
        System.out.println("服务器响应的："+ msg);
    }
}
