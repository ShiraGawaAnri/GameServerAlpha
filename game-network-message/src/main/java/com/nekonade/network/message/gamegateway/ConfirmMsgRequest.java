package com.nekonade.gamegateway.messages;


import com.nekonade.network.message.game.AbstractJsonGameMessage;
import com.nekonade.network.message.game.EnumMesasageType;
import com.nekonade.network.message.game.GameMessageMetadata;

@GameMessageMetadata(messageId= 1,desc = "连接认证请求消息",messageType= EnumMesasageType.REQUEST,serviceId=0)
public class ConfirmMsgRequest extends AbstractJsonGameMessage<ConfirmMsgRequest.ConfirmBody, ConfirmMsgResponse> {
    
    public static class ConfirmBody {
        private String token;

        public ConfirmBody() {
            
        }
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

	@Override
	protected ConfirmMsgResponse newCouple() {
		return new ConfirmMsgResponse();
	}

}
