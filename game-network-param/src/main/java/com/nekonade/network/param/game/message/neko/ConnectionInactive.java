package com.nekonade.network.param.game.message.neko;

import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 9, messageType = EnumMesasageType.REQUEST, serviceId = 101)
public class ConnectionInactive extends AbstractJsonGameMessage<ConnectionInactive.RequestBody> {

    @Override
    protected Class<ConnectionInactive.RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }

    @Getter
    @Setter
    public static class RequestBody {

        private long playerId;

        private int serverId;
    }
}
