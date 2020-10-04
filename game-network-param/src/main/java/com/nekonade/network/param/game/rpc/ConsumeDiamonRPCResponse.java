package com.nekonade.network.param.game.rpc;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 204, messageType = EnumMesasageType.RPC_RESPONSE, serviceId = 102) // 返回的服务id是102服务
public class ConsumeDiamonRPCResponse extends AbstractJsonGameMessage<ConsumeDiamonRPCResponse.ResponseBody> {
    public static class ResponseBody {
        
    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return null;
    }
}
