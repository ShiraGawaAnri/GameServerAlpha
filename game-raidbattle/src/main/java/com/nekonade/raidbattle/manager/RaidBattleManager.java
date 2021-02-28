package com.nekonade.raidbattle.manager;

import com.nekonade.common.dto.PlayerDTO;
import com.nekonade.common.error.GameNotifyException;
import com.nekonade.common.error.code.GameErrorCode;
import com.nekonade.dao.db.entity.RaidBattle;
import com.nekonade.raidbattle.message.channel.RaidBattleChannel;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class RaidBattleManager {

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



    public RaidBattleManager(RaidBattle raidBattle, ApplicationContext applicationContext, RaidBattleChannel gameChannel) {
        this.context = applicationContext;
        this.gameChannel = gameChannel;
        this.raidBattle = raidBattle;

    }

    public void addPlayer(PlayerDTO playerDTO) {
        ConcurrentHashMap<Long, RaidBattle.Player> players = raidBattle.getPlayers();
        if (!raidBattle.getMultiRaid()) {
            if (raidBattle.getOwnerPlayerId() != playerDTO.getPlayerId()) {
                throw GameNotifyException.newBuilder(GameErrorCode.SingleRaidNotAcceptOtherPlayer).build();
            }
            return;
        }
        //对应极少出现的情况
        if(isRaidBattleFinishOrFailed()){
            throw GameNotifyException.newBuilder(GameErrorCode.RaidBattleHasGone).build();
        }
        if (players.size() >= raidBattle.getMaxPlayers()) {
            throw GameNotifyException.newBuilder(GameErrorCode.MultiRaidBattlePlayersReachMax).build();
        }
        boolean joined = players.values().stream().anyMatch(eachPlayer -> eachPlayer.getPlayerId() == playerDTO.getPlayerId());
        if (joined) {
            throw GameNotifyException.newBuilder(GameErrorCode.MultiRaidBattlePlayersJoinedIn).build();
        }
        RaidBattle.Player rbPlayer = new RaidBattle.Player();
        BeanUtils.copyProperties(playerDTO, rbPlayer);
        players.putIfAbsent(rbPlayer.getPlayerId(),rbPlayer);
    }

    public RaidBattle.Player getPlayerByPlayerId(long playerId) {
        Optional<RaidBattle.Player> playerOp = raidBattle.getPlayers().values().stream().filter(each -> each.getPlayerId() == playerId).findFirst();
        if (playerOp.isPresent()) {
            return playerOp.get();
        }
        throw GameNotifyException.newBuilder(GameErrorCode.MultiRaidBattlePlayerNotJoinedIn).build();
    }

    private void setEnemyDead(RaidBattle.Enemy enemy) {
        enemy.setAlive(0);
        enemy.setHp(0);
    }

    private void setEnemyShouldBeDead(RaidBattle.Enemy enemy) {
        if (enemy.getHp() <= 0) {
            enemy.setAlive(0);
        }
    }

    private Boolean isEnemyAlive(List<RaidBattle.Enemy> enemies, int index) {
        if (enemies.size() <= index) {
            return null;
        }
        RaidBattle.Enemy enemy = enemies.get(index);
        return isEnemyAlive(enemy);
    }

    private boolean isEnemyAlive(RaidBattle.Enemy enemy) {
        return enemy.getAlive() == 1 || enemy.getHp() > 0;
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
            if (enemies.stream().noneMatch(this::isEnemyAlive)) {
                //没有存活
                raidBattle.setFinish(true);
                return Constants.RaidBattleFinish;
            }
        } else {
            //当存在Key Monster时
            //当所有Key Monster被击败时,其他Monster会被系统抹杀并结束战斗
            if (enemies.stream().filter(enemy -> enemy.getKey() == 1).noneMatch(this::isEnemyAlive)) {
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

    public boolean checkPlayerCharacterAllDead(RaidBattle.Player actionPlayer){
        return actionPlayer.getParty().values().stream().anyMatch(character -> character.getHp() > 0);
    }

    public RaidBattle.Enemy getTargetEnemy(int targetPos){
        int index = targetPos > this.raidBattle.getEnemies().size() ? 0 : targetPos;
        RaidBattle.Enemy enemy = this.raidBattle.getEnemies().get(index);
        if(!isEnemyAlive(enemy)){
            Optional<RaidBattle.Enemy> op = this.raidBattle.getEnemies().stream().filter(this::isEnemyAlive).findFirst();
            enemy = op.orElse(null);
        }
        return enemy;
    }

    public void cardAttack(int chara, int cardId, long turn) {
        CopyOnWriteArrayList<RaidBattle.Enemy> enemies = raidBattle.getEnemies();
        if (enemies.size() == 0) {
            return;
        }
        enemies.forEach(each -> {
            if (each.getAlive() == 1) {
                int hp = each.getHp();
                each.setHp(Math.max(0, hp - 1));
            }
        });
        //如果死亡则触发Event
    }


}
