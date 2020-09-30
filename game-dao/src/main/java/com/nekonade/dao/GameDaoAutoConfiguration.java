package com.nekonade.dao;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.nekonade.dao.daos.AsyncPlayerDao;
import com.nekonade.dao.daos.AsyncUserAccountDao;
import com.nekonade.dao.daos.PlayerDao;
import com.nekonade.dao.daos.UserAccountDao;
import com.nekonade.dao.redis.DaoRedisKeyConifg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.PostConstruct;

@Configuration
@ComponentScan({"com.nekonade.dao.daos","com.nekonade.dao.redis"})
@EnableMongoRepositories(basePackages= {"com.nekonade.dao.db.repository"})
public class GameDaoAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(GameDaoAutoConfiguration.class);
    @Autowired(required = false)
    private IGameDaoExecutorFactory gameDaoExecutorFactory;
    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private UserAccountDao userAccountDao;
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    
    @PostConstruct
    public void init() {
    	DaoRedisKeyConifg.Namespace = nacosDiscoveryProperties.getNamespace();
        logger.info("开始初始化Dao服务");
    }
    @Bean
    @ConditionalOnMissingBean
    public IGameDaoExecutorFactory gameDaoExecutorFactory() {
        return new DefaultGameDaoEventExecutorFactory();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public AsyncPlayerDao asyncPlayerDao() {
        return new AsyncPlayerDao(gameDaoExecutorFactory.getEventExecutorGroup(), playerDao);
    }
    @Bean
    public AsyncUserAccountDao asyncUserAccountDao() {
        return new AsyncUserAccountDao(gameDaoExecutorFactory.getEventExecutorGroup(), userAccountDao);
    }
}
