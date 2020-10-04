package com.nekonade.network.param.game.rpc;

import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 204, messageType = EnumMesasageType.RPC_REQUEST, serviceId = 101)
public class ConsumeDiamondRPCRequest extends AbstractJsonGameMessage<ConsumeDiamondRPCRequest.RequestBody> {
    public static class RequestBody {
        private int consumeCount;

        public int getConsumeCount() {
            return consumeCount;
        }
        public void setConsumeCount(int consumeCount) {
            this.consumeCount = consumeCount;
        }
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }



}
