package com.nekonade.raidbattle.business;

import com.nekonade.common.dto.ItemDTO;
import com.nekonade.common.error.GameNotifyException;
import com.nekonade.common.error.code.GameErrorCode;
import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.daos.RaidBattleDao;
import com.nekonade.dao.daos.RaidBattleDbDao;
import com.nekonade.dao.daos.RaidBattleRewardDao;
import com.nekonade.dao.db.entity.RaidBattle;
import com.nekonade.dao.db.entity.RaidBattleReward;
import com.nekonade.dao.db.entity.data.RaidBattleDB;
import com.nekonade.dao.db.entity.data.RewardsDB;
import com.nekonade.common.gameMessage.IGameMessage;
import com.nekonade.network.param.game.message.battle.RaidBattleAttackMsgResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.raidbattle.event.function.PushRaidBattleEvent;
import com.nekonade.raidbattle.event.function.PushRaidBattleToSinglePlayerEvent;
import com.nekonade.raidbattle.event.function.RaidBattleShouldBeFinishEvent;
import com.nekonade.raidbattle.manager.RaidBattleManager;
import com.nekonade.raidbattle.message.context.RaidBattleMessageContext;
import com.nekonade.raidbattle.service.BroadCastMessageService;
import com.nekonade.raidbattle.service.RaidBattleRewardService;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@GameMessageHandler
public class EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private BroadCastMessageService broadCastMessageService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RaidBattleDbDao raidBattleDbDao;

    @Autowired
    private RaidBattleRewardDao raidBattleRewardDao;

    @Autowired
    private RaidBattleRewardService raidBattleRewardService;

    @Autowired
    private RaidBattleDao raidBattleDao;

    @EventListener
    public void raidBattleShouldBeFinish(RaidBattleShouldBeFinishEvent event) {
        RaidBattleManager dataManager = event.getRaidBattleManager();
        RaidBattle raidBattle = dataManager.getRaidBattle();
        boolean idleCheck = event.isIdleCheck();
        boolean raidBattleFinishOrFailed = dataManager.isRaidBattleFinishOrFailed();
        RaidBattleManager.Constants result = dataManager.checkRaidBattleShouldBeFinished();
        switch (result) {
            case AllPlayerRetried:
                //参加人数达到最大值 && 全部玩家已经撤退
                break;
            case RaidBattleExpired:
                //战斗已超时
                ClearRaidBattleAndChannel(dataManager);
                throw GameNotifyException.newBuilder(GameErrorCode.RaidBattleHasExpired).build();
            case RaidBattleFinish:
                //正常击败所有怪物
                String raidId = raidBattle.getRaidId();
                String stageId = raidBattle.getStageId();
                if(!idleCheck && !raidBattleFinishOrFailed){
                    List<Long> getRewardPlayerIds = raidBattle.getPlayers().values().stream().filter(player -> !player.isRetreated()).map(RaidBattle.Player::getPlayerId).collect(Collectors.toList());
                    RaidBattleDB raidBattleDb = raidBattleDbDao.findRaidBattleDb(stageId);
                    RewardsDB reward = raidBattleDb.getReward();
                    List<RewardsDB.Item> items = reward.getItems();
                    long now = System.currentTimeMillis();
                    for(long playerId : getRewardPlayerIds){
                        //遍历生成&发布奖励
                        RaidBattleReward raidBattleReward = new RaidBattleReward();
                        raidBattleReward.setPlayerId(playerId);
                        raidBattleReward.setRaidId(raidId);
                        raidBattleReward.setTimestamp(now);
                        raidBattleReward.setClaimed(0);
                        List<ItemDTO> list = new ArrayList<>();
                        raidBattleReward.setItems(list);
                        items.forEach(each->{
                            double prob = each.getProb();
                            if(prob == 0 || (prob > 0 && RandomUtils.nextDouble() >= prob)){
                                ItemDTO itemDTO = new ItemDTO();
                                int amount;
                                if(each.getRandomAmount()){
                                    amount = RandomUtils.nextInt(each.getRandomAmountMin(), each.getRandomAmountMax());
                                }else{
                                    amount = each.getAmount();
                                }
                                if(amount > 0){
                                    BeanUtils.copyProperties(each, itemDTO);
                                    itemDTO.setAmount(amount);
                                    raidBattleReward.getItems().add(itemDTO);
                                }
                            }
                        });
                        //在这里应该有个RPC事件发送到NekoServer负责处理成就
                        //或者可在领取报酬的时候实现成就达成
                        raidBattleRewardService.asyncSaveRaidBattleReward(raidBattleReward);
                    }
                }
                //删除战斗
                logger.info("RaidBattle:{} 正常结束战斗 Time:{}",raidId,System.currentTimeMillis());
                ClearRaidBattleAndChannel(dataManager);
                break;
            case RaidBattleDoNothing:
                //尚有存活
                break;
            default:
            case EnemiesIsEmpty:
                //不存在怪物
                return;
        }
    }

    @EventListener
    public void pushRaidBattleEvent(PushRaidBattleEvent event) {
        RaidBattle raidBattle = event.getRaidBattleManager().getRaidBattle();
        List<Long> boardIds = event.getBoardIds();
        if (boardIds.size() == 0) {
            boardIds = new ArrayList<>(raidBattle.getPlayers().keySet());
        }
        RaidBattleAttackMsgResponse response = new RaidBattleAttackMsgResponse();
        BeanUtils.copyProperties(raidBattle, response.getBodyObj());
        broadCastMessageService.broadCastRaidBattleStatus(response, boardIds);
    }

    @EventListener
    public void pushRaidBattleToSinglePlayerEvent(PushRaidBattleToSinglePlayerEvent event){
        //对未知Player所在Gateway时的广播方法
        /*
        RaidBattle originRaidBattle = event.getRaidBattleManager().getRaidBattle();
        long playerId = event.getPlayerId();
        RaidBattle raidBattle = GameBeanUtils.deepCopyByJson(originRaidBattle,RaidBattle.class);
        Optional<RaidBattle.Player> op = raidBattle.getPlayers().stream().filter(player -> player.getPlayerId() == playerId).findFirst();
        if(op.isEmpty()) return;
        RaidBattle.Player player = op.get();
        CopyOnWriteArrayList<RaidBattle.Player> resultList = new CopyOnWriteArrayList<>();
        resultList.add(player);
        raidBattle.setPlayers(resultList);
        RaidBattleAttackMsgResponse response = new RaidBattleAttackMsgResponse();
        BeanUtils.copyProperties(raidBattle, response.getBodyObj());
        broadCastMessageService.broadCastRaidBattleStatus(response, playerId);
        */
        RaidBattleManager getRaidBattleManager = event.getRaidBattleManager();
        IGameMessage request = event.getRequest();
        RaidBattleMessageContext<RaidBattleManager> ctx = event.getCtx();
        RaidBattle raidBattle = getRaidBattleManager.getRaidBattle();
        RaidBattleAttackMsgResponse response = new RaidBattleAttackMsgResponse();
        response.wrapResponse(request);
        BeanUtils.copyProperties(raidBattle, response.getBodyObj());
        ctx.sendMessage(response);
    }

    private void ClearRaidBattle(String raidId){
        {
            String key = EnumRedisKey.RAIDBATTLE_RAIDID_DETAILS.getKey(raidId);
            Boolean delete = redisTemplate.delete(key);
        }
        {
            String key = EnumRedisKey.RAIDBATTLE_RAIDID_TO_SERVERID.getKey(raidId);
            Boolean delete = redisTemplate.delete(key);
        }
        {
            String key = EnumRedisKey.RAIDBATTLE_RAIDID_TO_SERVERID_BACKUP.getKey(raidId);
            Boolean delete = redisTemplate.delete(key);
        }
    }

    private void ClearRaidBattleAndChannel(RaidBattleManager dataManager) {
        this.raidBattleDao.saveOrUpdateToDB(dataManager.getRaidBattle());
        dataManager.closeRaidBattleChannel();
        String raidId = dataManager.getRaidBattle().getRaidId();
        ClearRaidBattle(raidId);
    }
}
