package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 210, messageType = EnumMesasageType.REQUEST, serviceId = 102)
public class BuyArenaChallengeTimesMsgRequest extends AbstractJsonGameMessage<BuyArenaChallengeTimesMsgRequest.RequestBody> {
    
    public static class RequestBody {
        
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return null;
    }
}
