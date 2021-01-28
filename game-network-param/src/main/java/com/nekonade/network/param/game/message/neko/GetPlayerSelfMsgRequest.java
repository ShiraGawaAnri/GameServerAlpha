package com.nekonade.network.param.game.message.neko;

import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 202, messageType = EnumMesasageType.REQUEST, serviceId = 101)
public class GetPlayerSelfMsgRequest extends AbstractJsonGameMessage<GetPlayerSelfMsgRequest.RequestBody> {

    @Getter
    @Setter
    public static class RequestBody {

    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }

}
