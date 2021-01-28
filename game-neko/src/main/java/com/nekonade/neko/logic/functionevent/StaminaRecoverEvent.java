package com.nekonade.neko.logic.functionevent;

import com.nekonade.dao.db.entity.manager.PlayerManager;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class StaminaRecoverEvent extends ApplicationEvent {

    @Getter
    private PlayerManager playerManager;

    public StaminaRecoverEvent(Object source,PlayerManager playerManager) {
        super(source);
        this.playerManager = playerManager;
    }
}
