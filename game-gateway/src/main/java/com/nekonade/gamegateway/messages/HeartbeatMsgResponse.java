package com.nekonade.gamegateway.messages;


import com.nekonade.network.message.game.AbstractJsonGameMessage;
import com.nekonade.network.message.game.EnumMesasageType;
import com.nekonade.network.message.game.GameMessageMetadata;

@GameMessageMetadata(messageId=2,messageType= EnumMesasageType.RESPONSE,serviceId=0)
public class HeartbeatMsgResponse extends AbstractJsonGameMessage<Void, HeartbeatMsgRequest> {

   

    @Override
    protected Class<Void> getBodyObjClass() {
        return null;
    }

	@Override
	protected HeartbeatMsgRequest newCouple() {
		return new HeartbeatMsgRequest();
	}
}
