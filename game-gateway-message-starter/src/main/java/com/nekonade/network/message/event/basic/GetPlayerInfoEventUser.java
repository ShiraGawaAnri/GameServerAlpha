package com.nekonade.network.message.event.basic;

import lombok.Getter;

@Getter
public class GetPlayerInfoEventUser extends UserBasicEvent {

    private Long playerId;

    public GetPlayerInfoEventUser(Long playerId) {
        super();
        this.playerId = playerId;
    }

}
