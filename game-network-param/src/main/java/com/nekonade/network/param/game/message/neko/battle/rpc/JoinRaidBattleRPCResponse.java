package com.nekonade.network.param.game.message.neko.battle.rpc;


import com.nekonade.common.dto.Player;
import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 1000, messageType = EnumMesasageType.RPC_RESPONSE, serviceId = 102)
public class JoinRaidBattleRPCResponse extends AbstractJsonGameMessage<JoinRaidBattleRPCResponse.ResponseBody> {

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }

    @Getter
    @Setter
    public static class ResponseBody {

        private String raidId;

        private long timestamp;

        private Player player;
    }

}



