package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 204, messageType = EnumMessageType.REQUEST, serviceId = 101)
public class GetStaminaMsgRequest extends AbstractJsonGameMessage<GetStaminaMsgRequest.RequestBody> {

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }

    @Getter
    @Setter
    public static class RequestBody {
    }
}
