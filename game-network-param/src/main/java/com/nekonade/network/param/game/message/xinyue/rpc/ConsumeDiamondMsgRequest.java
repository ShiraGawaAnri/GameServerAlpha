package com.nekonade.network.param.game.message.xinyue.rpc;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 210, messageType = EnumMesasageType.RPC_REQUEST, serviceId = 101)
public class ConsumeDiamondMsgRequest extends AbstractJsonGameMessage<ConsumeDiamondMsgRequest.RequestBody> {

    public static class RequestBody {
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
        
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return null;
    }
}
