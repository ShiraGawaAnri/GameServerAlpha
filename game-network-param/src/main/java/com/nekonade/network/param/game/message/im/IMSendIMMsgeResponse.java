package com.nekonade.network.param.game.message.im;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 312, messageType = EnumMesasageType.RESPONSE, serviceId = 103)
public class IMSendIMMsgeResponse extends AbstractJsonGameMessage<IMSendIMMsgeResponse.IMMsgBody> {
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
