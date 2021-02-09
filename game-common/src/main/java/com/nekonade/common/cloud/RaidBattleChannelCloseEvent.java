package com.nekonade.common.cloud;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class RaidBattleChannelCloseEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    @Getter
    private final String raidId;

    public RaidBattleChannelCloseEvent(Object source, String raidId) {
        super(source);
        this.raidId = raidId;
    }

}
