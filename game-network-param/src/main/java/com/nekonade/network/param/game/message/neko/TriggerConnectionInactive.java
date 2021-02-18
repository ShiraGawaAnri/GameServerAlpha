package com.nekonade.network.param.game.message.neko;

import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 9, messageType = EnumMessageType.REQUEST, serviceId = 101)
public class TriggerConnectionInactive extends AbstractJsonGameMessage<TriggerConnectionInactive.RequestBody> {

    @Override
    protected Class<TriggerConnectionInactive.RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }

    @Getter
    @Setter
    public static class RequestBody {

        private long playerId;

        private int serverId;
    }
}
