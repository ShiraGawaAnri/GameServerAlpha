package com.nekonade.network.param.game.message.neko.battle;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 1001, messageType = EnumMesasageType.RPC_REQUEST, serviceId = 102)
public class RaidBattleCardAttackMsgRequest extends AbstractJsonGameMessage<RaidBattleCardAttackMsgRequest.RequestBody> {

    @Override
    protected Class<RaidBattleCardAttackMsgRequest.RequestBody> getBodyObjClass() {
        return null;
    }

    @Getter
    @Setter
    public static class RequestBody {

        private String raidId;

        private long playerId;

        private long turn;

        private long timestamp;
    }
}



