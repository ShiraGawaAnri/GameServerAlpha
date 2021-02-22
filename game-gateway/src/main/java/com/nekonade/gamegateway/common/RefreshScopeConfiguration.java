package com.nekonade.gamegateway.common;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(RequestConfigs.class)
public class RefreshScopeConfiguration {

}
