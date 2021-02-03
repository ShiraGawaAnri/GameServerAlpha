package com.nekonade.network.param.game.message.neko;

import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 201,messageType= EnumMesasageType.RESPONSE,serviceId=101)
public class EnterGameMsgResponse extends AbstractJsonGameMessage<EnterGameMsgResponse.ResponseBody> {

    public static class ResponseBody {
        private String nickname;
        private long playerId;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public long getPlayerId() {
            return playerId;
        }

        public void setPlayerId(long playerId) {
            this.playerId = playerId;
        }
    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
