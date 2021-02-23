package com.nekonade.common.redis.single;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

//动态加载配置中心的内容获取redis属性
@Data
@RefreshScope
@ConfigurationProperties(prefix = "spring.redis")
@Configuration
public class RedisPropertiesSingle {

    private String host;

    private String username;

    private String password;

    private int port;

    private String timeout;

    private Integer commandTimeout;

    private Integer maxAttempts;

    private Integer maxRedirects;

    private Integer maxActive;

    private Integer maxWait;

    private Integer maxIdle;

    private Integer minIdle;

    private boolean testOnBorrow;
}
