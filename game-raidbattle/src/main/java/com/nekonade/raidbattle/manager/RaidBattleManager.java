package com.nekonade.raidbattle.manager;

import com.nekonade.common.concurrent.GameEventExecutorGroup;
import com.nekonade.common.constcollections.EnumCollections;
import com.nekonade.common.dto.PlayerDTO;
import com.nekonade.common.dto.RaidBattleTarget;
import com.nekonade.common.error.exceptions.GameNotifyException;
import com.nekonade.common.gameMessage.DataManager;
import com.nekonade.dao.db.entity.RaidBattle;
import com.nekonade.raidbattle.event.function.PushRaidBattleEvent;
import com.nekonade.raidbattle.event.user.JoinedRaidBattlePlayerInitCharacterEventUser;
import com.nekonade.raidbattle.message.channel.RaidBattleChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
@Slf4j
public class RaidBattleManager extends DataManager {

    private final GameEventExecutorGroup rbExecutorGroup;

    private final EventExecutorGroup kafkaSend;

    public enum Constants {
        EnemiesIsEmpty(-2),
        RaidBattleDoNothing(-1),
        RaidBattleFinish(0),
        RaidBattleExpired(1),
        AllPlayerRetried(2),
        ;

        Constants(int type) {
            this.type = type;
        }

        private final int type;

        public int getType(){
            return type;
        }
    }

    private final ApplicationContext context;

    private final RaidBattleChannel gameChannel;

    private final RaidBattle raidBattle;

    @Getter
    @Setter
    private volatile PushRaidBattleEvent event = null;

    public RaidBattleManager(RaidBattle raidBattle, ApplicationContext applicationContext, RaidBattleChannel gameChannel) {
        this.context = applicationContext;
        this.gameChannel = gameChannel;
        this.raidBattle = raidBattle;
        int subThreads = Math.max(16,raidBattle.getMaxPlayers() * 2);
        this.rbExecutorGroup = new GameEventExecutorGroup(subThreads);
        kafkaSend = new DefaultEventExecutorGroup(1);
        kafkaSend.scheduleWithFixedDelay(()->{
            if(event != null){
                long startTime = System.currentTimeMillis();
                synchronized (event){
                    final PushRaidBattleEvent finalEvent = event;
                    event = null;
                    context.publishEvent(finalEvent);
                }
                long endTime = System.currentTimeMillis();
                log.info("Kafka Send {} \t {} \t Cost:{}",startTime,endTime,(endTime-startTime));
            }
        },100,100, TimeUnit.MILLISECONDS);
    }

    public Promise<Object> playerJoinRaidBattle(PlayerDTO playerDTO, DefaultPromise<Object> promise) {
        JoinedRaidBattlePlayerInitCharacterEventUser joinedRaidBattlePlayerInitCharacterEventUser = new JoinedRaidBattlePlayerInitCharacterEventUser(playerDTO,this);
        gameChannel.fireUserEvent(joinedRaidBattlePlayerInitCharacterEventUser,promise);
        return promise;
    }

    public RaidBattle.Player getPlayerByPlayerId(long playerId) {
        Optional<RaidBattle.Player> playerOp = raidBattle.getPlayers().values().stream().filter(each -> each.getPlayerId() == playerId).findFirst();
        if (playerOp.isPresent()) {
            return playerOp.get();
        }
        throw GameNotifyException.newBuilder(EnumCollections.CodeMapper.GameErrorCode.MultiRaidBattlePlayerNotJoinedIn).build();
    }

    private void setEnemyDead(RaidBattle.Enemy enemy) {
        enemy.setAlive(0);
        enemy.setHp(0L);
    }

    private void setEnemyShouldBeDead(RaidBattle.Enemy enemy) {
        if (enemy.getHp() <= 0) {
            enemy.setAlive(0);
        }
    }

    public Boolean isTargetAlive(List<RaidBattleTarget> targets, int index) {
        if (targets.size() <= index) {
            return null;
        }
        RaidBattleTarget target = targets.get(index);
        return isTargetAlive(target);
    }

    public List<RaidBattle.Enemy> getLivingEnemy(List<RaidBattle.Enemy> enemies){
        return enemies.stream().filter(this::isTargetAlive).collect(Collectors.toList());
    }

    public List<RaidBattle.Player.Character> getLivingCharacter(List<RaidBattle.Player.Character> characters){
        return characters.stream().filter(this::isTargetAlive).collect(Collectors.toList());
    }

    private boolean isTargetAlive(RaidBattleTarget target) {
        return target.getAlive() == 1 || target.getHp() > 0;
    }


    private boolean isRaidBattleExpired() {
        return raidBattle.getExpired() <= System.currentTimeMillis();
    }

    private boolean isRaidBattleAllPlayersRetired() {
        ConcurrentHashMap<Long, RaidBattle.Player> players = raidBattle.getPlayers();
        return players.size() == raidBattle.getMaxPlayers() && players.values().stream().allMatch(RaidBattle.Player::isRetreated);
    }

    public Constants checkRaidBattleShouldBeFinished() {
        CopyOnWriteArrayList<RaidBattle.Enemy> enemies = raidBattle.getEnemies();
        if (enemies.size() == 0) {
            //不存在怪物
            return Constants.EnemiesIsEmpty;
        }

        //超时
        if (!isRaidBattleFinishOrFailed() && isRaidBattleExpired()) {
            raidBattle.setFinish(true);
            raidBattle.setFailed(true);
            return Constants.RaidBattleExpired;
        }
        //当所有玩家都撤退时,强制结束
        if (isRaidBattleAllPlayersRetired()) {
            raidBattle.setFinish(true);
            raidBattle.setFailed(true);
            return Constants.AllPlayerRetried;
        }
        enemies.forEach(this::setEnemyShouldBeDead);
        if (enemies.stream().noneMatch(enemy -> enemy.getKey() == 1)) {
            //当不存在 Key Monster时
            if (enemies.stream().noneMatch(this::isTargetAlive)) {
                //没有存活
                raidBattle.setFinish(true);
                return Constants.RaidBattleFinish;
            }
        } else {
            //当存在Key Monster时
            //当所有Key Monster被击败时,其他Monster会被系统抹杀并结束战斗
            if (enemies.stream().filter(enemy -> enemy.getKey() == 1).noneMatch(this::isTargetAlive)) {
                enemies.forEach(this::setEnemyDead);
                //没有存活
                raidBattle.setFinish(true);
                return Constants.RaidBattleFinish;
            }
        }
        //尚有存活
        return Constants.RaidBattleDoNothing;
    }

    private boolean isRaidBattleFinish() {
        return raidBattle.isFinish();
    }

    private boolean isRaidBattleFailed() {
        return raidBattle.isFailed();
    }

    public boolean isRaidBattleFinishOrFailed() {
        return isRaidBattleFinish() || isRaidBattleFailed();
    }

    public void closeRaidBattleChannel(){
        this.getGameChannel().unsafeClose();
    }

    public boolean isRaidBattleChannelActive(){
       return !this.getGameChannel().isClose();
    }

    public boolean checkPlayerCharacterAllDead(RaidBattle.Player actionPlayer){
        return actionPlayer.getParty().values().stream().noneMatch(this::isTargetAlive);
    }

    public RaidBattle.Enemy getTargetEnemy(int targetPos){
        int index = targetPos > this.raidBattle.getEnemies().size() ? 0 : targetPos;
        RaidBattle.Enemy enemy = this.raidBattle.getEnemies().get(index);
        if(!isTargetAlive(enemy)){
            Optional<RaidBattle.Enemy> op = this.raidBattle.getEnemies().stream().filter(this::isTargetAlive).findFirst();
            enemy = op.orElse(null);
        }
        return enemy;
    }

}
