package com.nekonade.network.message.context.battle;


import com.nekonade.network.message.channel.AbstractGameChannelHandlerContext;
import com.nekonade.network.message.channel.battle.AbstractRaidBattleChannelHandlerContext;

public class RaidBattleUserEventContext<T> {

    private final T dataManager;
    private final AbstractRaidBattleChannelHandlerContext ctx;


    public RaidBattleUserEventContext(T dataManager, AbstractRaidBattleChannelHandlerContext ctx) {
        super();
        this.dataManager = dataManager;
        this.ctx = ctx;
    }

    public T getDataManager() {
        return dataManager;
    }

    public AbstractRaidBattleChannelHandlerContext getCtx() {
        return ctx;
    }


}
