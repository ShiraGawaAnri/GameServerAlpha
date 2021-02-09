package com.nekonade.raidbattle.bean;

import com.nekonade.common.concurrent.GameEventExecutorGroup;
import com.nekonade.dao.daos.AsyncRaidBattleDao;
import com.nekonade.dao.daos.RaidBattleDao;
import com.nekonade.raidbattle.message.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class BeanConfiguration {

    @Autowired
    private ServerConfig serverConfig;//注入配置信息
    
    private GameEventExecutorGroup dbExecutorGroup;

    @Autowired
    private RaidBattleDao raidBattleDao; //注入数据库操作类

    @PostConstruct
    public void init() {
        dbExecutorGroup = new GameEventExecutorGroup(serverConfig.getDbThreads());//初始化db操作的线程池组
    }

    @Bean
    public AsyncRaidBattleDao asyncPlayerDao() {//配置AsyncPlayerDao的Bean
        return new AsyncRaidBattleDao(dbExecutorGroup, raidBattleDao);
    }
}
