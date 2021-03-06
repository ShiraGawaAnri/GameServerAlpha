package com.nekonade.network.message.handler;

import com.nekonade.common.gameMessage.DataManager;
import com.nekonade.network.message.channel.AbstractGameChannelHandlerContext;
import com.nekonade.network.message.channel.GameChannelInboundHandler;
import com.nekonade.network.message.channel.GameChannelPromise;
import com.nekonade.network.message.context.DispatchUserEventService;
import com.nekonade.network.message.context.GatewayMessageContext;
import com.nekonade.network.message.config.ServerConfig;
import com.nekonade.network.message.context.UserEventContext;
import com.nekonade.network.message.rpc.DispatchRPCEventService;
import com.nekonade.network.message.rpc.RPCEventContext;
import com.nekonade.common.gameMessage.IGameMessage;
import com.nekonade.network.param.game.messagedispatcher.DispatchGameMessageService;
import io.netty.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

public abstract class AbstractGameMessageDispatchHandler<T extends DataManager> implements GameChannelInboundHandler {
    private final DispatchRPCEventService dispatchRPCEventService;
    private final DispatchGameMessageService dispatchGameMessageService;
    private final DispatchUserEventService dispatchUserEventService;
    private final ServerConfig serverConfig;
    protected long playerId;
    protected Logger logger;
    protected int gatewayServerId;
    private ScheduledFuture<?> flushToRedisScheduleFuture;
    private ScheduledFuture<?> flushToDBScheduleFuture;
    public AbstractGameMessageDispatchHandler(ApplicationContext applicationContext) {
        this.dispatchRPCEventService = applicationContext.getBean(DispatchRPCEventService.class);
        this.dispatchGameMessageService = applicationContext.getBean(DispatchGameMessageService.class);
        this.dispatchUserEventService = applicationContext.getBean(DispatchUserEventService.class);
        this.serverConfig = applicationContext.getBean(ServerConfig.class);
        logger = LoggerFactory.getLogger(this.getClass());
    }

    protected abstract T getDataManager();

    @Override
    public void exceptionCaught(AbstractGameChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void channelRegister(AbstractGameChannelHandlerContext ctx, long playerId, GameChannelPromise promise) {
        this.playerId = playerId;
        GameChannelPromise initPromise = ctx.newPromise();
        initPromise.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                // ???????????????????????????????????????????????????????????????
                fixTimerFlushPlayer(ctx);
                promise.setSuccess();
            }
        });
        this.initData(ctx, playerId, initPromise);
    }

    @Override
    public void channelInactive(AbstractGameChannelHandlerContext ctx) throws Exception {
        if (flushToDBScheduleFuture != null) {// ???????????????
            flushToDBScheduleFuture.cancel(true);
        }
        if (flushToRedisScheduleFuture != null) {
            flushToRedisScheduleFuture.cancel(true);
        }
        this.updateToRedis0(ctx);
        this.updateToDB0(ctx);
        logger.debug("game channel ?????????playerId:{}", ctx.gameChannel().getPlayerId());
        ctx.fireChannelInactive();// ????????????Handler??????channel????????????
    }

    @Override
    public void channelRead(AbstractGameChannelHandlerContext ctx, Object msg) throws Exception {
        IGameMessage gameMessage = (IGameMessage) msg;
        T dataManager = this.getDataManager();
        GatewayMessageContext<T> stx = new GatewayMessageContext<>(dataManager, gameMessage, ctx);
        dispatchGameMessageService.callMethod(gameMessage, stx);
    }


    @Override
    public void userEventTriggered(AbstractGameChannelHandlerContext ctx, Object evt, Promise<Object> promise) throws Exception {
        T data = this.getDataManager();
        UserEventContext<T> utx = new UserEventContext<>(data, ctx);
        dispatchUserEventService.callMethod(utx, evt, promise);
    }

    @Override
    public void channelReadRPCRequest(AbstractGameChannelHandlerContext ctx, IGameMessage msg) throws Exception {
        T data = this.getDataManager();
        RPCEventContext<T> rpcEventContext = new RPCEventContext<>(data, msg, ctx);
        this.dispatchRPCEventService.callMethod(rpcEventContext, msg);
    }

    protected abstract Future<Boolean> updateToRedis(Promise<Boolean> promise);

    protected abstract Future<Boolean> updateToDB(Promise<Boolean> promise);

    protected long getPlayerId() {
        return playerId;
    }

    protected abstract void initData(AbstractGameChannelHandlerContext ctx, long playerId, GameChannelPromise promise);

    private void fixTimerFlushPlayer(AbstractGameChannelHandlerContext ctx) {
        int flushRedisDelay = serverConfig.getFlushRedisDelaySecond();// ???????????????????????????????????????????????????
        int flushDBDelay = serverConfig.getFlushDBDelaySecond();
        flushToRedisScheduleFuture = ctx.executor().scheduleWithFixedDelay(() -> {// ????????????????????????redis???????????????
            this.updateToRedis0(ctx);
        }, flushRedisDelay, flushRedisDelay, TimeUnit.SECONDS);
        flushToDBScheduleFuture = ctx.executor().scheduleWithFixedDelay(() -> {
            this.updateToDB0(ctx);
        }, flushDBDelay, flushDBDelay, TimeUnit.SECONDS);
    }

    private void updateToRedis0(AbstractGameChannelHandlerContext ctx) {
        long start = System.currentTimeMillis();// ???????????????????????????
        Promise<Boolean> promise = new DefaultPromise<>(ctx.executor());
        this.updateToRedis(promise).addListener(new GenericFutureListener<Future<Boolean>>() {
            @Override
            public void operationComplete(Future<Boolean> future) throws Exception {
                if (future.isSuccess()) {
                    if (logger.isDebugEnabled()) {
                        long end = System.currentTimeMillis();
                        logger.debug("player {} ???????????????redis??????,??????:{} ms", getPlayerId(), (end - start));
                    }
                } else {
                    logger.error("player {} ???????????????Redis??????", getPlayerId());
                    // ????????????????????????
                }
            }
        });
    }

    private void updateToDB0(AbstractGameChannelHandlerContext ctx) {
        long start = System.currentTimeMillis();// ????????????????????????
        Promise<Boolean> promise = new DefaultPromise<>(ctx.executor());
        updateToDB(promise).addListener((GenericFutureListener<Future<Boolean>>) future -> {
            if (future.isSuccess()) {
                if (logger.isDebugEnabled()) {
                    long end = System.currentTimeMillis();
                    logger.debug("player {} ???????????????MongoDB??????,??????:{} ms", getPlayerId(), (end - start));
                }
            } else {
                logger.error("player {} ???????????????MongoDB??????", getPlayerId());
                // ????????????????????????,??????????????????????????????????????????
            }
        });
    }

}
