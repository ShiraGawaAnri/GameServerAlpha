package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 207, messageType = EnumMessageType.REQUEST, serviceId = 101)
public class GetRaidBattleListMsgRequest extends AbstractJsonGameMessage<GetRaidBattleListMsgRequest.RequestBody> {

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }

    @Getter
    @Setter
    public static class RequestBody {

        private boolean finish = false;

        private int page = 1;

        private int limit = 10;

        private int sort = 1;

    }

}
