package com.nekonade.raidbattle.event.function;

import com.nekonade.common.gameMessage.IGameMessage;
import com.nekonade.raidbattle.manager.RaidBattleManager;
import com.nekonade.raidbattle.message.context.RaidBattleMessageContext;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PushRaidBattleToSinglePlayerEvent extends ApplicationEvent {

    private final RaidBattleManager raidBattleManager;

    private final long playerId;

    private final RaidBattleMessageContext<RaidBattleManager> ctx;

    private final IGameMessage request;

    public PushRaidBattleToSinglePlayerEvent(Object source, RaidBattleMessageContext<RaidBattleManager> ctx, IGameMessage request) {
        super(source);
        this.ctx = ctx;
        this.raidBattleManager = ctx.getDataManager();
        this.playerId = ctx.getPlayerId();
        this.request = request;
    }
}
