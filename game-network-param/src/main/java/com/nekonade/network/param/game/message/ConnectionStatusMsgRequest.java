package com.nekonade.network.param.game.message;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId=2,messageType= EnumMesasageType.REQUEST,serviceId=1)
public class ConnectionStatusMsgRequest extends AbstractJsonGameMessage<ConnectionStatusMsgRequest.MessageBody> {

    public static class MessageBody {
       
        private boolean connect;//true是连接成功，false是连接断开
        public boolean isConnect() {
            return connect;
        }

        public void setConnect(boolean connect) {
            this.connect = connect;
        }

    }

    @Override
    protected Class<MessageBody> getBodyObjClass() {
        return MessageBody.class;
    }


}
