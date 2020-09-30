package com.nekonade.center.config;

import com.nekonade.center.logicconfig.GameCenterConfig;
import com.nekonade.common.concurrent.GameEventExecutorGroup;
import com.nekonade.dao.DefaultGameDaoEventExecutorFactory;
import com.nekonade.dao.IGameDaoExecutorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class BeanConfig {

    @Autowired
    private GameCenterConfig gameCenterConfig;
    private GameEventExecutorGroup eventExecutorGroup;
    
    @PostConstruct
    public void init() {
        eventExecutorGroup = new GameEventExecutorGroup(gameCenterConfig.getDaoAsyncThreadCount(),"DB-Executor");
    }
    @Bean
    public IGameDaoExecutorFactory gameDaoExecutorFactory() {
        //提供一个特定的数据库操作线程池
        return new DefaultGameDaoEventExecutorFactory(eventExecutorGroup);
    }
    
}
