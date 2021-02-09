package com.nekonade.network.param.game.message;

import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 2, messageType = EnumMessageType.RESPONSE, serviceId = 1)
public class HeartbeatMsgResponse extends AbstractJsonGameMessage<HeartbeatMsgResponse.ResponseBody> {

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }

    public static class ResponseBody {
        private long serverTime;

        public long getServerTime() {
            return serverTime;
        }

        public void setServerTime(long serverTime) {
            this.serverTime = serverTime;
        }

    }
}
