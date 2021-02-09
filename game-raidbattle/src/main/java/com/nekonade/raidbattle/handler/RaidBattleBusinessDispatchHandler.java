package com.nekonade.raidbattle.handler;

import com.nekonade.common.dto.RaidBattle;
import com.nekonade.dao.daos.AsyncRaidBattleDao;
import com.nekonade.network.message.channel.battle.AbstractRaidBattleChannelHandlerContext;
import com.nekonade.network.message.channel.battle.RaidBattleChannelPromise;
import com.nekonade.network.message.handler.battle.AbstractRaidBattleMessageDispatchHandler;
import com.nekonade.network.message.manager.RaidBattleManager;
import com.nekonade.network.param.game.common.IGameMessage;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ConcurrentHashMap;

public class RaidBattleBusinessDispatchHandler extends AbstractRaidBattleMessageDispatchHandler<RaidBattleManager> {

    private final static Logger logger = LoggerFactory.getLogger(RaidBattleBusinessDispatchHandler.class);

    private RaidBattleManager raidBattleManager;

    private final AsyncRaidBattleDao asyncRaidBattleDao;

    public RaidBattleBusinessDispatchHandler(ApplicationContext applicationContext) {
        super(applicationContext);
        this.asyncRaidBattleDao = applicationContext.getBean(AsyncRaidBattleDao.class);
    }
    @Override
    protected RaidBattleManager getDataManager() {
        return raidBattleManager;
    }

    @Override
    protected Future<Boolean> updateToRedis(Promise<Boolean> promise) {
         asyncRaidBattleDao.updateToRedis("#unset#", raidBattleManager.getRaidBattle(),promise);
         return null;
    }

    @Override
    protected Future<Boolean> updateToDB(Promise<Boolean> promise) {
         asyncRaidBattleDao.updateToDB("#unset#", raidBattleManager.getRaidBattle(), promise);
         return null;
    }

    @Override
    public void channelRegister(AbstractRaidBattleChannelHandlerContext ctx, String raidId, RaidBattleChannelPromise promise) {
        super.channelRegister(ctx, raidId, promise);
    }

    @Override
    public void channelInactive(AbstractRaidBattleChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(AbstractRaidBattleChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void userEventTriggered(AbstractRaidBattleChannelHandlerContext ctx, Object evt, Promise<Object> promise) throws Exception {
        super.userEventTriggered(ctx, evt, promise);
    }

    @Override
    public void channelReadRPCRequest(AbstractRaidBattleChannelHandlerContext ctx, IGameMessage msg) throws Exception {
        super.channelReadRPCRequest(ctx, msg);
    }

    @Override
    protected void initData(AbstractRaidBattleChannelHandlerContext ctx, String raidId, RaidBattleChannelPromise promise) {
//        raidBattleMaps = new ConcurrentHashMap<>();
        // 异步加载竞技场信息
//        Promise<Optional<Arena>> arenaPromise = new DefaultPromise<>(ctx.executor());
//        asyncArenaDao.findArena(playerId, arenaPromise).addListener(new GenericFutureListener<Future<Optional<Arena>>>() {
//            @Override
//            public void operationComplete(Future<Optional<Arena>> future) throws Exception {
//                if (future.isSuccess()) {
//                    Optional<Arena> arOptional = future.get();
//                    if (arOptional.isPresent()) {
//                        arenaManager = new ArenaManager(arOptional.get());
//                    } else {
//                        Arena arena = new Arena();
//                        arena.setPlayerId(playerId);
//                        arenaManager = new ArenaManager(arena);
//                    }
//                    promise.setSuccess();
//                } else {
//                    logger.error("查询竞技场信息失败", future.cause());
//                    promise.setFailure(future.cause());
//                }
//            }
//        });
        promise.setSuccess();
    }
}
