package com.data.distribution;

import com.data.distribution.netty.client.DataDistributionClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetSocketAddress;

/**
 * 启动类,转发核心网的数据
 *
 * @author 三多
 * @Time 2019/10/15
 */
@SpringBootApplication
@Slf4j
public class DataDistributionCoreServerApp implements CommandLineRunner {
    @Value("${netty.port}")
    private static Integer serverPort;
    @Value("${netty.url}")
    private static String serverUrl;

    public static void main(String[] args) {
        SpringApplication.run(DataDistributionCoreServerApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 9888);
        log.info("连接服务的地址【" + socketAddress.getAddress().getHostAddress() + "】，" + "端口为【" + socketAddress.getPort() + "】");
        //目的加入群组
        DataDistributionClient.startAndWrite(socketAddress, "tourist,tourist@#lydsj");
    }
}
