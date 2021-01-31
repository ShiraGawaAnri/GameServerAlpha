package com.nekonade.network.message.event.basic;

public class GetArenaPlayerEventUser extends UserBasicEvent {

    private Long playerId;

    public GetArenaPlayerEventUser(Long playerId) {
        super();
        this.playerId = playerId;
    }

    public Long getPlayerId() {
        return playerId;
    }

}
