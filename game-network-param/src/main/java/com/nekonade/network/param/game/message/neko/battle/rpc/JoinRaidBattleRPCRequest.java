package com.nekonade.network.param.game.message.neko.battle.rpc;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 1000, messageType = EnumMesasageType.RPC_REQUEST, serviceId = 101)
public class JoinRaidBattleRPCRequest extends AbstractJsonGameMessage<JoinRaidBattleRPCRequest.RequestBody> {

    @Override
    protected Class<JoinRaidBattleRPCRequest.RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }

    @Getter
    @Setter
    public static class RequestBody {

        private String raidId;

        private long playerId;

        private long timestamp;
    }
}



