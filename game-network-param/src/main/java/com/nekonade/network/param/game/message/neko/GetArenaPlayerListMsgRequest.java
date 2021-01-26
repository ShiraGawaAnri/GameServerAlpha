package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 203, messageType = EnumMesasageType.REQUEST, serviceId = 101)
public class GetArenaPlayerListMsgRequest extends AbstractJsonGameMessage<GetArenaPlayerListMsgRequest.RequestBody> {
    public static class RequestBody {
        
    }
   

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return null;
    }
}
