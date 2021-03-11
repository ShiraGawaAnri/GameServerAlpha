package com.nekonade.raidbattle.business;

import com.nekonade.common.concurrent.GameEventExecutorGroup;
import com.nekonade.common.dto.PlayerDTO;
import com.nekonade.common.dto.RaidBattleDamageDTO;
import com.nekonade.common.error.GameNotifyException;
import com.nekonade.common.error.code.GameErrorCode;
import com.nekonade.common.gameMessage.GameMessageHeader;
import com.nekonade.common.gameMessage.IGameMessage;
import com.nekonade.dao.daos.CardsDbDao;
import com.nekonade.dao.db.entity.RaidBattle;
import com.nekonade.network.param.game.message.battle.JoinRaidBattleMsgRequest;
import com.nekonade.network.param.game.message.battle.JoinRaidBattleMsgResponse;
import com.nekonade.network.param.game.message.battle.RaidBattleCardAttackMsgRequest;
import com.nekonade.network.param.game.message.battle.rpc.JoinRaidBattleRPCRequest;
import com.nekonade.network.param.game.message.battle.rpc.JoinRaidBattleRPCResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;
import com.nekonade.raidbattle.event.function.PushRaidBattleEvent;
import com.nekonade.raidbattle.event.function.RaidBattleShouldBeFinishEvent;
import com.nekonade.raidbattle.event.user.PushRaidBattleDamageDTOEventUser;
import com.nekonade.raidbattle.event.user.PushRaidBattleToSinglePlayerEventUser;
import com.nekonade.raidbattle.manager.RaidBattleManager;
import com.nekonade.raidbattle.message.context.RaidBattleEvent;
import com.nekonade.raidbattle.message.context.RaidBattleEventContext;
import com.nekonade.raidbattle.message.context.RaidBattleMessageContext;
import com.nekonade.raidbattle.service.CalcRaidBattleService;
import com.nekonade.raidbattle.service.GameErrorService;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;

@GameMessageHandler
public class RaidBattleBusinessHandler {

    private final static Logger logger = LoggerFactory.getLogger(RaidBattleBusinessHandler.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private GameErrorService gameErrorService;

    @Autowired
    private CalcRaidBattleService calcRaidBattleService;

    @Autowired
    private CardsDbDao cardsDbDao;

    @RaidBattleEvent(IdleStateEvent.class)
    public void idleStateEvent(RaidBattleEventContext<RaidBattleManager> ctx, IdleStateEvent event, Promise<Object> promise) {
        IdleState state = event.state();
        logger.debug("RB收到空闲事件：{} state : {}", event.getClass().getName(), state.name());
        switch (state) {
            case READER_IDLE:
            case WRITER_IDLE:
            default:
                break;
            case ALL_IDLE:
//                检查该战斗的Enemy的状态
//                ctx.getCtx().close();
                RaidBattleShouldBeFinishEvent raidBattleShouldBeFinishEvent = new RaidBattleShouldBeFinishEvent(this, ctx.getDataManager(), true);
                context.publishEvent(raidBattleShouldBeFinishEvent);
                break;
        }
    }

    @GameMessageMapping(JoinRaidBattleMsgRequest.class)
    public void joinRaidBattleMsgRequest(JoinRaidBattleMsgRequest request, RaidBattleMessageContext<RaidBattleManager> ctx) {
        JoinRaidBattleRPCRequest joinRaidBattleMsgRequest = new JoinRaidBattleRPCRequest();
        joinRaidBattleMsgRequest.getHeader().setPlayerId(ctx.getPlayerId());
        Promise<IGameMessage> promise = ctx.newPromise();
        promise.addListener((GenericFutureListener<Future<IGameMessage>>) future -> {
            try {
                if (future.isSuccess()) {
                    JoinRaidBattleRPCResponse rpcResponse = (JoinRaidBattleRPCResponse) future.get();
                    String raidId = rpcResponse.getHeader().getAttribute().getRaidId();
                    int errorCode = rpcResponse.getHeader().getErrorCode();
                    if (errorCode == 0) {
                        logger.info("由RB服务器处理加入RaidBattle {} 的请求", raidId);
                        RaidBattle raidBattle = ctx.getDataManager().getRaidBattle();
                        PlayerDTO playerDTO = rpcResponse.getBodyObj().getPlayer();
                        DefaultPromise<Object> promise1 = ctx.newPromise();
                        ctx.getDataManager().playerJoinRaidBattle(playerDTO, promise1).addListener(future1 -> {
                            if (future1.isSuccess()) {
                                JoinRaidBattleMsgResponse response = new JoinRaidBattleMsgResponse();
                                BeanUtils.copyProperties(raidBattle, response.getBodyObj());
                                if(raidBattle.getPlayers().size() > 30){
                                    response.getBodyObj().setPlayers(null);
                                }
                                ctx.sendMessage(response);
                            } else {
                                Throwable e = future1.cause();
                                gameErrorService.returnGameErrorResponse(e, ctx);
                            }
                        });
                        /*promise1.addListener(future1 -> {
                            if (future1.isSuccess()) {
                                JoinRaidBattleMsgResponse response = new JoinRaidBattleMsgResponse();
                                BeanUtils.copyProperties(raidBattle, response.getBodyObj());
                                ctx.sendMessage(response);
                            } else {
                                Throwable e = future1.cause();
                                gameErrorService.returnGameErrorResponse(e, ctx);
                            }

                        });*/
                    } else {
                        //暂时处理
                        throw GameNotifyException.newBuilder(GameErrorCode.RaidBattleJoinWithEmptyParty).build();
                    }
                } else {
                    logger.error("加入战斗失败", future.cause());
                    throw future.cause();
                }
            } catch (Throwable e) {
                gameErrorService.returnGameErrorResponse(e, ctx);
            }

        });
        ctx.sendRPCMessage(joinRaidBattleMsgRequest, promise);
    }



    @GameMessageMapping(RaidBattleCardAttackMsgRequest.class)
    public void raidBattleCardAttackMsgRequest(RaidBattleCardAttackMsgRequest request, RaidBattleMessageContext<RaidBattleManager> ctx) {
        GameMessageHeader header = request.getHeader();
        long playerId = header.getPlayerId();
        ctx.getDataManager().getRbExecutorGroup().select(playerId).execute(()->{
            RaidBattleCardAttackMsgRequest.RequestBody param = request.getBodyObj();
            int charaPos = param.getCharaPos();
            String charaId = param.getCharaId();
            String cardId = param.getCardId();
            int targetPos = param.getTargetPos();
            List<Integer> selectCharaPos = param.getSelectCharaPos();
            long turn = param.getTurn();
            RaidBattleManager dataManager = ctx.getDataManager();
            String raidId = dataManager.getRaidBattle().getRaidId();
            //检查是否在Players里
            RaidBattle.Player actionPlayer = dataManager.getPlayerByPlayerId(playerId);
            if (actionPlayer.isRetreated()) {
                return;
            }
            if (dataManager.checkPlayerCharacterAllDead(actionPlayer)) {
                return;
            }
//        int cardId = request.getBodyObj().getCardId();
//        int chara = request.getBodyObj().getChara();
//        long turn = request.getBodyObj().getTurn();
            if (dataManager.isRaidBattleFinishOrFailed()) {
                //若战斗结束,则
                PushRaidBattleToSinglePlayerEventUser event = new PushRaidBattleToSinglePlayerEventUser(ctx, request);
                ctx.sendUserEvent(event, null, raidId);
                if (dataManager.isRaidBattleChannelActive()) {
                    dataManager.closeRaidBattleChannel();
                }
                return;
            }
            //攻击
            RaidBattle.Player.Character character = actionPlayer.getParty().get(charaId);
            if (character == null || character.getAlive() == 0) {
                throw GameNotifyException.newBuilder(GameErrorCode.RaidBattleAttackInvalidParam).build();
            }
            RaidBattleDamageDTO raidBattleDamageDTO = calcRaidBattleService.calcCardAttack(dataManager, actionPlayer, character, cardId, targetPos, selectCharaPos, turn);
            //RaidBattleDamageDTO raidBattleDamageDTO = new RaidBattleDamageDTO();

            //如果击败则立刻执行某些判断
            RaidBattleShouldBeFinishEvent raidBattleShouldBeFinishEvent = new RaidBattleShouldBeFinishEvent(this, dataManager);
            context.publishEvent(raidBattleShouldBeFinishEvent);
            PushRaidBattleDamageDTOEventUser pushRaidBattleDamageDTOEventUser = new PushRaidBattleDamageDTOEventUser(request, raidBattleDamageDTO);
            ctx.sendUserEvent(pushRaidBattleDamageDTOEventUser, null, raidId);
            //广播消息 - 除了自己
            //TODO:占用极多时间 尝试优化
            /*PushRaidBattleEvent pushRaidBattleEvent = new PushRaidBattleEvent(this, dataManager, request);
            context.publishEvent(pushRaidBattleEvent);*/
            PushRaidBattleEvent pushRaidBattleEvent = new PushRaidBattleEvent(this, dataManager, request);
            dataManager.setEvent(pushRaidBattleEvent);
        });


    }
}
