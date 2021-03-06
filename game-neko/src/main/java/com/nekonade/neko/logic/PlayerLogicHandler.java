package com.nekonade.neko.logic;

import com.nekonade.common.constcollections.EnumCollections;
import com.nekonade.common.error.exceptions.GameErrorException;
import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.daos.db.CharacterDBDao;
import com.nekonade.dao.daos.db.GachaPoolDBDao;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.repository.GachaPoolDBRepository;
import com.nekonade.neko.service.GameErrorService;
import com.nekonade.neko.service.StaminaService;
import com.nekonade.network.message.context.GatewayMessageContext;
import com.nekonade.network.message.event.function.EnterGameEvent;
import com.nekonade.network.message.event.function.StagePassEvent;
import com.nekonade.network.message.event.user.*;
import com.nekonade.network.message.manager.PlayerManager;
import com.nekonade.network.param.game.message.neko.*;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@GameMessageHandler
public class PlayerLogicHandler {

    private static final Logger logger = LoggerFactory.getLogger(PlayerLogicHandler.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private StaminaService staminaService;

    @Autowired
    private GameErrorService gameErrorService;

    @Autowired
    private GachaPoolDBDao gachaPoolsDbDao;

    @Autowired
    private GachaPoolDBRepository gachaPoolsDbRepository;

    @Autowired
    private CharacterDBDao charactersDbDao;

    private Boolean isOperationCoolDowning(String coolDownKey){
        return redisTemplate.hasKey(coolDownKey);
    }

    private void setOperationCoolDown(long playerId,String coolDownKey, Duration duration){
        redisTemplate.opsForValue().set(coolDownKey,String.valueOf(playerId),duration);
    }

    private void delOperationCoolDown(String coolDownKey){
        redisTemplate.opsForValue().getOperations().delete(coolDownKey);
    }


    @GameMessageMapping(TriggerConnectionInactive.class)
    public void connectionInactive(TriggerConnectionInactive request, GatewayMessageContext<PlayerManager> ctx) {
        //???????????????????????????????????????????????????gamechannel????????????NekoServer??????gamechannel??????
        ctx.getPlayerManager().getGameChannel().unsafeClose();
        //???????????????????????????????????????
    }

    @GameMessageMapping(DoEnterGameMsgRequest.class)
    public void enterGame(DoEnterGameMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        logger.info("???????????????????????????????????????{}", request.getHeader().getPlayerId());
        DoEnterGameMsgResponse response = new DoEnterGameMsgResponse();
        PlayerManager playerManager = ctx.getDataManager();
        Player player = playerManager.getPlayer();
        response.getBodyObj().setNickname(player.getNickName());
        response.getBodyObj().setPlayerId(player.getPlayerId());
        long playerId = ctx.getPlayerId();
        String key = EnumRedisKey.PLAYERID_TO_NICKNAME.getKey(String.valueOf(playerId));
        redisTemplate.opsForValue().set(key, player.getNickName(), EnumRedisKey.PLAYERID_TO_NICKNAME.getTimeout());
        EnterGameEvent enterGameEvent = new EnterGameEvent(this, playerManager,request.getHeader());
        context.publishEvent(enterGameEvent);
        ctx.sendMessage(response);
        //MessageUtils.CalcMessageDealTimeNow(logger,request);
    }

    //??????????????????
    @GameMessageMapping(GetPlayerSelfMsgRequest.class)
    public void getPlayerSelf(GetPlayerSelfMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        long playerId = ctx.getPlayer().getPlayerId();
        DefaultPromise<Object> promise = ctx.newPromise();
        GetSelfInfoEventUser event = new GetSelfInfoEventUser();
        ctx.sendUserEvent(event, promise, playerId).addListener(future -> {
            if (future.isSuccess()) {
                GetPlayerSelfMsgResponse response = (GetPlayerSelfMsgResponse) future.get();
                ctx.sendMessage(response);
            } else {
                Throwable cause = future.cause();
                gameErrorService.returnGameErrorResponse(cause, ctx);
                logger.error("playerId {} ????????????????????????", playerId, cause);
            }
        });
    }





    //??????ID?????????????????????????????????
    @GameMessageMapping(GetPlayerByIdMsgRequest.class)
    public void getPlayerById(GetPlayerByIdMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        long playerId = request.getBodyObj().getPlayerId();
        //long playerId = ctx.getPlayer().getPlayerId();
        DefaultPromise<Object> promise = ctx.newPromise();
        GetPlayerInfoEventUser event = new GetPlayerInfoEventUser(playerId);
        ctx.sendUserEvent(event, promise, playerId).addListener(future -> {
            if (future.isSuccess()) {
                GetPlayerByIdMsgResponse response = (GetPlayerByIdMsgResponse) future.get();
                ctx.sendMessage(response);
            } else {
                Throwable cause = future.cause();
                gameErrorService.returnGameErrorResponse(cause, ctx);
                logger.error("playerId {} ??????????????????", playerId, cause);
            }
        });
    }

    @GameMessageMapping(GetInventoryMsgRequest.class)
    public void getInventoryMsgRequest(GetInventoryMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        long playerId = ctx.getPlayer().getPlayerId();
        DefaultPromise<Object> promise = ctx.newPromise();
        GetInventoryEventUser event = new GetInventoryEventUser();
        ctx.sendUserEvent(event, promise, playerId).addListener(future -> {
            if (future.isSuccess()) {
                GetInventoryMsgResponse response = (GetInventoryMsgResponse) future.get();
                ctx.sendMessage(response);
            } else {
                Throwable cause = future.cause();
                gameErrorService.returnGameErrorResponse(cause, ctx);
                logger.error("playerId {} ????????????????????????", playerId, cause);
            }
        });
    }

    @GameMessageMapping(GetStaminaMsgRequest.class)
    public void getStaminaMsgRequest(GetStaminaMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        long playerId = ctx.getPlayer().getPlayerId();
        GetStaminaEventUser event = new GetStaminaEventUser();
        DefaultPromise<Object> promise = ctx.newPromise();
        ctx.sendUserEvent(event, promise, playerId).addListener(future -> {
            if (future.isSuccess()) {
                GetStaminaMsgResponse response = (GetStaminaMsgResponse) future.get();
                ctx.sendMessage(response);
            } else {
                Throwable cause = future.cause();
                gameErrorService.returnGameErrorResponse(cause, ctx);
                logger.error("playerId {} ????????????????????????", playerId, cause);
            }
        });
    }

    @GameMessageMapping(GetMailBoxMsgRequest.class)
    public void getMailBoxMsgRequest(GetMailBoxMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        long playerId = ctx.getPlayer().getPlayerId();
        GetMailBoxEventUser event = new GetMailBoxEventUser();
        DefaultPromise<Object> promise = ctx.newPromise();
        ctx.sendUserEvent(event, promise, playerId).addListener(future -> {
            if (future.isSuccess()) {
                GetMailBoxMsgResponse response = (GetMailBoxMsgResponse) future.get();
                ctx.sendMessage(response);
            } else {
                Throwable cause = future.cause();
                gameErrorService.returnGameErrorResponse(cause, ctx);
                logger.error("playerId {} ????????????????????????", playerId, cause);
            }
        });
    }

    //?????????????????????????????????
    @GameMessageMapping(GetPlayerCharacterListMsgRequest.class)
    public void getPlayerCharacterListMsgRequest(GetPlayerCharacterListMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        long playerId = ctx.getPlayer().getPlayerId();
        DefaultPromise<Object> promise = ctx.newPromise();
        GetPlayerCharacterListEventUser event = new GetPlayerCharacterListEventUser();
        ctx.sendUserEvent(event, promise, playerId).addListener(future -> {
            if (future.isSuccess()) {
                GetPlayerCharacterListMsgResponse response = (GetPlayerCharacterListMsgResponse) future.get();
                ctx.sendMessage(response);
            } else {
                Throwable cause = future.cause();
                gameErrorService.returnGameErrorResponse(cause, ctx);
                logger.error("playerId {} ??????????????????????????????", playerId, cause);
            }
        });
    }

    @GameMessageMapping(DoCreateBattleMsgRequest.class)
    public void createBattleMsgRequest(DoCreateBattleMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        long playerId = ctx.getPlayer().getPlayerId();
        DoCreateBattleEventUser event = new DoCreateBattleEventUser(playerId, request);
        DefaultPromise<Object> promise = ctx.newPromise();
        ctx.sendUserEvent(event, promise, playerId).addListener(future -> {
            if (future.isSuccess()) {
                DoCreateBattleMsgResponse response = (DoCreateBattleMsgResponse) future.get();
                ctx.sendMessage(response);
            } else {
                Throwable cause = future.cause();
                gameErrorService.returnGameErrorResponse(cause, ctx);
                logger.error("playerId {} ??????????????????", playerId, cause);
            }
        });
    }

    private List<Long> getArenaPlayerIdList() {
        return Arrays.asList(50000001L, 50000002L, 50000003L, 50000006L, 50000007L);// ?????????????????????playerId
    }

    @GameMessageMapping(GetArenaPlayerListMsgRequest.class)
    public void getArenaPlayerList(GetArenaPlayerListMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        List<Long> playerIds = this.getArenaPlayerIdList();// ????????????????????????PlayerId
        List<GetArenaPlayerListMsgResponse.ArenaPlayer> arenaPlayers = new ArrayList<>(playerIds.size());
        AtomicReference<Integer> count = new AtomicReference<>(playerIds.size());
        playerIds.forEach(playerId -> {// ???????????????PlayerId?????????????????????GameChannel??????????????????
            GetArenaPlayerEventUser getArenaPlayerEvent = new GetArenaPlayerEventUser(playerId);
            Promise<Object> promise = ctx.newPromise();// ???????????????promise????????????for?????????????????????Promise?????????setSuccess?????????
            ctx.sendUserEvent(getArenaPlayerEvent, promise, playerId).addListener(future -> {
                if (future.isSuccess()) {// ??????????????????????????????????????????
                    GetArenaPlayerListMsgResponse.ArenaPlayer arenaPlayer = (GetArenaPlayerListMsgResponse.ArenaPlayer) future.get();
                    arenaPlayers.add(arenaPlayer);
                } else {
                    arenaPlayers.add(null);
                }
                count.getAndSet(count.get() - 1);
                if (count.get().equals(0)) {
                    List<GetArenaPlayerListMsgResponse.ArenaPlayer> result = arenaPlayers.stream().filter(Objects::nonNull).collect(Collectors.toList());
                    GetArenaPlayerListMsgResponse response = new GetArenaPlayerListMsgResponse();
                    response.getBodyObj().setArenaPlayers(result);
                    ctx.sendMessage(response);
                }
            });
        });
    }

    @GameMessageMapping(GetRaidBattleListMsgRequest.class)
    public void getRaidBattleListMsgRequest(GetRaidBattleListMsgRequest request, GatewayMessageContext<PlayerManager> ctx){
        PlayerManager dataManager = ctx.getDataManager();
        long playerId = dataManager.getPlayer().getPlayerId();
        GetRaidBattleListEventUser event = new GetRaidBattleListEventUser();
        BeanUtils.copyProperties(request.getBodyObj(),event);
        DefaultPromise<Object> promise = ctx.newPromise();
        ctx.sendUserEvent(event, promise, playerId).addListener(future -> {
            if (future.isSuccess()) {
                GetRaidBattleListMsgResponse response = (GetRaidBattleListMsgResponse) future.get();
                ctx.sendMessage(response);
            } else {
                Throwable cause = future.cause();
                gameErrorService.returnGameErrorResponse(cause, ctx);
                logger.error("playerId {} ????????????????????????", playerId, cause);
            }
        });
    }

    @GameMessageMapping(GetRaidBattleRewardListMsgRequest.class)
    public void getRaidBattleRewardListMsgRequest(GetRaidBattleRewardListMsgRequest request, GatewayMessageContext<PlayerManager> ctx){
        PlayerManager dataManager = ctx.getDataManager();
        long playerId = dataManager.getPlayer().getPlayerId();
        GetRaidBattleRewardListEventUser event = new GetRaidBattleRewardListEventUser();
        BeanUtils.copyProperties(request.getBodyObj(),event);
        DefaultPromise<Object> promise = ctx.newPromise();
        ctx.sendUserEvent(event, promise, playerId).addListener(future -> {
            if (future.isSuccess()) {
                GetRaidBattleRewardListMsgResponse response = (GetRaidBattleRewardListMsgResponse) future.get();
                ctx.sendMessage(response);
            } else {
                Throwable cause = future.cause();
                gameErrorService.returnGameErrorResponse(cause, ctx);
                logger.error("playerId {} ??????????????????????????????", playerId, cause);
            }
        });
    }

    @GameMessageMapping(DoReceiveMailMsgRequest.class)
    public void receiveMailMsgRequest(DoReceiveMailMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        long playerId = ctx.getPlayer().getPlayerId();
        DoReceiveMailEventUser event = new DoReceiveMailEventUser(request.getBodyObj());
        DefaultPromise<Object> promise = ctx.newPromise();
        String coolDownKey = EnumRedisKey.COOLDOWN_DO_CLAIM_RAIDBATTLE_REWARD.getKey(String.valueOf(playerId));
        if(isOperationCoolDowning(coolDownKey)){
            GameErrorException build = GameErrorException.newBuilder(EnumCollections.CodeMapper.GameErrorCode.CoolDownDoClaimRaidBattleReward).build();
            gameErrorService.returnGameErrorResponse(build, ctx);
            return;
        }
        setOperationCoolDown(playerId,coolDownKey,EnumRedisKey.COOLDOWN_DO_CLAIM_RAIDBATTLE_REWARD.getTimeout());

        ctx.sendUserEvent(event, promise, playerId).addListener(future -> {
            if (future.isSuccess()) {
                DoReceiveMailMsgResponse response = (DoReceiveMailMsgResponse) future.get();
                ctx.sendMessage(response);
            } else {
                Throwable cause = future.cause();
                gameErrorService.returnGameErrorResponse(cause, ctx);
                logger.error("playerId {} ??????????????????", playerId, cause);
            }
            delOperationCoolDown(coolDownKey);
        });
    }

    @GameMessageMapping(DoClaimRaidBattleRewardMsgRequest.class)
    public void claimRaidBattleRewardMsgRequest(DoClaimRaidBattleRewardMsgRequest request,GatewayMessageContext<PlayerManager> ctx){
        long playerId = ctx.getPlayer().getPlayerId();
        String coolDownKey = EnumRedisKey.COOLDOWN_DO_RECEIVE_MAILBOX_REWARD.getKey(String.valueOf(playerId));
        if(isOperationCoolDowning(coolDownKey)){
            GameErrorException build = GameErrorException.newBuilder(EnumCollections.CodeMapper.GameErrorCode.CoolDownDoReceiveMailBox).build();
            gameErrorService.returnGameErrorResponse(build, ctx);
            return;
        }
        setOperationCoolDown(playerId,coolDownKey,EnumRedisKey.COOLDOWN_DO_RECEIVE_MAILBOX_REWARD.getTimeout());

        String raidId = request.getBodyObj().getRaidId();
        DoClaimRaidBattleRewardEventUser event = new DoClaimRaidBattleRewardEventUser(raidId);
        DefaultPromise<Object> promise = ctx.newPromise();
        ctx.sendUserEvent(event, promise, playerId).addListener(future -> {
            if (future.isSuccess()) {
                DoClaimRaidBattleRewardMsgResponse response = (DoClaimRaidBattleRewardMsgResponse) future.get();
                ctx.sendMessage(response);
                //????????????
                StagePassEvent passBlockPointEvent = new StagePassEvent(this, response.getBodyObj().getStageId(), ctx.getPlayerManager());
                context.publishEvent(passBlockPointEvent);
            } else {
                Throwable cause = future.cause();
                gameErrorService.returnGameErrorResponse(cause, ctx);
                logger.error("playerId {} ??????RaidBattle{}????????????", playerId, raidId, cause);
            }
            delOperationCoolDown(coolDownKey);
        });
    }

    @GameMessageMapping(DoDiamondGachaMsgRequest.class)
    public void diamondGachaMsgRequest(DoDiamondGachaMsgRequest request,GatewayMessageContext<PlayerManager> ctx){
        long playerId = ctx.getPlayer().getPlayerId();
        String gachaPoolsId = request.getBodyObj().getGachaPoolsId();
        PlayerManager playerManager = ctx.getPlayerManager();
        int type = request.getBodyObj().getType();
        DoDiamondGachaEventUser event = new DoDiamondGachaEventUser(playerManager,gachaPoolsId,type);
        DefaultPromise<Object> promise = ctx.newPromise();
        ctx.sendUserEvent(event, promise, playerId).addListener(future -> {
            if(future.isSuccess()){
                try{
                    DoDiamondGachaMsgResponse response = (DoDiamondGachaMsgResponse) future.get();
                    response.wrapResponse(request);
                    ctx.sendMessage(response);
                    //MessageUtils.CalcMessageDealTimeNow(logger,request);
                }catch (Throwable e){
                    gameErrorService.returnGameErrorResponse(e, ctx);
                }
            }else{
                Throwable e = future.cause();
                gameErrorService.returnGameErrorResponse(e, ctx);
            }
        });


    }
}
