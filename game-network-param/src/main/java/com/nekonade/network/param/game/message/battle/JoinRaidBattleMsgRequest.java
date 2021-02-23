package com.nekonade.network.param.game.message.battle;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageGroup;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 1000, messageType = EnumMessageType.REQUEST, serviceId = 102,groupId = EnumMessageGroup.RAIDBATTLE)
public class JoinRaidBattleMsgRequest extends AbstractJsonGameMessage<JoinRaidBattleMsgRequest.RequestBody> {

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }

    @Getter
    @Setter
    public static class RequestBody {

        private String raidId;

        private long playerId;

        private long timestamp;
    }
}



