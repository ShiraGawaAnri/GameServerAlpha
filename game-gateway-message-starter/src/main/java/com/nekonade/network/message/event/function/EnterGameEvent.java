package com.nekonade.network.message.event.function;

import com.nekonade.network.message.manager.PlayerManager;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class EnterGameEvent extends ApplicationEvent {

    @Getter
    private final PlayerManager playerManager;

    public EnterGameEvent(Object source, PlayerManager playerManager) {
        super(source);
        this.playerManager = playerManager;
    }

}
