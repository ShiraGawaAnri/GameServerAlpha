package com.nekonade.raidbattle.message.handler;

import com.nekonade.common.gameMessage.DataManager;
import com.nekonade.common.gameMessage.IGameMessage;
import com.nekonade.network.param.game.messagedispatcher.DispatchGameMessageService;
import com.nekonade.raidbattle.manager.RaidBattleManager;
import com.nekonade.raidbattle.message.ServerConfig;
import com.nekonade.raidbattle.message.channel.AbstractRaidBattleChannelHandlerContext;
import com.nekonade.raidbattle.message.channel.RaidBattleChannelInboundHandler;
import com.nekonade.raidbattle.message.channel.RaidBattleChannelPromise;
import com.nekonade.raidbattle.message.context.DispatchRaidBattleEventService;
import com.nekonade.raidbattle.message.context.RaidBattleEventContext;
import com.nekonade.raidbattle.message.context.RaidBattleMessageContext;
import com.nekonade.raidbattle.message.rpc.DispatchRaidBattleRPCEventService;
import com.nekonade.raidbattle.message.rpc.RaidBattleRPCEventContext;
import io.netty.util.concurrent.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

public abstract class AbstractRaidBattleMessageDispatchHandler<T extends DataManager> implements RaidBattleChannelInboundHandler {
    private final DispatchRaidBattleRPCEventService dispatchRPCEventService;
    private final DispatchGameMessageService dispatchGameMessageService;
    private final DispatchRaidBattleEventService dispatchUserEventService;
    private final ServerConfig serverConfig;
    @Getter
    protected String raidId;
    protected Logger logger;
    protected int gatewayServerId;
    private ScheduledFuture<?> flushToRedisScheduleFuture;
    private ScheduledFuture<?> flushToDBScheduleFuture;
    public AbstractRaidBattleMessageDispatchHandler(ApplicationContext applicationContext) {
        this.dispatchRPCEventService = applicationContext.getBean(DispatchRaidBattleRPCEventService.class);
        this.dispatchGameMessageService = applicationContext.getBean(DispatchGameMessageService.class);
        this.dispatchUserEventService = applicationContext.getBean(DispatchRaidBattleEventService.class);
        this.serverConfig = applicationContext.getBean(ServerConfig.class);
        logger = LoggerFactory.getLogger(this.getClass());
    }

    protected abstract T getDataManager();

    @Override
    public void exceptionCaught(AbstractRaidBattleChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void channelRegister(AbstractRaidBattleChannelHandlerContext ctx, String raidId, RaidBattleChannelPromise promise) {
        this.raidId = raidId;
        RaidBattleChannelPromise initPromise = ctx.newPromise();
        initPromise.addListener(future -> {
            // ???????????????????????????????????????????????????????????????
            fixTimerFlushRaidBattle(ctx);
            promise.setSuccess();
        });
        this.initData(ctx, raidId, initPromise);
    }

    @Override
    public void channelInactive(AbstractRaidBattleChannelHandlerContext ctx) throws Exception {
        if (flushToDBScheduleFuture != null) {// ???????????????
            flushToDBScheduleFuture.cancel(true);
        }
        if (flushToRedisScheduleFuture != null) {
            flushToRedisScheduleFuture.cancel(true);
        }
        this.updateToRedis0(ctx);
        this.updateToDB0(ctx);
        logger.debug("game channel ?????????playerId:{}", ctx.gameChannel().getRaidId());
        ctx.fireChannelInactive();// ????????????Handler??????channel????????????
    }

    @Override
    public void channelRead(AbstractRaidBattleChannelHandlerContext ctx, Object msg) throws Exception {
        IGameMessage gameMessage = (IGameMessage) msg;
        T dataManager = this.getDataManager();
        RaidBattleMessageContext<T> rtx = new RaidBattleMessageContext<>(dataManager, gameMessage, ctx);
        dataManager.seqIncr();
        dispatchGameMessageService.callMethod(gameMessage, rtx);
    }


    @Override
    public void userEventTriggered(AbstractRaidBattleChannelHandlerContext ctx, Object evt, Promise<Object> promise) throws Exception {
        T data = this.getDataManager();
        RaidBattleEventContext<T> utx = new RaidBattleEventContext<>(data, ctx);
        dispatchUserEventService.callMethod(utx, evt, promise);
    }

    @Override
    public void channelReadRPCRequest(AbstractRaidBattleChannelHandlerContext ctx, IGameMessage msg) throws Exception {
        T data = this.getDataManager();
        RaidBattleRPCEventContext<T> rpcEventContext = new RaidBattleRPCEventContext<>(data, msg, ctx);
        this.dispatchRPCEventService.callMethod(rpcEventContext, msg);
    }

    protected abstract Future<Boolean> updateToRedis(Promise<Boolean> promise);

    protected abstract Future<Boolean> updateToDB(Promise<Boolean> promise);

    protected abstract void initData(AbstractRaidBattleChannelHandlerContext ctx, String raidId, RaidBattleChannelPromise promise);

    private void fixTimerFlushRaidBattle(AbstractRaidBattleChannelHandlerContext ctx) {
        int flushRedisDelay = serverConfig.getFlushRedisDelaySecond();// ???????????????????????????????????????????????????
        int flushDBDelay = serverConfig.getFlushDBDelaySecond();
        flushToRedisScheduleFuture = ctx.executor().scheduleWithFixedDelay(() -> {// ????????????????????????redis???????????????
            this.updateToRedis0(ctx);
        }, flushRedisDelay, flushRedisDelay, TimeUnit.SECONDS);
        flushToDBScheduleFuture = ctx.executor().scheduleWithFixedDelay(() -> {
            this.updateToDB0(ctx);
        }, flushDBDelay, flushDBDelay, TimeUnit.SECONDS);
    }

    private void updateToRedis0(AbstractRaidBattleChannelHandlerContext ctx) {
        long start = System.currentTimeMillis();// ???????????????????????????
        Promise<Boolean> promise = new DefaultPromise<>(ctx.executor());
        this.updateToRedis(promise).addListener(new GenericFutureListener<Future<Boolean>>() {
            @Override
            public void operationComplete(Future<Boolean> future) throws Exception {
                if (future.isSuccess()) {
                    if (logger.isDebugEnabled()) {
                        long end = System.currentTimeMillis();
                        logger.debug("raidId {} ???????????????redis??????,??????:{} ms", getRaidId(), (end - start));
                    }
                } else {
                    logger.error("raidId {} ???????????????Redis??????", getRaidId());
                    // ????????????????????????
                }
            }
        });
    }

    private void updateToDB0(AbstractRaidBattleChannelHandlerContext ctx) {
        long start = System.currentTimeMillis();// ????????????????????????
        Promise<Boolean> promise = new DefaultPromise<>(ctx.executor());
        updateToDB(promise).addListener(new GenericFutureListener<Future<Boolean>>() {
            @Override
            public void operationComplete(Future<Boolean> future) throws Exception {
                if (future.isSuccess()) {
                    if (logger.isDebugEnabled()) {
                        long end = System.currentTimeMillis();
                        logger.debug("player {} ???????????????MongoDB??????,??????:{} ms", getRaidId(), (end - start));
                    }
                } else {
                    logger.error("player {} ???????????????MongoDB??????", getRaidId());
                    // ????????????????????????,??????????????????????????????????????????
                }
            }
        });
    }

}
