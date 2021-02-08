package com.nekonade.raidbattle.business;

import com.nekonade.network.message.context.GatewayMessageContext;
import com.nekonade.network.message.context.UserEvent;
import com.nekonade.network.message.context.UserEventContext;
import com.nekonade.network.message.manager.PlayerManager;
import com.nekonade.network.message.manager.RaidBattleManager;
import com.nekonade.network.param.game.common.IGameMessage;
import com.nekonade.network.param.game.message.neko.BuyArenaChallengeTimesMsgRequest;
import com.nekonade.network.param.game.message.neko.BuyArenaChallengeTimesMsgResponse;
import com.nekonade.network.param.game.message.neko.JoinRaidBattleMsgRequest;
import com.nekonade.network.param.game.message.neko.rpc.JoinRaidBattleRPCRequest;
import com.nekonade.network.param.game.message.neko.rpc.JoinRaidBattleRPCResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;
import com.nekonade.network.param.game.rpc.ConsumeDiamondRPCRequest;
import com.nekonade.network.param.game.rpc.ConsumeDiamondRPCResponse;
import com.nekonade.raidbattle.error.RaidBattleError;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GameMessageHandler
public class RaidBattleBusinessHandler {

    private final static Logger logger = LoggerFactory.getLogger(RaidBattleBusinessHandler.class);

    @UserEvent(IdleStateEvent.class)
    public void idleStateEvent(UserEventContext<RaidBattleManager> ctx, IdleStateEvent event, Promise<Object> promise) {
        logger.debug("收到空闲事件：{}", event.getClass().getName());
        ctx.getCtx().close();
    }

    @GameMessageMapping(JoinRaidBattleMsgRequest.class) // 接收客户端购买竞技场挑战次数的请求
    public void joinRaidBattleMsgRequest(JoinRaidBattleMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        JoinRaidBattleRPCRequest joinRaidBattleMsgRequest = new JoinRaidBattleRPCRequest();
        Promise<IGameMessage> promise = ctx.newPromise();
        promise.addListener((GenericFutureListener<Future<IGameMessage>>) future -> {
            if (future.isSuccess()) {
                JoinRaidBattleRPCResponse rpcResponse = (JoinRaidBattleRPCResponse) future.get();
                if(rpcResponse.getHeader().getErrorCode() == 0) {
                    // 如果错码为0，表示扣钻石成功，可以增加挑战次数
                }
            } else {
                logger.error("竞技场扣除钻石失败",future.cause());
                //向客户端返回错误码
            }
        });
        ctx.sendRPCMessage(joinRaidBattleMsgRequest, promise);
    }


    @GameMessageMapping(BuyArenaChallengeTimesMsgRequest.class)
    public void buyChallengeTimes(BuyArenaChallengeTimesMsgRequest request, GatewayMessageContext<RaidBattleManager> ctx) {
        // 先通过rpc扣除钻石，扣除成功之后，再添加挑战次数
        BuyArenaChallengeTimesMsgResponse response = new BuyArenaChallengeTimesMsgResponse();
        Promise<IGameMessage> rpcPromise = ctx.newRPCPromise();
        rpcPromise.addListener((GenericFutureListener<Future<IGameMessage>>) future -> {
            if (future.isSuccess()) {
                ConsumeDiamondRPCResponse rpcResponse = (ConsumeDiamondRPCResponse) future.get();
                int errorCode = rpcResponse.getHeader().getErrorCode();
                if (errorCode == 0) {
                    ctx.getDataManager().addChallengeTimes(10);// 假设添加10次竞技场挑战次
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
