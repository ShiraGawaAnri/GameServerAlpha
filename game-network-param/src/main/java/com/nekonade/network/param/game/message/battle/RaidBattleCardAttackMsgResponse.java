package com.nekonade.network.param.game.message.battle;


import com.nekonade.common.dto.RaidBattleDTO;
import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageGroup;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 1002, messageType = EnumMessageType.RESPONSE, serviceId = 102,groupId = EnumMessageGroup.RAIDBATTLE)
public class RaidBattleCardAttackMsgResponse extends AbstractJsonGameMessage<RaidBattleCardAttackMsgResponse.ResponseBody> {

    @Override
    protected Class<RaidBattleCardAttackMsgResponse.ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }

    @Getter
    @Setter
    public static class ResponseBody extends RaidBattleDTO {

    }
}



