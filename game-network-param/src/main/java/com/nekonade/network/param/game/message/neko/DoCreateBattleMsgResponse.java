package com.nekonade.network.param.game.message.neko;


import com.nekonade.common.dto.RaidBattleDTO;
import com.nekonade.common.gameMessage.AbstractJsonGameMessage;
import com.nekonade.common.gameMessage.EnumMessageType;
import com.nekonade.common.gameMessage.GameMessageMetadata;
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
