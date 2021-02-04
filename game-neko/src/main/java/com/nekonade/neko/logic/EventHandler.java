package com.nekonade.neko.logic;

import com.nekonade.common.dto.Mail;
import com.nekonade.common.dto.RaidBattle;
import com.nekonade.common.error.GameErrorException;
import com.nekonade.dao.db.entity.Inventory;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.entity.Stamina;
import com.nekonade.common.model.PageResult;
import com.nekonade.dao.helper.SortParam;
import com.nekonade.neko.service.RaidBattleService;
import com.nekonade.neko.service.MailBoxService;
import com.nekonade.network.message.event.basic.*;
import com.nekonade.network.message.manager.GameErrorCode;
import com.nekonade.network.message.manager.InventoryManager;
import com.nekonade.network.message.manager.PlayerManager;
import com.nekonade.neko.service.StaminaService;
import com.nekonade.network.message.context.UserEvent;
import com.nekonade.network.message.context.UserEventContext;
import com.nekonade.network.message.rpc.GameRPCError;
import com.nekonade.network.param.game.message.neko.*;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;

@GameMessageHandler
public class EventHandler {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PlayerLogicHandler.class);

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
        long playerId = utx.getDataManager().getPlayer().getPlayerId();
        CreateBattleMsgResponse response = new CreateBattleMsgResponse();
        RaidBattle raidBattle = raidBattleService.createRaidBattle(event.getPlayerId(), event.getRequest());
        if(raidBattle == null){
            promise.setFailure(GameErrorException.newBuilder(GameErrorCode.StageDbNotFound).build());
            return;
        }
        if(!raidBattle.isActive()){
            promise.setFailure(GameErrorException.newBuilder(GameErrorCode.StageDbNotFound).build());
            return;
        }
        CreateBattleMsgResponse.RaidBattle rb = new CreateBattleMsgResponse.RaidBattle();
        BeanUtils.copyProperties(raidBattle,rb);
        response.setBodyObj(rb);
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
