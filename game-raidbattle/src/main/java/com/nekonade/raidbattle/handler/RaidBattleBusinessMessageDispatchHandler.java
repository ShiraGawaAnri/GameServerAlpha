package com.nekonade.raidbattle.handler;

import com.alibaba.fastjson.JSON;
import com.nekonade.common.constraint.RedisConstraint;
import com.nekonade.dao.daos.AsyncRaidBattleDao;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.entity.RaidBattle;
import com.nekonade.network.param.game.messagedispatcher.DispatchGameMessageService;
import com.nekonade.raidbattle.manager.RaidBattleManager;
import com.nekonade.raidbattle.message.ServerConfig;
import com.nekonade.raidbattle.message.channel.AbstractRaidBattleChannelHandlerContext;
import com.nekonade.raidbattle.message.channel.RaidBattleChannelPromise;
import com.nekonade.network.param.game.common.IGameMessage;
import com.nekonade.raidbattle.message.context.DispatchRaidBattleEventService;
import com.nekonade.raidbattle.message.context.RaidBattleEventContext;
import com.nekonade.raidbattle.message.context.RaidBattleMessageContext;
import com.nekonade.raidbattle.message.handler.AbstractRaidBattleMessageDispatchHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RaidBattleBusinessMessageDispatchHandler extends AbstractRaidBattleMessageDispatchHandler<RaidBattleManager> {

    private final static Logger logger = LoggerFactory.getLogger(RaidBattleBusinessMessageDispatchHandler.class);

    private final DispatchGameMessageService dispatchGameMessageService;

    private final DispatchRaidBattleEventService dispatchRaidBattleEventService;

    private final AsyncRaidBattleDao raidBattleDao;

    private final ServerConfig serverConfig;

    private final ApplicationContext context;

    private RaidBattle raidBattle;

    private RaidBattleManager raidBattleManager;


    private ScheduledFuture<?> flushToRedisScheduleFuture;
    private ScheduledFuture<?> flushToDBScheduleFuture;


    public RaidBattleBusinessMessageDispatchHandler(ApplicationContext applicationContext) {
        super(applicationContext);
        this.context = applicationContext;
        this.raidBattleDao = applicationContext.getBean(AsyncRaidBattleDao.class);
        this.dispatchGameMessageService = applicationContext.getBean(DispatchGameMessageService.class);
        this.serverConfig = applicationContext.getBean(ServerConfig.class);;
        this.dispatchRaidBattleEventService = applicationContext.getBean(DispatchRaidBattleEventService.class);
    }
    @Override
    protected RaidBattleManager getDataManager() {
        return raidBattleManager;
    }

    @Override
    protected Future<Boolean> updateToRedis(Promise<Boolean> promise) {
         raidBattleDao.updateToRedis(raidId, raidBattleManager.getRaidBattle(),promise);
         return null;
    }

    @Override
    protected Future<Boolean> updateToDB(Promise<Boolean> promise) {
         raidBattleDao.updateToDB(raidId, raidBattleManager.getRaidBattle(), promise);
         return null;
    }

    @Override
    public void channelRegister(AbstractRaidBattleChannelHandlerContext ctx, String raidId, RaidBattleChannelPromise promise) {
        String result = raidBattleDao.findPlayerFromRedis(raidId);
        if (!StringUtils.isEmpty(result) && !result.equals(RedisConstraint.RedisDefaultValue)) {
            try {
                raidBattle = JSON.parseObject(result, RaidBattle.class);
                raidBattleManager = new RaidBattleManager(raidBattle, context, ctx.gameChannel());
                promise.setSuccess();
                fixTimerFlushRaidBattle(ctx);
                return;
            } catch (Exception e) {
                logger.error("channel注册时,redis转换失败,从MongoDb查找 RaidBattle {}", raidId);
            }
        }
        CompletableFuture<Optional<RaidBattle>> findFromDb = raidBattleDao.findRaidBattle(raidId);
        findFromDb.whenComplete((r,e)->{
            if(e == null){
                if(r.isPresent()){
                    raidBattle = r.get();
                    raidBattleManager = new RaidBattleManager(raidBattle, context, ctx.gameChannel());
                    promise.setSuccess();
                    fixTimerFlushRaidBattle(ctx);
                }else{
                    logger.error("RaidBattle {} 不存在", raidId);
//                    throw new IllegalArgumentException("找不到RaidBattle数据，raidId:" + raidId);
                    promise.setFailure(new IllegalArgumentException("找不到RaidBattle数据，raidId:" + raidId));
                }
            }else{
                promise.setFailure(e);
            }
        });
    }



    @Override
    public void channelInactive(AbstractRaidBattleChannelHandlerContext ctx) throws Exception {
        if (flushToDBScheduleFuture != null) {// 取消定时器
            flushToDBScheduleFuture.cancel(true);
        }
        if (flushToRedisScheduleFuture != null) {
            flushToRedisScheduleFuture.cancel(true);
        }
        this.raidBattleDao.syncFlushRaidBattle(raidBattle);// GameChannel移除的时候，强制更新一次数据
        logger.debug("强制flush RaidBattle {} 成功", raidBattle.getRaidId());
        logger.debug("game channel 移除，raidId:{}", ctx.gameChannel().getRaidId());
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(AbstractRaidBattleChannelHandlerContext ctx, Object msg) throws Exception {
        IGameMessage gameMessage = (IGameMessage) msg;
        RaidBattleMessageContext<RaidBattleManager> stx = new RaidBattleMessageContext<>(raidBattleManager, gameMessage, ctx);
        dispatchGameMessageService.callMethod(gameMessage, stx);
    }

    @Override
    public void userEventTriggered(AbstractRaidBattleChannelHandlerContext ctx, Object evt, Promise<Object> promise) throws Exception {
        RaidBattleEventContext<RaidBattleManager> utx = new RaidBattleEventContext<>(raidBattleManager, ctx);
        dispatchRaidBattleEventService.callMethod(utx, evt, promise);
    }

    @Override
    public void channelReadRPCRequest(AbstractRaidBattleChannelHandlerContext ctx, IGameMessage msg) throws Exception {
        super.channelReadRPCRequest(ctx, msg);
    }

    @Override
    protected void initData(AbstractRaidBattleChannelHandlerContext ctx, String raidId, RaidBattleChannelPromise promise) {
        promise.setSuccess();
    }

    private void fixTimerFlushRaidBattle(AbstractRaidBattleChannelHandlerContext ctx) {
        int flushRedisDelay = serverConfig.getFlushRedisDelaySecond();// 获取定时器执行的延迟时间，单位是秒
        int flushDBDelay = serverConfig.getFlushDBDelaySecond();
        flushToRedisScheduleFuture = ctx.executor().scheduleWithFixedDelay(() -> {// 创建持久化数据到redis的定时任务
            long start = System.currentTimeMillis();// 任务开始执行的时间
            CompletableFuture<Boolean> future = raidBattleDao.saveOrUpdateRaidBattleToRedis(raidBattle);
            future.whenComplete((r,e)->{
               if(e == null){
                   if (logger.isDebugEnabled()) {
                       long end = System.currentTimeMillis();
                       logger.debug("RaidBattle {} 同步数据到redis成功,耗时:{} ms", raidBattle, (end - start));
                   }
               } else {
                   logger.error("RaidBattle {} 同步数据到Redis失败", raidBattle.getRaidId());
               }
            });
            }, flushRedisDelay, flushRedisDelay, TimeUnit.SECONDS);
        flushToDBScheduleFuture = ctx.executor().scheduleWithFixedDelay(() -> {
            long start = System.currentTimeMillis();
            CompletableFuture<Boolean> future = raidBattleDao.saveOrUpdateRaidBattleToDB(raidBattle);
            future.whenComplete((r,e)->{
                if(e == null){
                    if (logger.isDebugEnabled()) {
                        long end = System.currentTimeMillis();
                        logger.debug("RaidBattle {} 同步数据到MongoDb成功,耗时:{} ms", raidBattle, (end - start));
                    }
                } else {
                    logger.error("RaidBattle {} 同步数据到MongoDb失败", raidBattle.getRaidId());
                }
            });
        }, flushDBDelay, flushDBDelay, TimeUnit.SECONDS);
    }

}
