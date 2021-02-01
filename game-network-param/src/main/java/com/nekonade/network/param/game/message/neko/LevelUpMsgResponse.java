package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 205, messageType = EnumMesasageType.RESPONSE, serviceId = 101)
public class LevelUpMsgResponse extends AbstractJsonGameMessage<LevelUpMsgResponse.RequestBody> {

    @Getter
    @Setter
    public static class RequestBody {
        private Object data;
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }
}
