package com.nekonade.neko.logic.event;

public class GetArenaPlayerEvent {

    private Long playerId;

    public GetArenaPlayerEvent(Long playerId) {
        super();
        this.playerId = playerId;
    }

    public Long getPlayerId() {
        return playerId;
    }

}
