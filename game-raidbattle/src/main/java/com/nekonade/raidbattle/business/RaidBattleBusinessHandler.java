package com.nekonade.raidbattle.business;

import com.nekonade.common.dto.PlayerDTO;
import com.nekonade.common.error.GameNotifyException;
import com.nekonade.common.error.code.GameErrorCode;
import com.nekonade.dao.daos.CardsDbDao;
import com.nekonade.dao.db.entity.RaidBattle;
import com.nekonade.network.param.game.common.IGameMessage;
import com.nekonade.network.param.game.message.battle.JoinRaidBattleMsgRequest;
import com.nekonade.network.param.game.message.battle.JoinRaidBattleMsgResponse;
import com.nekonade.network.param.game.message.battle.RaidBattleCardAttackMsgRequest;
import com.nekonade.network.param.game.message.battle.rpc.JoinRaidBattleRPCRequest;
import com.nekonade.network.param.game.message.battle.rpc.JoinRaidBattleRPCResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;
import com.nekonade.raidbattle.event.function.PushRaidBattleEvent;
import com.nekonade.raidbattle.event.function.PushRaidBattleToSinglePlayerEvent;
import com.nekonade.raidbattle.event.function.RaidBattleShouldBeFinishEvent;
import com.nekonade.raidbattle.manager.RaidBattleManager;
import com.nekonade.raidbattle.message.context.RaidBattleEvent;
import com.nekonade.raidbattle.message.context.RaidBattleEventContext;
import com.nekonade.raidbattle.message.context.RaidBattleMessageContext;
import com.nekonade.raidbattle.service.CalcRaidBattleService;
import com.nekonade.raidbattle.service.GameErrorService;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
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
        logger.debug("RB收到空闲事件：{} state : {}", event.getClass().getName(),state.name());
        switch (state){
            case READER_IDLE:
            case WRITER_IDLE:
            default:
                break;
            case ALL_IDLE:
//                检查该战斗的Enemy的状态
//                ctx.getCtx().close();
                RaidBattleShouldBeFinishEvent raidBattleShouldBeFinishEvent = new RaidBattleShouldBeFinishEvent(this,ctx.getDataManager(),true);
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
            try{
                if (future.isSuccess()) {
                    JoinRaidBattleRPCResponse rpcResponse = (JoinRaidBattleRPCResponse) future.get();
                    String raidId = rpcResponse.getHeader().getAttribute().getRaidId();
                    if(rpcResponse.getHeader().getErrorCode() == 0) {
                        // 如果错码为0，表示扣钻石成功，可以增加挑战次数
                        logger.info("由RB服务器处理加入RaidBattle {} 的请求",raidId);
                        RaidBattle raidBattle = ctx.getDataManager().getRaidBattle();
                        PlayerDTO playerDTO = rpcResponse.getBodyObj().getPlayer();
                        ctx.getDataManager().addPlayer(playerDTO);
                        JoinRaidBattleMsgResponse response = new JoinRaidBattleMsgResponse();
                        BeanUtils.copyProperties(raidBattle,response.getBodyObj());
                        ctx.sendMessage(response);
                    }
                } else {
                    logger.error("加入战斗失败",future.cause());
                    throw future.cause();
                }
            }catch (Throwable e){
                gameErrorService.returnGameErrorResponse(e,ctx);
            }

        });
        ctx.sendRPCMessage(joinRaidBattleMsgRequest, promise);
    }


    @GameMessageMapping(RaidBattleCardAttackMsgRequest.class)
    public void raidBattleCardAttackMsgRequest(RaidBattleCardAttackMsgRequest request, RaidBattleMessageContext<RaidBattleManager> ctx){
        long playerId = request.getHeader().getPlayerId();
        RaidBattleCardAttackMsgRequest.RequestBody param = request.getBodyObj();
        String charaId = param.getCharaId();
        String cardId = param.getCardId();
        int targetPos = param.getTargetPos();
        List<Integer> selectCharaPos = param.getSelectCharaPos();
        long turn = param.getTurn();
        RaidBattleManager dataManager = ctx.getDataManager();
        //检查是否在Players里
        RaidBattle.Player actionPlayer = dataManager.getPlayerByPlayerId(playerId);
        if(actionPlayer.isRetreated()){
            return;
        }
        if(dataManager.checkPlayerCharacterAllDead(actionPlayer)){
            return;
        }
//        int cardId = request.getBodyObj().getCardId();
//        int chara = request.getBodyObj().getChara();
//        long turn = request.getBodyObj().getTurn();
        //检查是否活着
        if(dataManager.isRaidBattleFinishOrFailed()){
            PushRaidBattleToSinglePlayerEvent pushRaidBattleToSinglePlayerEvent = new PushRaidBattleToSinglePlayerEvent(this,ctx,request);
            context.publishEvent(pushRaidBattleToSinglePlayerEvent);
            dataManager.closeRaidBattleChannel();
            return;
        }
        //攻击
        RaidBattle.Player.Character character = actionPlayer.getParty().get(charaId);
        if(character == null){
            throw GameNotifyException.newBuilder(GameErrorCode.RaidBattleAttackInvalidParam).build();
        }
        //TODO:实现卡组功能

        List<Object> cardsDeck = character.getCardsDeck();
        calcRaidBattleService.calcCardAttack(dataManager,actionPlayer,cardId,targetPos,selectCharaPos,turn);

        //如果击败则立刻执行某些判断
        RaidBattleShouldBeFinishEvent raidBattleShouldBeFinishEvent = new RaidBattleShouldBeFinishEvent(this, dataManager);
        context.publishEvent(raidBattleShouldBeFinishEvent);
        //广播消息
        PushRaidBattleEvent pushRaidBattleEvent = new PushRaidBattleEvent(this,dataManager);
        context.publishEvent(pushRaidBattleEvent);
    }
}
