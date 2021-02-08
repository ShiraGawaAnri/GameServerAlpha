package com.nekonade.network.message.event.basic;

import com.nekonade.network.param.game.message.neko.CreateBattleMsgRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBattleEventUser extends UserBasicEvent {

    private long playerId;

    private CreateBattleMsgRequest request;

    public CreateBattleEventUser(long playerId, CreateBattleMsgRequest request) {
        this.playerId = playerId;
        this.request = request;
    }
}
