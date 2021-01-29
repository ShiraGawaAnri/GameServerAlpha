package com.nekonade.neko.logic;

import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.entity.manager.PlayerManager;
import com.nekonade.dao.redis.EnumRedisKey;
import com.nekonade.neko.logic.event.*;
import com.nekonade.neko.service.StaminaService;
import com.nekonade.network.message.context.GatewayMessageContext;
import com.nekonade.network.param.game.common.IGameMessage;
import com.nekonade.network.param.game.message.neko.*;
import com.nekonade.network.param.game.message.neko.rpc.ConsumeDiamondMsgRequest;
import com.nekonade.network.param.game.message.neko.rpc.ConsumeDiamondMsgResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@GameMessageHandler
public class PlayerLogicHandler {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private Logger logger = LoggerFactory.getLogger(PlayerLogicHandler.class);

    @Autowired
    private StaminaService staminaService;

    @GameMessageMapping(EnterGameMsgRequest.class)
    public void enterGame(EnterGameMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        logger.info("接收到客户端进入游戏请求：{}", request.getHeader().getPlayerId());
        EnterGameMsgResponse response = new EnterGameMsgResponse();
        Player player = ctx.getPlayer();
        response.getBodyObj().setNickname(player.getNickName());
        response.getBodyObj().setPlayerId(player.getPlayerId());
        long playerId = ctx.getPlayerId();
        String key = EnumRedisKey.PLAYERID_TO_PLAYER_NICKNAME.getKey(String.valueOf(playerId));
        redisTemplate.opsForValue().set(key,player.getNickName(),EnumRedisKey.PLAYERID_TO_PLAYER_NICKNAME.getTimeout());
        ctx.sendMessage(response);
    }

    //通过ID查询指定角色的简单信息
    @GameMessageMapping(GetPlayerSelfMsgRequest.class)
    public void getPlayerSelf(GetPlayerSelfMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        long playerId = ctx.getPlayer().getPlayerId();
        DefaultPromise<Object> promise = ctx.newPromise();
        GetSelfInfoEvent event = new GetSelfInfoEvent();
        ctx.sendUserEvent(event, promise, playerId).addListener(future -> {
            if (future.isSuccess()) {
                GetPlayerSelfMsgResponse response = (GetPlayerSelfMsgResponse) future.get();
                ctx.sendMessage(response);
            } else {
                logger.error("playerId {} 自身数据查询失败", playerId, future.cause());
            }
        });
    }


    //通过ID查询指定角色的简单信息
    @GameMessageMapping(GetPlayerByIdMsgRequest.class)
    public void getPlayerById(GetPlayerByIdMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        long playerId = request.getBodyObj().getPlayerId();
        //long playerId = ctx.getPlayer().getPlayerId();
        DefaultPromise<Object> promise = ctx.newPromise();
        GetPlayerInfoEvent event = new GetPlayerInfoEvent(playerId);
        ctx.sendUserEvent(event, promise, playerId).addListener(future -> {
            if (future.isSuccess()) {
                GetPlayerByIdMsgResponse response = (GetPlayerByIdMsgResponse) future.get();
                ctx.sendMessage(response);
            } else {
                logger.error("playerId {} 数据查询失败", playerId, future.cause());
            }
        });
    }

    @GameMessageMapping(GetInventoryMsgRequest.class)
    public void getInventoryMsgRequest(GetInventoryMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        long playerId = ctx.getPlayer().getPlayerId();
        DefaultPromise<Object> promise = ctx.newPromise();
        GetInventoryEvent event = new GetInventoryEvent();
        ctx.sendUserEvent(event, promise, playerId).addListener(future -> {
            if (future.isSuccess()) {
                GetInventoryMsgResponse response = (GetInventoryMsgResponse) future.get();
                ctx.sendMessage(response);
            } else {
                logger.error("playerId {} 仓库数据查询失败", playerId, future.cause());
            }
        });
    }

    @GameMessageMapping(GetStaminaMsgRequest.class)
    public void getStaminaMsgRequest(GetStaminaMsgRequest request,GatewayMessageContext<PlayerManager> ctx){
        long playerId = ctx.getPlayer().getPlayerId();
        GetStaminaEvent event = new GetStaminaEvent();
        DefaultPromise<Object> promise = ctx.newPromise();
        ctx.sendUserEvent(event,promise,playerId).addListener(future -> {
           if(future.isSuccess()){
               GetStaminaMsgResponse response = (GetStaminaMsgResponse) future.get();
               ctx.sendMessage(response);
           } else {
               logger.error("playerId {} 疲劳数据查询失败", playerId, future.cause());
           }
        });
    }


    @GameMessageMapping(BuyArenaChallengeTimesMsgRequest.class) // 接收客户端购买竞技场挑战次数的请求
    public void buyArenaChallengeTimes(BuyArenaChallengeTimesMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        ConsumeDiamondMsgRequest consumeDiamondMsgRequest = new ConsumeDiamondMsgRequest();
        Promise<IGameMessage> promise = ctx.newPromise();
        promise.addListener((GenericFutureListener<Future<IGameMessage>>) future -> {
            if (future.isSuccess()) {
                ConsumeDiamondMsgResponse rpcResponse = (ConsumeDiamondMsgResponse) future.get();
                if(rpcResponse.getHeader().getErrorCode() == 0) {
                    // 如果错码为0，表示扣钻石成功，可以增加挑战次数
                }
            } else {
                logger.error("竞技场扣除钻石失败",future.cause());
                //向客户端返回错误码
            }
        });
        ctx.sendRPCMessage(consumeDiamondMsgRequest, promise);
    }


    private List<Long> getArenaPlayerIdList() {
        return Arrays.asList(50000001L,50000002L,50000003L,50000006L,50000007L);// 模拟竞技场列表playerId
    }

    @GameMessageMapping(GetArenaPlayerListMsgRequest.class)
    public void getArenaPlayerList(GetArenaPlayerListMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        List<Long> playerIds = this.getArenaPlayerIdList();// 获取本次要显示的PlayerId
        List<GetArenaPlayerListMsgResponse.ArenaPlayer> arenaPlayers = new ArrayList<>(playerIds.size());
        AtomicReference<Integer> count = new AtomicReference<>(playerIds.size());
        playerIds.forEach(playerId -> {// 遍历所有的PlayerId，向他们对应的GameChannel发送查询事件
            GetArenaPlayerEvent getArenaPlayerEvent = new GetArenaPlayerEvent(playerId);
            Promise<Object> promise = ctx.newPromise();// 注意，这个promise不能放到for循环外面，一个Promise只能被setSuccess一次。
            ctx.sendUserEvent(getArenaPlayerEvent, promise, playerId).addListener(future -> {
                if (future.isSuccess()) {// 如果执行成功，获取执行的结果
                    GetArenaPlayerListMsgResponse.ArenaPlayer arenaPlayer = (GetArenaPlayerListMsgResponse.ArenaPlayer) future.get();
                    arenaPlayers.add(arenaPlayer);
                } else {
                    arenaPlayers.add(null);
                }
                count.getAndSet(count.get() - 1);
                if (count.get().equals(0)) {
                    List<GetArenaPlayerListMsgResponse.ArenaPlayer> result = arenaPlayers.stream().filter(c -> c != null).collect(Collectors.toList());
                    GetArenaPlayerListMsgResponse response = new GetArenaPlayerListMsgResponse();
                    response.getBodyObj().setArenaPlayers(result);
                    ctx.sendMessage(response);
                }
            });
        });
    }


    
    
    
}
