package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 204, messageType = EnumMesasageType.REQUEST, serviceId = 101)
public class GetStaminaMsgRequest extends AbstractJsonGameMessage<GetStaminaMsgRequest.RequestBody> {

    @Getter
    @Setter
    public static class RequestBody {
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }
}
