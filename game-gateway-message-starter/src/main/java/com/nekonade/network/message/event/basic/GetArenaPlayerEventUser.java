package com.nekonade.network.message.event.basic;

public class GetArenaPlayerEventUser extends UserBasicEvent {

    private final Long playerId;

    public GetArenaPlayerEventUser(Long playerId) {
        super();
        this.playerId = playerId;
    }

    public Long getPlayerId() {
        return playerId;
    }

}
