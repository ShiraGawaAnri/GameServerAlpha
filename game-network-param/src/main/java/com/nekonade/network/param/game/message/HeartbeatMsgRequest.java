package com.nekonade.network.param.game.message;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId=2,messageType= EnumMesasageType.REQUEST,serviceId=1)
public class HeartbeatMsgRequest extends AbstractJsonGameMessage<Void> {

    @Override
    protected Class<Void> getBodyObjClass() {
        return null;
    }

}
