package com.nekonade.network.param.game.message.neko;

import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 201, messageType = EnumMessageType.RESPONSE, serviceId = 101)
public class DoEnterGameMsgResponse extends AbstractJsonGameMessage<DoEnterGameMsgResponse.ResponseBody> {

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }

    @Getter
    @Setter
    public static class ResponseBody {

        private String nickname;

        private long playerId;
    }
}