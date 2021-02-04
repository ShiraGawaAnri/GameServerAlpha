package com.nekonade.network.param.game.message.neko;



import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GameMessageMetadata(messageId = 303, messageType = EnumMesasageType.RESPONSE, serviceId = 101)
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

    @Getter
    @Setter
    public static class ArenaPlayer {

        private long playerId;

        private String nickName;

        private Map<String, String> heros = new HashMap<>();

    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
