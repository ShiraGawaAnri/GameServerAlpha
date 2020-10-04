package com.mygame.game.message.xinyue;

import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.message.xinyue.EnterGameMsgRequest.RequestBody;
import com.xinyue.network.message.game.EnumMesasageType;
import com.xinyue.network.message.game.GameMessageMetadata;
@GameMessageMetadata(messageId=201,messageType= EnumMesasageType.REQUEST,serviceId = 101)
public class EnterGameMsgRequest extends AbstractJsonGameMessage<RequestBody>{

    public static class RequestBody{
        
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return null;
    }
}
