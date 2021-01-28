package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 204, messageType = EnumMesasageType.RESPONSE, serviceId = 101)
public class GetStaminaMsgResponse extends AbstractJsonGameMessage<GetStaminaMsgResponse.RequestBody> {

    @Getter
    @Setter
    public static class RequestBody {
        private Object stamina;
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }
}
