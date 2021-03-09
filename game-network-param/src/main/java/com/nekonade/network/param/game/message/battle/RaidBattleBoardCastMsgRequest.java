package com.nekonade.network.param.game.message.battle;


import com.nekonade.common.dto.RaidBattleDTO;
import com.nekonade.common.gameMessage.AbstractJsonGameMessage;
import com.nekonade.common.gameMessage.EnumMessageGroup;
import com.nekonade.common.gameMessage.EnumMessageType;
import com.nekonade.common.gameMessage.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 1010, messageType = EnumMessageType.REQUEST, serviceId = 102,groupId = EnumMessageGroup.RAIDBATTLE)
public class RaidBattleBoardCastMsgRequest extends AbstractJsonGameMessage<RaidBattleBoardCastMsgRequest.ResponseBody> {

    @Override
    protected Class<RaidBattleBoardCastMsgRequest.ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }

    @Getter
    @Setter
    public static class ResponseBody extends RaidBattleDTO {

    }
}



