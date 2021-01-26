package com.nekonade.network.param.game.message.neko;



import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GameMessageMetadata(messageId = 203, messageType = EnumMesasageType.RESPONSE, serviceId = 101)
public class GetArenaPlayerListMsgResponse extends AbstractJsonGameMessage<GetArenaPlayerListMsgResponse.ResponseBody> {
    public static class ResponseBody {
        private List<ArenaPlayer> arenaPlayers;

        public List<ArenaPlayer> getArenaPlayers() {
            return arenaPlayers;
        }

        public void setArenaPlayers(List<ArenaPlayer> arenaPlayers) {
            this.arenaPlayers = arenaPlayers;
        }


    }
    public static class ArenaPlayer {
        private long playerId;
        private String nickName;
        private Map<String, String> heros = new HashMap<>();

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
