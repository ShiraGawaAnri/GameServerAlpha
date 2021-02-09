package com.nekonade.network.param.game.message.im;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 311, messageType = EnumMessageType.REQUEST, serviceId = 101)
public class SendIMMsgRequest extends AbstractJsonGameMessage<SendIMMsgRequest.SendIMMsgBody> {

    @Override
    protected Class<SendIMMsgBody> getBodyObjClass() {
        return SendIMMsgBody.class;
    }

    public static class SendIMMsgBody {
        private String chat;

        public String getChat() {
            return chat;
        }

        public void setChat(String chat) {
            this.chat = chat;
        }
    }
}
