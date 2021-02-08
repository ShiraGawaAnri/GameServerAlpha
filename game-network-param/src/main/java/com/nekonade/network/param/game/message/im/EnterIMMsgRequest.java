package com.nekonade.network.param.game.message.im;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 310, messageType = EnumMesasageType.REQUEST, serviceId = 103)
public class EnterIMMsgRequest extends AbstractJsonGameMessage<EnterIMMsgRequest.EnterIMMsgBody> {
    @Override
    protected Class<EnterIMMsgBody> getBodyObjClass() {
        return null;
    }

    public static class EnterIMMsgBody {

    }

}
