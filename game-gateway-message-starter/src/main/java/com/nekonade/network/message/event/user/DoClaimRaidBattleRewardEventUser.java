package com.nekonade.network.message.event.user;

import com.nekonade.network.param.game.message.neko.DoReceiveMailMsgRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DoClaimRaidBattleRewardEventUser extends BasicEventUser {

    private final String raidId;

    public DoClaimRaidBattleRewardEventUser(String raidId) {
        this.raidId = raidId;
    }
}
