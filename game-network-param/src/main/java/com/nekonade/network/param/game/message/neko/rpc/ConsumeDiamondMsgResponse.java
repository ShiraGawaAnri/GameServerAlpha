package com.nekonade.network.param.game.message.neko.rpc;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 210, messageType = EnumMesasageType.RPC_RESPONSE, serviceId = 102)
public class ConsumeDiamondMsgResponse  extends AbstractJsonGameMessage<ConsumeDiamondMsgResponse.ResponseBody> {

    public static class ResponseBody {
        
    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
