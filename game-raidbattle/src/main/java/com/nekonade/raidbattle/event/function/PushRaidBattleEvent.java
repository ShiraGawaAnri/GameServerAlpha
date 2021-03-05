package com.nekonade.raidbattle.event.function;

import com.nekonade.raidbattle.manager.RaidBattleManager;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PushRaidBattleEvent extends ApplicationEvent {

    private final RaidBattleManager raidBattleManager;

    private final List<Long> boardIds;

    private final long fromPlayerId;

    public PushRaidBattleEvent(Object source,RaidBattleManager raidBattleManager,long fromPlayerId,List<Long> boardIds) {
        super(source);
        this.raidBattleManager = raidBattleManager;
        this.boardIds = boardIds;
        this.fromPlayerId = fromPlayerId;
    }

    public PushRaidBattleEvent(Object source,RaidBattleManager raidBattleManager,long fromPlayerId) {
        super(source);
        this.raidBattleManager = raidBattleManager;
        this.boardIds = new ArrayList<>();
        this.fromPlayerId = fromPlayerId;
    }
}
