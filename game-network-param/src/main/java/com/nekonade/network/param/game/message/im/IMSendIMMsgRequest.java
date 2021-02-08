package com.nekonade.network.param.game.message.im;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 312, messageType = EnumMesasageType.REQUEST, serviceId = 103)
public class IMSendIMMsgRequest extends AbstractJsonGameMessage<IMSendIMMsgRequest.SendIMMsgBody> {

    @Override
    protected Class<SendIMMsgBody> getBodyObjClass() {
        return SendIMMsgBody.class;
    }

    public static class SendIMMsgBody {
        private String chat;
        private String sender;

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getChat() {
            return chat;
        }

        public void setChat(String chat) {
            this.chat = chat;
        }
    }
}
