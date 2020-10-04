package com.nekonade.gamegateway.messages;


import com.nekonade.network.message.game.AbstractJsonGameMessage;
import com.nekonade.network.message.game.EnumMesasageType;
import com.nekonade.network.message.game.GameMessageMetadata;

@GameMessageMetadata(messageId=1,messageType= EnumMesasageType.RESPONSE,serviceId=0)
public class ConfirmMsgResponse extends AbstractJsonGameMessage<ConfirmMsgResponse.ResponseBody, ConfirmMsgRequest> {
    
    public static class ResponseBody{
  
        private String secretKey; //对称加密密钥，客户端需要使用非对称加密私钥解密才能获得。
        public String getSecretKey() {
            return secretKey;
        }
        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }

	@Override
	protected ConfirmMsgRequest newCouple() {
		return new ConfirmMsgRequest();
	}
}
