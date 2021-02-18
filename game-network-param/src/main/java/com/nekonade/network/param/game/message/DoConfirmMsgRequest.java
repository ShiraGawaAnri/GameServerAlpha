package com.nekonade.network.param.game.message;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 1, messageType = EnumMessageType.REQUEST, serviceId = 1)
public class DoConfirmMsgRequest extends AbstractJsonGameMessage<DoConfirmMsgRequest.ConfirmBody> {

    @Override
    protected Class<ConfirmBody> getBodyObjClass() {
        return ConfirmBody.class;
    }

    public static class ConfirmBody {
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

}
