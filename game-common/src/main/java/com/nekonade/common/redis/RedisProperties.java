package com.nekonade.common.redis;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

//动态加载配置中心的内容获取redis属性
@Data
@RefreshScope
@ConfigurationProperties(prefix = "spring.redis")
@Configuration
public class RedisProperties {

    private String host;

    private String password;

    private String port;

    private String timeout;
}
