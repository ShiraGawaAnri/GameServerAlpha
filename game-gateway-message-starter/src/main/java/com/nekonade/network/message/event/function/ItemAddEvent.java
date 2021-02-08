package com.nekonade.network.message.event.function;

import com.nekonade.network.message.manager.PlayerManager;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

public class ItemAddEvent extends ApplicationEvent {

    @Getter
    private final PlayerManager playerManager;

    @Getter
    @Setter
    private String itemId;

    @Getter
    @Setter
    private int count;

    public ItemAddEvent(Object source, PlayerManager playerManager, String itemId, int count) {
        super(source);
        this.playerManager = playerManager;
        this.itemId = itemId;
        this.count = count;
    }
}
