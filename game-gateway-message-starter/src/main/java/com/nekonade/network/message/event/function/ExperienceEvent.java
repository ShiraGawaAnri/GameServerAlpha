package com.nekonade.network.message.event.function;

import com.nekonade.network.message.manager.PlayerManager;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ExperienceEvent extends ApplicationEvent {

    @Getter
    private PlayerManager playerManager;

    public ExperienceEvent(Object source, PlayerManager playerManager) {
        super(source);
        this.playerManager = playerManager;
    }
}
