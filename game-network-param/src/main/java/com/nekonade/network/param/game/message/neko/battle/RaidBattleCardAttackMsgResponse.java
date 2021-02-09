package com.nekonade.network.param.game.message.neko.battle;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 1001, messageType = EnumMessageType.RPC_RESPONSE, serviceId = 102)
public class RaidBattleCardAttackMsgResponse extends AbstractJsonGameMessage<RaidBattleCardAttackMsgResponse.ResponseBody> {

    @Override
    protected Class<RaidBattleCardAttackMsgResponse.ResponseBody> getBodyObjClass() {
        return null;
    }

    @Getter
    @Setter
    public static class ResponseBody {

        private Object result;
    }
}



