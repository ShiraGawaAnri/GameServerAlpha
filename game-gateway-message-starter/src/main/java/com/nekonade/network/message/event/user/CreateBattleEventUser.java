package com.nekonade.network.message.event.user;

import com.nekonade.network.param.game.message.neko.DoCreateBattleMsgRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBattleEventUser extends BasicEventUser {

    private long playerId;

    private DoCreateBattleMsgRequest request;

    public CreateBattleEventUser(long playerId, DoCreateBattleMsgRequest request) {
        this.playerId = playerId;
        this.request = request;
    }
}
