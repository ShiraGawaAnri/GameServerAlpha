package com.nekonade.raidbattle.business;

import com.nekonade.common.dto.Player;
import com.nekonade.dao.db.entity.RaidBattle;
import com.nekonade.network.param.game.message.neko.battle.JoinRaidBattleMsgResponse;
import com.nekonade.raidbattle.manager.RaidBattleManager;
import com.nekonade.raidbattle.message.context.RaidBattleMessageContext;
import com.nekonade.raidbattle.message.context.RaidBattleEvent;
import com.nekonade.network.param.game.common.IGameMessage;
import com.nekonade.network.param.game.message.neko.BuyArenaChallengeTimesMsgRequest;
import com.nekonade.network.param.game.message.neko.BuyArenaChallengeTimesMsgResponse;
import com.nekonade.network.param.game.message.neko.battle.JoinRaidBattleMsgRequest;
import com.nekonade.network.param.game.message.neko.battle.rpc.JoinRaidBattleRPCRequest;
import com.nekonade.network.param.game.message.neko.battle.rpc.JoinRaidBattleRPCResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;
import com.nekonade.network.param.game.rpc.ConsumeDiamondRPCRequest;
import com.nekonade.network.param.game.rpc.ConsumeDiamondRPCResponse;
import com.nekonade.raidbattle.error.RaidBattleError;
import com.nekonade.raidbattle.message.context.RaidBattleEventContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

@GameMessageHandler
public class RaidBattleBusinessHandler {

    private final static Logger logger = LoggerFactory.getLogger(RaidBattleBusinessHandler.class);

    @RaidBattleEvent(IdleStateEvent.class)
    public void idleStateEvent(RaidBattleEventContext<RaidBattleManager> ctx, IdleStateEvent event, Promise<Object> promise) {
        logger.debug("收到空闲事件：{}", event.getClass().getName());
        ctx.getCtx().close();
    }

    @GameMessageMapping(JoinRaidBattleMsgRequest.class)
    public void joinRaidBattleMsgRequest(JoinRaidBattleMsgRequest request, RaidBattleMessageContext<RaidBattleManager> ctx) {
        JoinRaidBattleRPCRequest joinRaidBattleMsgRequest = new JoinRaidBattleRPCRequest();
        joinRaidBattleMsgRequest.getHeader().setPlayerId(ctx.getPlayerId());
        Promise<IGameMessage> promise = ctx.newPromise();
        promise.addListener((GenericFutureListener<Future<IGameMessage>>) future -> {
            if (future.isSuccess()) {
                JoinRaidBattleRPCResponse rpcResponse = (JoinRaidBattleRPCResponse) future.get();
                String raidId = rpcResponse.getHeader().getAttribute().getRaidId();
                if(rpcResponse.getHeader().getErrorCode() == 0) {
                    // 如果错码为0，表示扣钻石成功，可以增加挑战次数
                    logger.info("由RB服务器处理加入RaidBattle {} 的请求",raidId);
                    RaidBattle raidBattle = ctx.getDataManager().getRaidBattle();
                    Player player = rpcResponse.getBodyObj().getPlayer();
                    ctx.getDataManager().addPlayer(player);
                    JoinRaidBattleMsgResponse response = new JoinRaidBattleMsgResponse();
                    BeanUtils.copyProperties(raidBattle,response.getBodyObj());
                    ctx.sendMessage(response);
                }
            } else {
                logger.error("竞技场扣除钻石失败",future.cause());
                //向客户端返回错误码
            }
        });
        ctx.sendRPCMessage(joinRaidBattleMsgRequest, promise);
    }


    @GameMessageMapping(BuyArenaChallengeTimesMsgRequest.class)
    public void buyChallengeTimes(BuyArenaChallengeTimesMsgRequest request, RaidBattleMessageContext<RaidBattleManager> ctx) {
        // 先通过rpc扣除钻石，扣除成功之后，再添加挑战次数
        BuyArenaChallengeTimesMsgResponse response = new BuyArenaChallengeTimesMsgResponse();
        Promise<IGameMessage> rpcPromise = ctx.newRPCPromise();
        rpcPromise.addListener((GenericFutureListener<Future<IGameMessage>>) future -> {
            if (future.isSuccess()) {
                ConsumeDiamondRPCResponse rpcResponse = (ConsumeDiamondRPCResponse) future.get();
                int errorCode = rpcResponse.getHeader().getErrorCode();
                if (errorCode == 0) {

                    logger.debug("购买竞技挑战次数成功");
                } else {
                    response.getHeader().setErrorCode(errorCode);
                }
            } else {
                response.getHeader().setErrorCode(RaidBattleError.SERVER_ERROR.getErrorCode());
            }
            ctx.sendMessage(response);
        });
        ConsumeDiamondRPCRequest rpcRequest = new ConsumeDiamondRPCRequest();
        rpcRequest.getBodyObj().setConsumeCount(20);// 假设是20钻石
        ctx.sendRPCMessage(rpcRequest, rpcPromise);
    }
}
