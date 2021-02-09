package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 210, messageType = EnumMessageType.REQUEST, serviceId = 102)
public class BuyArenaChallengeTimesMsgRequest extends AbstractJsonGameMessage<BuyArenaChallengeTimesMsgRequest.RequestBody> {

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return null;
    }

    public static class RequestBody {

    }
}
