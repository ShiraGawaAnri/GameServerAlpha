package com.nekonade.gamegateway.messages;


import com.nekonade.network.message.game.AbstractJsonGameMessage;
import com.nekonade.network.message.game.EnumMesasageType;
import com.nekonade.network.message.game.GameMessageMetadata;

@GameMessageMetadata(messageId=2,messageType= EnumMesasageType.REQUEST,serviceId=0)
public class HeartbeatMsgRequest extends AbstractJsonGameMessage<Void,HeartbeatMsgResponse> {

    @Override
    protected Class<Void> getBodyObjClass() {
        return null;
    }

	@Override
	protected HeartbeatMsgResponse newCouple() {
		return new HeartbeatMsgResponse();
	}

}
