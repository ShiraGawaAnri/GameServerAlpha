package com.nekonade.center.logicconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gamecenter.config")
@Getter
@Setter
public class GameCenterConfig {

    private int userTokenExpire = 7;
    private int playerTokenExpire = 7;
    /**
     * dao异步操作的线程数，默认是4
     */
    private int daoAsyncThreadCount = 4;
    
    
}
