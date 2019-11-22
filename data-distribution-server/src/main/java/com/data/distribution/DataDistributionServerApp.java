package com.data.distribution;

import com.data.distribution.netty.server.DataDistributeServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @description: 启动类模拟数据发送使用socket
 * @author: sanduo
 * @date: 2019/10/15 14:27
 * @version: 1.0
 */
@SpringBootApplication
public class DataDistributionServerApp {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DataDistributionServerApp.class);
        //不启动web服务
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
        //启动netty
        DataDistributeServer dataDistributeServer = new DataDistributeServer();
        try {
            dataDistributeServer.start(9888);
        } finally {
            //释放资源
            dataDistributeServer.destroy();
        }
    }
}
