package com.data.distribution.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 三多
 * @Time 2019/10/21
 */
@Component
@ConfigurationProperties(prefix = "netty",ignoreUnknownFields = true)
@Data
public class NettyConfig {
    private Integer port;
    private String url;
}
