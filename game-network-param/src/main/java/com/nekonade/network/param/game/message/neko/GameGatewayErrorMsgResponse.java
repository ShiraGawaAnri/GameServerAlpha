package com.nekonade.network.param.game.message.neko;


import com.nekonade.common.error.GameErrorException;
import com.nekonade.common.error.IServerError;
import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 4, messageType = EnumMesasageType.RESPONSE, serviceId = 1)
public class GameGatewayErrorMsgResponse extends AbstractJsonGameMessage<GameGatewayErrorMsgResponse.RequestBody> {

    @Getter
    @Setter
    public static class RequestBody {

        private Object error;
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }
}
