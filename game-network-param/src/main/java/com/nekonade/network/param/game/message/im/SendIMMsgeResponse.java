package com.nekonade.network.param.game.message.im;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 311, messageType = EnumMessageType.RESPONSE, serviceId = 101)
public class SendIMMsgeResponse extends AbstractJsonGameMessage<SendIMMsgeResponse.IMMsgBody> {
    @Override
    protected Class<IMMsgBody> getBodyObjClass() {
        return IMMsgBody.class;
    }

    public static class IMMsgBody {
        private String chat;
        private String sender;//消息发送者，这里测试，使用昵称，也可以添加一些其它的信息，比如头像，等级等。

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
