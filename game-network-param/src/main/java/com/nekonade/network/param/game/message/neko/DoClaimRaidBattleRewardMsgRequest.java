package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@GameMessageMetadata(messageId = 308, messageType = EnumMessageType.REQUEST, serviceId = 101)
public class DoClaimRaidBattleRewardMsgRequest extends AbstractJsonGameMessage<DoClaimRaidBattleRewardMsgRequest.RequestBody> {

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }


    @Getter
    @Setter
    public static class RequestBody {

        @NonNull
        private String raidId;
    }
}
