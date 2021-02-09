package com.nekonade.raidbattle.manager;

import com.nekonade.common.dto.Player;
import com.nekonade.common.error.GameNotification;
import com.nekonade.common.error.code.GameErrorCode;
import com.nekonade.dao.db.entity.RaidBattle;
import com.nekonade.network.param.game.message.neko.error.GameErrorMsgResponse;
import com.nekonade.raidbattle.message.channel.RaidBattleChannel;
import lombok.Getter;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class RaidBattleManager {

    private final ApplicationContext context;

    private final RaidBattleChannel gameChannel;

    private final RaidBattle raidBattle;

    public RaidBattleManager(RaidBattle raidBattle, ApplicationContext applicationContext, RaidBattleChannel gameChannel) {
        this.context = applicationContext;
        this.gameChannel = gameChannel;
        this.raidBattle = raidBattle;
    }

    public void addPlayer(Player player){
        CopyOnWriteArrayList<Player> players = raidBattle.getPlayers();
        if(players.size() >= 30){
            throw GameNotification.newBuilder(GameErrorCode.MultiRaidBattlePlayersReachMax).build();
        }
        boolean joined = players.stream().anyMatch(eachPlayer -> eachPlayer.getPlayerId() == player.getPlayerId());
        if(joined) {
            return;
//            throw GameNotification.newBuilder(GameErrorCode.MultiRaidBattlePlayersReachMax).build();
        }
        players.addIfAbsent(player);

    }
}
