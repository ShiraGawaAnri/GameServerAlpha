package com.nekonade.dao;


import com.nekonade.common.concurrent.GameEventExecutorGroup;

/**
 * 
 * @ClassName: DefaultGameDaoEventExecutorFactory 
 * @Description: 提供一个默认的线程池创建工厂，用于数据库的异步操作。有时候，在业务项目中不想创建太多的线程池，可以重写这个接口，然后
 * 返回一个和业务共公使用的线程池即可。
 * @author: wang guang shuai
 * @date: 2020年1月6日 下午3:16:18
 */
public class DefaultGameDaoEventExecutorFactory implements IGameDaoExecutorFactory{

    private GameEventExecutorGroup eventExecutorGroup;
    public DefaultGameDaoEventExecutorFactory() {
        
    }
    public DefaultGameDaoEventExecutorFactory(GameEventExecutorGroup eventExecutorGroup) {
        this.eventExecutorGroup = eventExecutorGroup;
    }
    @Override
    public GameEventExecutorGroup getEventExecutorGroup() {
        if(eventExecutorGroup == null) {
            eventExecutorGroup = new GameEventExecutorGroup(Runtime.getRuntime().availableProcessors(),"Default-DB-Executor");
        }
        return eventExecutorGroup;
    }

}
