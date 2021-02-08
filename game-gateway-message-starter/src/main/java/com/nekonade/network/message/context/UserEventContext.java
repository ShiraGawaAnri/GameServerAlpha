package com.nekonade.network.message.context;


import com.nekonade.network.message.channel.AbstractGameChannelHandlerContext;

public class UserEventContext<T> {

    private final T dataManager;
    private final AbstractGameChannelHandlerContext ctx;


    public UserEventContext(T dataManager, AbstractGameChannelHandlerContext ctx) {
        super();
        this.dataManager = dataManager;
        this.ctx = ctx;
    }

    public T getDataManager() {
        return dataManager;
    }

    public AbstractGameChannelHandlerContext getCtx() {
        return ctx;
    }


}
