package com.nekonade.log.GameLog.bean;

import com.nekonade.common.concurrent.GameEventExecutorGroup;
import com.nekonade.dao.daos.AsyncLogDao;
import com.nekonade.dao.daos.LogGameLogicDao;
import com.nekonade.log.GameLog.config.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;

@Configuration
public class BeanConfiguration {

    @Autowired
    private ServerConfig serverConfig;//注入配置信息

    @Autowired
    private LogGameLogicDao logGameLogicDao;

    private GameEventExecutorGroup dbExecutorGroup;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        dbExecutorGroup = new GameEventExecutorGroup(serverConfig.getDbThreads());//初始化db操作的线程池组
    }

    @Bean
    public AsyncLogDao asyncLogDao() {
        return new AsyncLogDao(dbExecutorGroup, logGameLogicDao, redisTemplate);
    }
}
