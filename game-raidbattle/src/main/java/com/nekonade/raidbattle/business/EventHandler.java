package com.nekonade.raidbattle.business;

import com.nekonade.common.constcollections.EnumCollections;
import com.nekonade.common.dto.CharacterDTO;
import com.nekonade.common.dto.ItemDTO;
import com.nekonade.common.dto.PlayerDTO;
import com.nekonade.common.dto.RaidBattleDamageDTO;
import com.nekonade.common.error.exceptions.GameNotifyException;
import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.daos.RaidBattleDao;
import com.nekonade.dao.daos.db.RaidBattleDbDao;
import com.nekonade.dao.daos.RaidBattleRewardDao;
import com.nekonade.dao.db.entity.RaidBattle;
import com.nekonade.dao.db.entity.RaidBattleReward;
import com.nekonade.dao.db.entity.data.RaidBattleDB;
import com.nekonade.dao.db.entity.data.RewardsDB;
import com.nekonade.common.gameMessage.IGameMessage;
import com.nekonade.network.param.game.message.battle.RaidBattleAttackMsgResponse;
import com.nekonade.network.param.game.message.battle.RaidBattleBoardCastMsgResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.raidbattle.event.function.PushRaidBattleEvent;
import com.nekonade.raidbattle.event.function.RaidBattleShouldBeFinishEvent;
import com.nekonade.raidbattle.event.user.JoinedRaidBattlePlayerInitCharacterEventUser;
import com.nekonade.raidbattle.event.user.PushRaidBattleDamageDTOEventUser;
import com.nekonade.raidbattle.event.user.PushRaidBattleToSinglePlayerEventUser;
import com.nekonade.raidbattle.manager.RaidBattleManager;
import com.nekonade.raidbattle.message.context.RaidBattleEvent;
import com.nekonade.raidbattle.message.context.RaidBattleEventContext;
import com.nekonade.raidbattle.service.BroadCastMessageService;
import com.nekonade.raidbattle.service.CalcRaidBattleService;
import com.nekonade.raidbattle.service.RaidBattleRewardService;
import io.netty.util.concurrent.Promise;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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

    @Autowired
    private CalcRaidBattleService calcRaidBattleService;

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
                throw GameNotifyException.newBuilder(EnumCollections.CodeMapper.GameErrorCode.RaidBattleHasExpired).build();
            case RaidBattleFinish:
                //正常击败所有怪物
                String raidId = raidBattle.getRaidId();
                String stageId = raidBattle.getStageId();
                if(!idleCheck && !raidBattleFinishOrFailed){
                    int totalPlayers = raidBattle.getPlayers().size();
                    List<Long> getRewardPlayerIds = raidBattle.getPlayers().values().stream().filter(player -> !player.isRetreated()).map(RaidBattle.Player::getPlayerId).collect(Collectors.toList());
                    RaidBattleDB raidBattleDb = raidBattleDbDao.findRaidBattleDb(stageId);
                    RewardsDB reward = raidBattleDb.getReward();
                    List<RewardsDB.Item> items = reward.getItems();
                    long now = System.currentTimeMillis();
                    for(long playerId : getRewardPlayerIds){
                        //遍历生成&发布奖励
                        RaidBattle.Player player = raidBattle.getPlayers().get(playerId);
                        RaidBattleReward raidBattleReward = new RaidBattleReward();
                        raidBattleReward.setPlayerId(playerId);
                        raidBattleReward.setRaidId(raidId);
                        raidBattleReward.setTimestamp(now);
                        raidBattleReward.setClaimed(0);
                        raidBattleReward.setStageId(raidBattle.getStageId());
                        raidBattleReward.setPlayers(totalPlayers);
                        raidBattleReward.setContribution(player.getContributePoint());
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
                    logger.info("执行了奖励分发");
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
        IGameMessage gameMessage = event.getGameMessage();
        RaidBattle raidBattle = event.getRaidBattleManager().getRaidBattle();
        long playerId = gameMessage.getHeader().getPlayerId();
        List<Long> boardIds = event.getBoardIds();
        if (boardIds.size() == 0) {
            boardIds = new ArrayList<>(raidBattle.getPlayers().keySet());
            boardIds.remove(playerId);
        }
        RaidBattleBoardCastMsgResponse response = new RaidBattleBoardCastMsgResponse();
        response.wrapResponse(gameMessage);
        BeanUtils.copyProperties(raidBattle, response.getBodyObj());
        broadCastMessageService.broadCastRaidBattleStatus(response, boardIds);
        logger.info("{} 广播战斗状态 {} {} {}",System.currentTimeMillis(),playerId,gameMessage.getHeader().getClientSeqId(),gameMessage.getHeader().getClientSendTime());
    }

    @RaidBattleEvent(PushRaidBattleToSinglePlayerEventUser.class)
    public void pushRaidBattleToSinglePlayerEventUser(RaidBattleEventContext<RaidBattleManager> rtx, PushRaidBattleToSinglePlayerEventUser event, Promise<Object> promise){
        RaidBattleManager dataManager = rtx.getDataManager();
        RaidBattle raidBattle = dataManager.getRaidBattle();
        IGameMessage request = event.getRequest();
        RaidBattleBoardCastMsgResponse response = new RaidBattleBoardCastMsgResponse();
        response.wrapResponse(request);
        BeanUtils.copyProperties(raidBattle, response.getBodyObj());
        //ctx.sendMessage(response);
        rtx.getCtx().writeAndFlush(response);
    }

    @RaidBattleEvent(PushRaidBattleDamageDTOEventUser.class)
    public void pushRaidBattleDamageDTOEvent(RaidBattleEventContext<RaidBattleManager> rtx, PushRaidBattleDamageDTOEventUser event, Promise<Object> promise){
        IGameMessage request = event.getRequest();
        RaidBattleDamageDTO damageDTO = event.getDamageDTO();
        RaidBattleAttackMsgResponse response = new RaidBattleAttackMsgResponse();
        BeanUtils.copyProperties(damageDTO, response.getBodyObj());
        /*
        //Protobuf version
        RaidBattleAttackMsgBody.RaidBattleAttackMsgResponseBody.Builder builder = RaidBattleAttackMsgBody.RaidBattleAttackMsgResponseBody.newBuilder();
        builder.setRaidId(damageDTO.getRaidId());
        RaidBattleAttackMsgBody.Status status = RaidBattleAttackMsgBody.Status.newBuilder().build();
        builder.setStatus(status);
        damageDTO.getScenario().forEach(each->{
            if(each instanceof RaidBattleDamageDTO.Contribution){
                RaidBattleDamageDTO.Contribution temp = (RaidBattleDamageDTO.Contribution) each;
                RaidBattleAttackMsgBody.Contribution tempResult = RaidBattleAttackMsgBody.Contribution.newBuilder().setAmount(temp.getAmount()).build();

                builder.addScenario(Any.pack(tempResult));
            }else if(each instanceof RaidBattleDamageDTO.Attack){

                RaidBattleAttackMsgBody.Attack.Builder tempResult = RaidBattleAttackMsgBody.Attack.newBuilder();
                RaidBattleDamageDTO.Attack temp = (RaidBattleDamageDTO.Attack) each;
                tempResult.setFrom(temp.getFrom()).setPos(temp.getPos()).setConcurrentAttack(temp.isConcurrentAttack()).setAllAttack(temp.isAllAttack());

                List<RaidBattleDamageDTO.Damage> damages = temp.getDamages();
                damages.forEach(eachDamage->{
                    RaidBattleAttackMsgBody.Damage.Builder eachTempBuilder = RaidBattleAttackMsgBody.Damage.newBuilder();
                    eachTempBuilder.setElement(eachDamage.getElement()).setPos(eachDamage.getPos()).setValue(eachDamage.getValue()).setHp(eachDamage.getHp()).setCritical(eachDamage.isCritical()).setMiss(eachDamage.getMiss()).setGuard(eachDamage.isGuard()).setEffect(eachDamage.getEffect());
                    tempResult.addDamages(eachTempBuilder.build());
                });

                builder.addScenario(Any.pack(tempResult.build()));
            }
        });

        RaidBattleAttackMsgBody.RaidBattleAttackMsgResponseBody responseBody = builder.build();
        response.setResponseBody(responseBody);*/

        response.wrapResponse(request);

        rtx.getCtx().writeAndFlush(response);
        if(promise != null){
            promise.setSuccess(true);
        }
    }

    @RaidBattleEvent(JoinedRaidBattlePlayerInitCharacterEventUser.class)
    public void joinedRaidBattlePlayerInitCharacterEventUser(RaidBattleEventContext<RaidBattleManager> rtx, JoinedRaidBattlePlayerInitCharacterEventUser event, Promise<Boolean> promise){
        PlayerDTO playerDTO = event.getPlayerDTO();
        RaidBattleManager raidBattleManager = event.getRaidBattleManager();
        RaidBattle raidBattle = raidBattleManager.getRaidBattle();
        ConcurrentHashMap<Long, RaidBattle.Player> players = raidBattle.getPlayers();
        if (!raidBattle.getMultiRaid()) {
            if (raidBattle.getOwnerPlayerId() != playerDTO.getPlayerId()) {
                promise.setFailure(GameNotifyException.newBuilder(EnumCollections.CodeMapper.GameErrorCode.SingleRaidNotAcceptOtherPlayer).build());
            }
            return;
        }
        //对应极少出现的情况
        if(raidBattleManager.isRaidBattleFinishOrFailed()){
            promise.setFailure(GameNotifyException.newBuilder(EnumCollections.CodeMapper.GameErrorCode.RaidBattleHasGone).build());
            return;
        }
        if (players.size() >= raidBattle.getMaxPlayers()) {
            promise.setFailure(GameNotifyException.newBuilder(EnumCollections.CodeMapper.GameErrorCode.MultiRaidBattlePlayersReachMax).build());
            return;
        }
        boolean joined = players.values().stream().anyMatch(eachPlayer -> eachPlayer.getPlayerId() == playerDTO.getPlayerId());
        if (joined) {
            //promise.setFailure(GameNotifyException.newBuilder(GameErrorCode.MultiRaidBattlePlayersJoinedIn).build());
            promise.setSuccess(true);
            return;
        }
        ConcurrentHashMap<String, CharacterDTO> characters = playerDTO.getCharacters();
        RaidBattle.Player addSelfPlayer = new RaidBattle.Player();
        BeanUtils.copyProperties(playerDTO, addSelfPlayer);
        //随机选N个角色加入到战斗队伍
        ConcurrentHashMap<String, RaidBattle.Player.Character> party = addSelfPlayer.getParty();
        List<CharacterDTO> characterList = new ArrayList<>(characters.values());
        Collections.shuffle(characterList);
        int getNumber = Math.min(4,characterList.size());
        //初始化Player角色数据
        for(int i = 0;i < getNumber;i++){
            CharacterDTO chara = characterList.get(i);
            RaidBattle.Player.Character character = calcRaidBattleService.CalcRaidBattleInitCharacterStatus(chara);
            party.put(chara.getCharaId(),character);
        }
        raidBattle.getPlayers().putIfAbsent(addSelfPlayer.getPlayerId(),addSelfPlayer);
        promise.setSuccess(true);
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
