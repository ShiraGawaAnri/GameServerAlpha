package com.nekonade.network.param.game.message.neko;

import com.nekonade.common.dto.RaidBattleRewardDTO;
import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@GameMessageMetadata(messageId = 308, messageType = EnumMessageType.RESPONSE, serviceId = 101)
public class DoClaimRaidBattleRewardMsgResponse extends AbstractJsonGameMessage<DoClaimRaidBattleRewardMsgResponse.ResponseBody> {

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }

    @Getter
    @Setter
    public static class ResponseBody extends RaidBattleRewardDTO {


    }
}
