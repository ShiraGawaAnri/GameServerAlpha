package com.nekonade.network.param.game.message.battle;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageGroup;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 1002, messageType = EnumMessageType.REQUEST, serviceId = 102,groupId = EnumMessageGroup.RAIDBATTLE)
public class RaidBattleCardAttackMsgRequest extends AbstractJsonGameMessage<RaidBattleCardAttackMsgRequest.RequestBody> {

    @Override
    protected Class<RaidBattleCardAttackMsgRequest.RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }

    @Getter
    @Setter
    public static class RequestBody {

        private int chara;

        private int cardId;

        private long turn;

        private long timestamp;
    }
}



