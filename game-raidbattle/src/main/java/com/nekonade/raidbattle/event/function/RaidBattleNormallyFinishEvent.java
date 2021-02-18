package com.nekonade.raidbattle.event.function;

import com.nekonade.raidbattle.manager.RaidBattleManager;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RaidBattleNormallyFinishEvent extends ApplicationEvent {

    private final RaidBattleManager raidBattleManager;


    public RaidBattleNormallyFinishEvent(Object source,RaidBattleManager raidBattleManager) {
        super(source);
        this.raidBattleManager = raidBattleManager;
    }
}
