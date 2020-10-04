package com.nekonade.network.param.game.message.xinyue;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

import java.util.Map;

@GameMessageMetadata(messageId = 202, messageType = EnumMesasageType.RESPONSE, serviceId = 101)
public class GetPlayerByIdMsgResponse extends AbstractJsonGameMessage<GetPlayerByIdMsgResponse.ResponseBody> {
    public static class ResponseBody {
        private long playerId;
        private String nickName;
        private Map<String, String> heros;

        public long getPlayerId() {
            return playerId;
        }

        public void setPlayerId(long playerId) {
            this.playerId = playerId;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public Map<String, String> getHeros() {
            return heros;
        }

        public void setHeros(Map<String, String> heros) {
            this.heros = heros;
        }


    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
