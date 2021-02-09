package com.nekonade.network.param.game.message.neko.rpc;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 210, messageType = EnumMessageType.RPC_REQUEST, serviceId = 101)
public class ConsumeDiamondMsgRequest extends AbstractJsonGameMessage<ConsumeDiamondMsgRequest.RequestBody> {

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return null;
    }

    public static class RequestBody {

        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

    }
}
