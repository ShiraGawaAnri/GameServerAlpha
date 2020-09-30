package com.nekonade.dao;


import com.nekonade.common.concurrent.GameEventExecutorGroup;

public interface IGameDaoExecutorFactory {

    GameEventExecutorGroup getEventExecutorGroup();
}
