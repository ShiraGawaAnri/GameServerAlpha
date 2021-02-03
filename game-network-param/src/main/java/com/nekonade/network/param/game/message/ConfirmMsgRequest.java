package com.nekonade.network.param.game.message;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 1,messageType= EnumMesasageType.REQUEST,serviceId=1)
public class ConfirmMsgRequest extends AbstractJsonGameMessage<ConfirmMsgRequest.ConfirmBody> {
    
    public static class ConfirmBody {
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    @Override
    protected Class<ConfirmBody> getBodyObjClass() {
        return ConfirmBody.class;
    }

}
