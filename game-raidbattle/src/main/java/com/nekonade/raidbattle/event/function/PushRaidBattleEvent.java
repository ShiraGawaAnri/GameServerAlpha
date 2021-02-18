package com.nekonade.raidbattle.event.function;

import com.nekonade.raidbattle.event.user.BasicEventUser;
import com.nekonade.raidbattle.manager.RaidBattleManager;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PushRaidBattleEvent extends ApplicationEvent {

    private final RaidBattleManager raidBattleManager;

    private final List<Long> boardIds;

    public PushRaidBattleEvent(Object source,RaidBattleManager raidBattleManager,List<Long> boardIds) {
        super(source);
        this.raidBattleManager = raidBattleManager;
        this.boardIds = boardIds;
    }

    public PushRaidBattleEvent(Object source,RaidBattleManager raidBattleManager) {
        super(source);
        this.raidBattleManager = raidBattleManager;
        this.boardIds = new ArrayList<>();
    }
}
