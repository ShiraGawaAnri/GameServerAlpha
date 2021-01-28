package com.nekonade.neko.logic.event;

import lombok.Getter;

@Getter
public class GetPlayerInfoEvent extends BasicEvent{

    private Long playerId;

    public GetPlayerInfoEvent(Long playerId) {
        super();
        this.playerId = playerId;
    }

}
