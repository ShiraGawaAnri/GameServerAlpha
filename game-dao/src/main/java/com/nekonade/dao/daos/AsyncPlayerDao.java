package com.nekonade.dao.daos;


import com.nekonade.common.concurrent.GameEventExecutorGroup;
import com.nekonade.dao.db.entity.Player;

public class AsyncPlayerDao  extends AbstractAsyncDao<Player,Long,PlayerDao>{

    // 由外面注入线程池组，可以使线程池组的共用
    public AsyncPlayerDao(GameEventExecutorGroup executorGroup, PlayerDao playerDao) {// 初始化的时候，从构造方法注入线程数量，和需要的PlayerDao实例
        super(executorGroup, playerDao);
    }
   

}
