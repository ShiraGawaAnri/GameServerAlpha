package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 202, messageType = EnumMesasageType.REQUEST, serviceId = 101)
public class GetPlayerByIdMsgRequest extends AbstractJsonGameMessage<GetPlayerByIdMsgRequest.RequestBody> {
    public static class RequestBody {
        private int playerId;

        public int getPlayerId() {
            return playerId;
        }

        public void setPlayerId(int playerId) {
            this.playerId = playerId;
        }


    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }
}
