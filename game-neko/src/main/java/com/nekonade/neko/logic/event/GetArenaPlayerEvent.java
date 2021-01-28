package com.nekonade.neko.logic.event;

public class GetArenaPlayerEvent extends BasicEvent{

    private Long playerId;

    public GetArenaPlayerEvent(Long playerId) {
        super();
        this.playerId = playerId;
    }

    public Long getPlayerId() {
        return playerId;
    }

}
