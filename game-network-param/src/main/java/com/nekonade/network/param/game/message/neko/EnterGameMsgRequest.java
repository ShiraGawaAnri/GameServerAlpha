package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 201, messageType = EnumMessageType.REQUEST, serviceId = 101)
public class EnterGameMsgRequest extends AbstractJsonGameMessage<EnterGameMsgRequest.RequestBody> {

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return null;
    }

    public static class RequestBody {

    }
}
