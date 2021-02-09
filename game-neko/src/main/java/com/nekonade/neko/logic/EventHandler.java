package com.nekonade.neko.logic;

import com.alibaba.fastjson.JSON;
import com.nekonade.common.cloud.RaidBattleServerInstance;
import com.nekonade.common.dto.Mail;
import com.nekonade.common.dto.RaidBattle;
import com.nekonade.common.error.GameErrorException;
import com.nekonade.common.error.GameNotification;
import com.nekonade.common.model.PageResult;
import com.nekonade.common.utils.CalcCoolDownUtils;
import com.nekonade.dao.daos.RaidBattleDbDao;
import com.nekonade.dao.db.entity.Inventory;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.entity.Stamina;
import com.nekonade.dao.helper.SortParam;
import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.neko.service.MailBoxService;
import com.nekonade.neko.service.RaidBattleService;
import com.nekonade.neko.service.StaminaService;
import com.nekonade.network.message.context.ServerConfig;
import com.nekonade.network.message.context.UserEvent;
import com.nekonade.network.message.context.UserEventContext;
import com.nekonade.network.message.event.basic.*;
import com.nekonade.network.message.event.function.StaminaSubPointEvent;
import com.nekonade.common.error.code.GameErrorCode;
import com.nekonade.network.message.manager.InventoryManager;
import com.nekonade.network.message.manager.PlayerManager;
import com.nekonade.network.param.game.message.neko.*;
import com.nekonade.network.param.game.message.neko.battle.JoinRaidBattleMsgRequest;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@GameMessageHandler
public class EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(PlayerLogicHandler.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private StaminaService staminaService;

    @Autowired
    private MailBoxService mailBoxService;

    @Autowired
    private RaidBattleDbDao raidBattleDbDao;

    @Autowired
    private RaidBattleService raidBattleService;

    @Autowired
    private RaidBattleServerInstance raidBattleServerInstance;

    @Autowired
    private ServerConfig serverConfig;

    @UserEvent(IdleStateEvent.class)
    public void idleStateEvent(UserEventContext<PlayerManager> ctx, IdleStateEvent event, Promise<Object> promise) {
        logger.debug("收到空闲事件：{}", event.getClass().getName());
        ctx.getCtx().close();
    }

    @UserEvent(LevelUpEvent.class)
    public void levelUpEvent(UserEventContext<PlayerManager> utx, LevelUpEvent event, Promise<Boolean> promise) {
        LevelUpMsgResponse response = new LevelUpMsgResponse();
        response.getBodyObj().setData(event);
        utx.getCtx().writeAndFlush(response);
//        promise.setSuccess(true);
    }

    @UserEvent(GetSelfInfoEventUser.class)
    public void GetSelfInfoEvent(UserEventContext<PlayerManager> utx, GetSelfInfoEventUser event, Promise<Object> promise) {
        GetPlayerSelfMsgResponse response = new GetPlayerSelfMsgResponse();
        utx.getDataManager().getStaminaManager().checkStamina();
        Player player = utx.getDataManager().getPlayer();
        GetPlayerSelfMsgResponse.ResponseBody body = response.getBodyObj();
        body.setCreateTime(player.getCreateTime());
        body.setLastLoginTime(player.getLastLoginTime());
        body.setLevel(player.getLevel());
        body.setNickname(player.getNickName());
        body.setPlayerId(player.getPlayerId());
        body.setZoneId(player.getZoneId());
        promise.setSuccess(response);
    }

    @UserEvent(GetPlayerInfoEventUser.class)
    public void getPlayerInfoEvent(UserEventContext<PlayerManager> utx, GetPlayerInfoEventUser event, Promise<Object> promise) {
        GetPlayerByIdMsgResponse response = new GetPlayerByIdMsgResponse();
        Player player = utx.getDataManager().getPlayer();
        response.getBodyObj().setPlayerId(player.getPlayerId());
        response.getBodyObj().setNickName(player.getNickName());
        Map<String, String> heros = new HashMap<>();
        player.getHeros().forEach((k, v) -> {// 复制处理一下，防止对象安全溢出。
            heros.put(k, v);
        });
        // response.getBodyObj().setHeros(this.player.getHeros());不要使用这种方式，它会把这个map传递到其它线程
        response.getBodyObj().setHeros(heros);
        promise.setSuccess(response);
    }

    @UserEvent(GetStaminaEventUser.class)
    public void getStaminaEvent(UserEventContext<PlayerManager> utx, GetStaminaEventUser event, Promise<Object> promise) {
        GetStaminaMsgResponse response = new GetStaminaMsgResponse();
        utx.getDataManager().getStaminaManager().checkStamina();
        Stamina stamina = utx.getDataManager().getStaminaManager().getStamina().clone();
        BeanUtils.copyProperties(stamina, response.getBodyObj());
        promise.setSuccess(response);
    }

    @UserEvent(GetInventoryEventUser.class)
    public void getInventoryEvent(UserEventContext<PlayerManager> utx, GetInventoryEventUser event, Promise<Object> promise) {
        GetInventoryMsgResponse response = new GetInventoryMsgResponse();
        InventoryManager inventoryManager = utx.getDataManager().getInventoryManager();
        Inventory inventory = inventoryManager.getInventory().clone();
        BeanUtils.copyProperties(inventory, response.getBodyObj());
        promise.setSuccess(response);
    }

    @UserEvent(GetMailBoxEventUser.class)
    public void getMailBoxEvent(UserEventContext<PlayerManager> utx, GetMailBoxEventUser event, Promise<Object> promise) {
        GetMailBoxMsgResponse response = new GetMailBoxMsgResponse();
        PlayerManager playerManager = utx.getDataManager();
        long playerId = playerManager.getPlayer().getPlayerId();
        SortParam sortParam = new SortParam();
        sortParam.setSortDirection(Sort.Direction.DESC);
        PageResult<Mail> result = mailBoxService.findByPage(playerId, null, 1, 10, sortParam);
        BeanUtils.copyProperties(result, response.getBodyObj());
        promise.setSuccess(response);
    }

    @UserEvent(CreateBattleEventUser.class)
    public void createBattle(UserEventContext<PlayerManager> utx, CreateBattleEventUser event, Promise<Object> promise) throws ExecutionException, InterruptedException {
        EventExecutor executor = new DefaultEventExecutor();
        PlayerManager playerManager = utx.getDataManager();
        Player player = playerManager.getPlayer();
        long playerId = event.getPlayerId();
        CreateBattleMsgResponse response = new CreateBattleMsgResponse();
        RaidBattle raidBattle = raidBattleService.findRaidBattleDb(playerId, event.getRequest());

        //不存在的关卡
        if (raidBattle == null) {
            promise.setFailure(GameErrorException.newBuilder(GameErrorCode.StageDbNotFound).build());
            return;
        }
        String stageId = raidBattle.getStageId();
        if(stageId == null){
            logger.error("未设定StageId,请必须设定 {}",raidBattle);
            promise.setFailure(GameErrorException.newBuilder(GameErrorCode.StageDbNotFound).build());
            return;
        }

        //同一个副本同一时间只能有一个
        String stageIdPlayerIdKey = EnumRedisKey.RAIDBATTLE_STAGEID_PLAYERID_TO_RAIDID.getKey(stageId,String.valueOf(playerId));
        String hasBeenCreatedKey = redisTemplate.opsForValue().get(stageIdPlayerIdKey);
        if(StringUtils.isNotEmpty(hasBeenCreatedKey)){
            String raidKey = EnumRedisKey.RAIDBATTLE_RAIDID_DETAILS.getKey(hasBeenCreatedKey);
            String battleJson = redisTemplate.opsForValue().get(raidKey);
            try{
                RaidBattle createdBattle = JSON.parseObject(battleJson, RaidBattle.class);
                if(createdBattle != null){
                    BeanUtils.copyProperties(createdBattle, response.getBodyObj());
                    promise.setSuccess(response);
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
                promise.setFailure(null);
            }
        }
        //未激活/已关闭的活动关卡
        if (!raidBattle.isActive()) {
            promise.setFailure(GameNotification.newBuilder(GameErrorCode.StageDbClosed).build());
            return;
        }
        //其他特殊条件========= 如不能使用某个角色,必须多少级,必须完成过某个关卡等等
        if (false) {
            promise.setFailure(null);
            return;
        }
        long limitCounter = raidBattle.getLimitCounter();
        boolean flagLimitCounter = false;
        String raidBattleLimitCounterKey = EnumRedisKey.RAIDBATTLE_LIMIT_COUNTER.getKey(String.valueOf(playerId), stageId);
        if (limitCounter > 0) {
            flagLimitCounter = true;
            String timesStr = redisTemplate.opsForValue().get(raidBattleLimitCounterKey);
            if (!StringUtils.isEmpty(timesStr)) {
                if (Long.valueOf(timesStr) >= limitCounter) {
                    promise.setFailure(GameNotification.newBuilder(GameErrorCode.StageReachLimitCount).build());
                    return;
                }
            }
        }
        //消耗道具
        Map<String, Integer> costItemMap = raidBattle.getCostItemMap();
        boolean flagCostItem = false;
        if (costItemMap != null && costItemMap.size() > 0) {
            flagCostItem = playerManager.getInventoryManager().checkItemEnough(costItemMap);
//            try {
//                flagCostItem = playerManager.getInventoryManager().checkItemEnough(costItemMap);
//            } catch (RuntimeException e) {
//                promise.setFailure(e);
//                return;
//            }
        }
        //疲劳
        int costStaminaPoint = raidBattle.getCostStaminaPoint();
        if (costStaminaPoint > 0) {
            if (costStaminaPoint > player.getStamina().getValue()) {
                promise.setFailure(GameNotification.newBuilder(GameErrorCode.StageDbClosed).build());
                return;
            }
        }
        //同时战斗不允许超过N个
        //非多人战 1
        //多人战 5
        String sameTimeKey = EnumRedisKey.RAIDBATTLE_SAMETIME_RAID_LIMIT.getKey(String.valueOf(playerId));
        Set<String> sameTimeRaids = redisTemplate.opsForSet().members(sameTimeKey);
        if(sameTimeRaids != null && sameTimeRaids.size() > 0){
            AtomicInteger multiRaid = new AtomicInteger();
            List<String> removeList = new ArrayList<>();
            long now = System.currentTimeMillis();
            List<String> collect = sameTimeRaids.stream().filter(eachRaidId -> {
                String tempKey = EnumRedisKey.RAIDBATTLE_RAIDID_DETAILS.getKey(eachRaidId);
                String tempBattleDetails = redisTemplate.opsForValue().get(tempKey);
                if (!StringUtils.isEmpty(tempBattleDetails)) {
                    RaidBattle tempRbd = JSON.parseObject(tempBattleDetails, RaidBattle.class);
                    if (tempRbd != null && (!tempRbd.isFinish() || tempRbd.getExpired() > now)) {
                        if (tempRbd.isMultiRaid()) {
                            multiRaid.getAndIncrement();
                        } else {
                            return !raidBattle.isMultiRaid();
                        }
                    }
                } else {
                    removeList.add(eachRaidId);
                }
                return false;
            }).collect(Collectors.toList());
            //顺便清理已不存在的战斗
            if(removeList.size() > 0){
                redisTemplate.opsForSet().remove(sameTimeKey,removeList.toArray());
            }
            if(!raidBattle.isMultiRaid() && collect.size() > 0){
                String singleRaidId = collect.get(0);
                String raidKey = EnumRedisKey.RAIDBATTLE_RAIDID_DETAILS.getKey(singleRaidId);
                String battleJson = redisTemplate.opsForValue().get(raidKey);
                try{
                    RaidBattle createdBattle = JSON.parseObject(battleJson, RaidBattle.class);
                    BeanUtils.copyProperties(createdBattle, response.getBodyObj());
                    promise.setSuccess(response);
                    return;
                }catch (Exception e){
                    e.printStackTrace();
                    promise.setFailure(null);
                }
            }else if(raidBattle.isMultiRaid() && multiRaid.get() >= 5){
                promise.setFailure(GameNotification.newBuilder(GameErrorCode.MultiRaidBattleSameTimeReachLimitCount).build());
            }
        }

        if (flagCostItem) {
            playerManager.getInventoryManager().consumeItem(costItemMap);
        }
        StaminaSubPointEvent staminaSubPointEvent = new StaminaSubPointEvent(this, playerManager, costStaminaPoint);
        context.publishEvent(staminaSubPointEvent);
        if (flagLimitCounter) {
            //TODO:非多人战应该在完成战斗后再写入
            long coolDownTimestamp = CalcCoolDownUtils.calcCoolDownTimestamp(raidBattle.getLimitCounterRefreshType());
            redisTemplate.opsForValue().setIfAbsent(raidBattleLimitCounterKey, "0", coolDownTimestamp, TimeUnit.MILLISECONDS);
            redisTemplate.opsForValue().increment(raidBattleLimitCounterKey);
        }
        //设定过期时间
        long now = System.currentTimeMillis();
        long restTime = raidBattle.getRestTime();
        String raidId = DigestUtils.md5Hex(playerId + stageId + UUID.randomUUID().toString());
        JoinRaidBattleMsgRequest joinRaidBattleMsgRequest = new JoinRaidBattleMsgRequest();
        int serviceId = joinRaidBattleMsgRequest.getHeader().getServiceId();
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> raidBattleServerInstance.selectRaidBattleServerId(raidId,serviceId), executor).whenComplete((r,e)->{
            if(e != null){
                e.printStackTrace();
                logger.error("取得负载ID失败",e);
            }
        });
        raidBattle.setRaidId(raidId);
        raidBattle.setExpired(now + restTime);
        raidBattle.setOwnerPlayerId(playerId);
        //redis缓存相关
        String battleDetailsJson = JSON.toJSONString(raidBattle);
        String raidIdKey = EnumRedisKey.RAIDBATTLE_RAIDID_DETAILS.getKey(raidId);
        //可通过 raidId查找 战斗详情
        redisTemplate.opsForValue().setIfAbsent(raidIdKey, battleDetailsJson, EnumRedisKey.RAIDBATTLE_RAIDID_DETAILS.getTimeout());
        //TODO:提前创建战斗报酬,内容为空,但在战斗详情消失/结束前不会允许访问
        //映射 stageId playerId - raidId方便查找
        redisTemplate.opsForValue().setIfAbsent(stageIdPlayerIdKey,raidId,EnumRedisKey.RAIDBATTLE_STAGEID_PLAYERID_TO_RAIDID.getTimeout());
        //加入到个人拥有的raid数组当中
        redisTemplate.opsForSet().add(sameTimeKey,raidId);
        //TODO: [重要]写入本战斗由哪个RaidBattle服务来处理
        Integer serverId = future.get();
//        DefaultPromise<Integer> serverIdPromise = new DefaultPromise<>(executor);
//        Promise<Integer> await = raidBattleServerInstance.selectRaidBattleServerId(raidId, serverConfig.getServiceId(), serverIdPromise).sync();
//        Integer serverId = await.get();
        BeanUtils.copyProperties(raidBattle, response.getBodyObj());
        promise.setSuccess(response);

        //令特定serverId从redis中取得信息并且创建?
        /*raidBattleServerInstance.selectRaidBattleServerId(raidId,serverConfig.getServiceId(),serverIdPromise).addListener(future -> {
            if(future.isSuccess()){
                Integer serverId = (Integer) future.get();
            }
        });*/
    }

    @UserEvent(GetArenaPlayerEventUser.class)
    public void getArenaPlayer(UserEventContext<PlayerManager> utx, GetArenaPlayerEventUser event, Promise<Object> promise) {
        GetArenaPlayerListMsgResponse.ArenaPlayer arenaPlayer = new GetArenaPlayerListMsgResponse.ArenaPlayer();
        Player player = utx.getDataManager().getPlayer();
        arenaPlayer.setPlayerId(player.getPlayerId());
        arenaPlayer.setNickName(player.getNickName());
        Map<String, String> heros = new HashMap<>();
        player.getHeros().forEach((k, v) -> {// 复制处理一下，防止对象安全溢出。
            heros.put(k, v);
        });
        arenaPlayer.setHeros(heros);
        promise.setSuccess(arenaPlayer);
    }
}
