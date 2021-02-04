package com.nekonade.neko.logic;

import com.nekonade.dao.db.entity.Player;
import com.nekonade.neko.service.GameErrorService;
import com.nekonade.network.message.event.basic.*;
import com.nekonade.network.message.manager.ArenaManager;
import com.nekonade.network.message.manager.PlayerManager;
import com.nekonade.dao.redis.EnumRedisKey;
import com.nekonade.network.message.event.function.EnterGameEvent;
import com.nekonade.neko.service.StaminaService;
import com.nekonade.network.message.context.GatewayMessageContext;
import com.nekonade.network.param.game.common.IGameMessage;
import com.nekonade.network.param.game.message.neko.*;
import com.nekonade.network.param.game.message.neko.rpc.ConsumeDiamondMsgRequest;
import com.nekonade.network.param.game.message.neko.rpc.ConsumeDiamondMsgResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;
import com.nekonade.network.param.game.rpc.ConsumeDiamondRPCRequest;
import com.nekonade.network.param.game.rpc.ConsumeDiamondRPCResponse;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;
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


    @GameMessageMapping(ConnectionInactive.class)
    public void connectionInactive(ConnectionInactive request, GatewayMessageContext<PlayerManager> ctx){
        //由于是广播形式的链接状态改变，所以gamechannel防止多个NekoServer里有gamechannel重复
        ctx.getPlayerManager().getGameChannel().unsafeClose();
        //随后会在别的消息里自动重建
    }

    @GameMessageMapping(EnterGameMsgRequest.class)
    public void enterGame(EnterGameMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        logger.info("接收到客户端进入游戏请求：{}", request.getHeader().getPlayerId());
        EnterGameMsgResponse response = new EnterGameMsgResponse();
        PlayerManager playerManager = ctx.getDataManager();
        Player player = playerManager.getPlayer();
        response.getBodyObj().setNickname(player.getNickName());
        response.getBodyObj().setPlayerId(player.getPlayerId());
        long playerId = ctx.getPlayerId();
        String key = EnumRedisKey.PLAYERID_TO_PLAYER_NICKNAME.getKey(String.valueOf(playerId));
        redisTemplate.opsForValue().set(key,player.getNickName(),EnumRedisKey.PLAYERID_TO_PLAYER_NICKNAME.getTimeout());
        EnterGameEvent enterGameEvent = new EnterGameEvent(this,playerManager);
        context.publishEvent(enterGameEvent);
        ctx.getPlayerManager().getExperienceManager().addExperience(1000);
        ctx.sendMessage(response);
    }

    //查询自身消息
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
                gameErrorService.returnGameErrorResponse(cause,ctx);
                logger.error("playerId {} 自身数据查询失败", playerId, cause);
            }
        });
    }


    //通过ID查询指定角色的简单信息
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
                gameErrorService.returnGameErrorResponse(cause,ctx);
                logger.error("playerId {} 数据查询失败", playerId, cause);
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
                gameErrorService.returnGameErrorResponse(cause,ctx);
                logger.error("playerId {} 仓库数据查询失败", playerId, cause);
            }
        });
    }

    @GameMessageMapping(GetStaminaMsgRequest.class)
    public void getStaminaMsgRequest(GetStaminaMsgRequest request,GatewayMessageContext<PlayerManager> ctx){
        long playerId = ctx.getPlayer().getPlayerId();
        GetStaminaEventUser event = new GetStaminaEventUser();
        DefaultPromise<Object> promise = ctx.newPromise();
        ctx.sendUserEvent(event,promise,playerId).addListener(future -> {
           if(future.isSuccess()){
               GetStaminaMsgResponse response = (GetStaminaMsgResponse) future.get();
               ctx.sendMessage(response);
           } else {
               Throwable cause = future.cause();
               gameErrorService.returnGameErrorResponse(cause,ctx);
               logger.error("playerId {} 疲劳数据查询失败", playerId, cause);
           }
        });
    }

    @GameMessageMapping(GetMailBoxMsgRequest.class)
    public void getMailBoxMsgRequest(GetMailBoxMsgRequest request,GatewayMessageContext<PlayerManager> ctx){
        long playerId = ctx.getPlayer().getPlayerId();
        GetMailBoxEventUser event = new GetMailBoxEventUser();
        DefaultPromise<Object> promise = ctx.newPromise();
        ctx.sendUserEvent(event,promise,playerId).addListener(future -> {
            if(future.isSuccess()){
                GetMailBoxMsgResponse response = (GetMailBoxMsgResponse) future.get();
                ctx.sendMessage(response);
            } else {
                Throwable cause = future.cause();
                gameErrorService.returnGameErrorResponse(cause,ctx);
                logger.error("playerId {} 邮箱数据查询失败", playerId, cause);
            }
        });
    }

    @GameMessageMapping(CreateBattleMsgRequest.class)
    public void createBattleMsgRequest(CreateBattleMsgRequest request,GatewayMessageContext<PlayerManager> ctx){
        long playerId = ctx.getPlayer().getPlayerId();
        CreateBattleEventUser event = new CreateBattleEventUser(playerId,request);
        DefaultPromise<Object> promise = ctx.newPromise();
        ctx.sendUserEvent(event,promise,playerId).addListener(future -> {
            if(future.isSuccess()){
                CreateBattleMsgResponse response = (CreateBattleMsgResponse) future.get();
                ctx.sendMessage(response);
            } else {
                Throwable cause = future.cause();
                gameErrorService.returnGameErrorResponse(cause,ctx);
                logger.error("playerId {} 建立战斗失败", playerId, cause);
            }
        });
    }


//    @GameMessageMapping(BuyArenaChallengeTimesMsgRequest.class) // 接收客户端购买竞技场挑战次数的请求
//    public void buyArenaChallengeTimes(BuyArenaChallengeTimesMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
//        ConsumeDiamondMsgRequest consumeDiamondMsgRequest = new ConsumeDiamondMsgRequest();
//        Promise<IGameMessage> promise = ctx.newPromise();
//        promise.addListener((GenericFutureListener<Future<IGameMessage>>) future -> {
//            if (future.isSuccess()) {
//                ConsumeDiamondMsgResponse rpcResponse = (ConsumeDiamondMsgResponse) future.get();
//                if(rpcResponse.getHeader().getErrorCode() == 0) {
//                    // 如果错码为0，表示扣钻石成功，可以增加挑战次数
//                }
//            } else {
//                logger.error("竞技场扣除钻石失败",future.cause());
//                //向客户端返回错误码
//            }
//        });
//        ctx.sendRPCMessage(consumeDiamondMsgRequest, promise);
//    }
//
//
//    @GameMessageMapping(BuyArenaChallengeTimesMsgRequest.class)
//    public void buyChallengeTimes(BuyArenaChallengeTimesMsgRequest request, GatewayMessageContext<ArenaManager> ctx) {
//        // 先通过rpc扣除钻石，扣除成功之后，再添加挑战次数
//        BuyArenaChallengeTimesMsgResponse response = new BuyArenaChallengeTimesMsgResponse();
//        Promise<IGameMessage> rpcPromise = ctx.newRPCPromise();
//        rpcPromise.addListener(new GenericFutureListener<Future<IGameMessage>>() {
//            @Override
//            public void operationComplete(Future<IGameMessage> future) throws Exception {
//                if (future.isSuccess()) {
//                    ConsumeDiamondRPCResponse rpcResponse = (ConsumeDiamondRPCResponse) future.get();
//                    int errorCode = rpcResponse.getHeader().getErrorCode();
//                    if (errorCode == 0) {
//                        ctx.getDataManager().addChallengeTimes(10);// 假设添加10次竞技场挑战次
//                        logger.debug("购买竞技挑战次数成功");
//                    } else {
//                        response.getHeader().setErrorCode(errorCode);
//                    }
//                } else {
//                    response.getHeader().setErrorCode(-1);
//                }
//                ctx.sendMessage(response);
//            }
//        });
//        ConsumeDiamondRPCRequest rpcRequest = new ConsumeDiamondRPCRequest();
//        rpcRequest.getBodyObj().setConsumeCount(20);// 假设是20钻石
//        ctx.sendRPCMessage(rpcRequest, rpcPromise);
//    }


    private List<Long> getArenaPlayerIdList() {
        return Arrays.asList(50000001L,50000002L,50000003L,50000006L,50000007L);// 模拟竞技场列表playerId
    }

    @GameMessageMapping(GetArenaPlayerListMsgRequest.class)
    public void getArenaPlayerList(GetArenaPlayerListMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        List<Long> playerIds = this.getArenaPlayerIdList();// 获取本次要显示的PlayerId
        List<GetArenaPlayerListMsgResponse.ArenaPlayer> arenaPlayers = new ArrayList<>(playerIds.size());
        AtomicReference<Integer> count = new AtomicReference<>(playerIds.size());
        playerIds.forEach(playerId -> {// 遍历所有的PlayerId，向他们对应的GameChannel发送查询事件
            GetArenaPlayerEventUser getArenaPlayerEvent = new GetArenaPlayerEventUser(playerId);
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
                    List<GetArenaPlayerListMsgResponse.ArenaPlayer> result = arenaPlayers.stream().filter(Objects::nonNull).collect(Collectors.toList());
                    GetArenaPlayerListMsgResponse response = new GetArenaPlayerListMsgResponse();
                    response.getBodyObj().setArenaPlayers(result);
                    ctx.sendMessage(response);
                }
            });
        });
    }


    
    
    
}
