package com.nekonade.neko.logic;

import com.nekonade.common.dto.Mail;
import com.nekonade.common.dto.RaidBattle;
import com.nekonade.common.error.GameErrorException;
import com.nekonade.dao.db.entity.Inventory;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.entity.Stamina;
import com.nekonade.common.model.PageResult;
import com.nekonade.dao.helper.SortParam;
import com.nekonade.dao.redis.EnumRedisKey;
import com.nekonade.neko.service.RaidBattleService;
import com.nekonade.neko.service.MailBoxService;
import com.nekonade.network.message.event.basic.*;
import com.nekonade.network.message.event.function.StaminaSubPointEvent;
import com.nekonade.network.message.manager.GameErrorCode;
import com.nekonade.network.message.manager.InventoryManager;
import com.nekonade.network.message.manager.PlayerManager;
import com.nekonade.neko.service.StaminaService;
import com.nekonade.network.message.context.UserEvent;
import com.nekonade.network.message.context.UserEventContext;
import com.nekonade.network.param.game.message.neko.*;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Promise;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;

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
    private RaidBattleService raidBattleService;

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
    public void GetSelfInfoEvent(UserEventContext<PlayerManager> utx, GetSelfInfoEventUser event, Promise<Object> promise){
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
    public void getStaminaEvent(UserEventContext<PlayerManager> utx, GetStaminaEventUser event, Promise<Object> promise){
        GetStaminaMsgResponse response = new GetStaminaMsgResponse();
        utx.getDataManager().getStaminaManager().checkStamina();
        Stamina stamina = utx.getDataManager().getStaminaManager().getStamina().clone();
        BeanUtils.copyProperties(stamina,response.getBodyObj());
        promise.setSuccess(response);
    }

    @UserEvent(GetInventoryEventUser.class)
    public void getInventoryEvent(UserEventContext<PlayerManager> utx, GetInventoryEventUser event, Promise<Object> promise){
        GetInventoryMsgResponse response = new GetInventoryMsgResponse();
        InventoryManager inventoryManager = utx.getDataManager().getInventoryManager();
        Inventory inventory = inventoryManager.getInventory().clone();
        BeanUtils.copyProperties(inventory,response.getBodyObj());
        promise.setSuccess(response);
    }

    @UserEvent(GetMailBoxEventUser.class)
    public void getMailBoxEvent(UserEventContext<PlayerManager> utx, GetMailBoxEventUser event, Promise<Object> promise){
        GetMailBoxMsgResponse response = new GetMailBoxMsgResponse();
        PlayerManager playerManager = utx.getDataManager();
        long playerId = playerManager.getPlayer().getPlayerId();
        SortParam sortParam = new SortParam();
        sortParam.setSortDirection(Sort.Direction.DESC);
        PageResult<Mail> result = mailBoxService.findByPage(playerId, null, 1, 10, sortParam);
        BeanUtils.copyProperties(result,response.getBodyObj());
        promise.setSuccess(response);
    }

    @UserEvent(CreateBattleEventUser.class)
    public void createBattle(UserEventContext<PlayerManager> utx, CreateBattleEventUser event, Promise<Object> promise){
        PlayerManager playerManager = utx.getDataManager();
        Player player = playerManager.getPlayer();
        long playerId = event.getPlayerId();
        CreateBattleMsgResponse response = new CreateBattleMsgResponse();
        RaidBattle raidBattle = raidBattleService.findRaidBattleDb(playerId, event.getRequest());
        //不存在的关卡
        if(raidBattle == null){
            promise.setFailure(GameErrorException.newBuilder(GameErrorCode.StageDbNotFound).build());
            return;
        }
        //未激活/已关闭的活动关卡
        if(!raidBattle.isActive()){
            promise.setFailure(GameErrorException.newBuilder(GameErrorCode.StageDbClosed).build());
            return;
        }
        //其他特殊条件========= 如不能使用某个角色,必须多少级,等等
        if(false){
            promise.setFailure(null);
            return;
        }
        long limitCounter = raidBattle.getLimitCounter();
        String stageId = raidBattle.getStageId();
        if(limitCounter > 0){
            //先查是否在限定次数的数组里
            String limitStageidsKey = EnumRedisKey.RAIDBATTLE_LIMIT_STAGEIDS.getKey();
            String limitCountStr = (String) redisTemplate.opsForHash().get(limitStageidsKey, stageId);
            if(!StringUtils.isEmpty(limitCountStr)){
                String raidbattleLimitCounterKey = EnumRedisKey.RAIDBATTLE_LIMIT_COUNTER.getKey(String.valueOf(playerId), stageId);
                String timesStr = redisTemplate.opsForValue().get(raidbattleLimitCounterKey);
                if(!StringUtils.isEmpty(timesStr)){
                    Long limitCount = Long.valueOf(limitCountStr);
                    Long times = Long.valueOf(timesStr);
                    //计算冷却时间
                }
            }

        }


        //疲劳
        int costStaminaPoint = raidBattle.getCostStaminaPoint();
        if(costStaminaPoint > 0){
            if(costStaminaPoint > player.getStamina().getValue()){
                promise.setFailure(GameErrorException.newBuilder(GameErrorCode.StageDbClosed).build());
                return;
            }
        }
        StaminaSubPointEvent staminaSubPointEvent = new StaminaSubPointEvent(this, playerManager,costStaminaPoint);
        context.publishEvent(staminaSubPointEvent);
        BeanUtils.copyProperties(raidBattle,response.getBodyObj());
        promise.setSuccess(response);
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
