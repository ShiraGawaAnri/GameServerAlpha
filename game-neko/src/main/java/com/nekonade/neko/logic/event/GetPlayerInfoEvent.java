package com.nekonade.neko.logic.event;

import lombok.Getter;

@Getter
public class GetPlayerInfoEvent {

    private Long playerId;

    public GetPlayerInfoEvent(Long playerId) {
        super();
        this.playerId = playerId;
    }

}
