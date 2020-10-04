package com.nekonade.network.param.game.message.xinyue;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId=201,messageType= EnumMesasageType.REQUEST,serviceId = 101)
public class EnterGameMsgRequest extends AbstractJsonGameMessage<EnterGameMsgRequest.RequestBody> {

    public static class RequestBody{
        
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return null;
    }
}
