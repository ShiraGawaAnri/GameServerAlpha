package com.nekonade.network.param.game.message.neko;


import com.nekonade.common.dto.RaidBattleDTO;
import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 401, messageType = EnumMessageType.RESPONSE, serviceId = 101)
public class DoCreateBattleMsgResponse extends AbstractJsonGameMessage<DoCreateBattleMsgResponse.RaidBattle> {

    @Override
    protected Class<DoCreateBattleMsgResponse.RaidBattle> getBodyObjClass() {
        return RaidBattle.class;
    }

    @Getter
    @Setter
    public static class RaidBattle extends RaidBattleDTO {

    }
}