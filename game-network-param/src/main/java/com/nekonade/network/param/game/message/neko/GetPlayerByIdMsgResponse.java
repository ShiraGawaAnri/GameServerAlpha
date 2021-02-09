package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@GameMessageMetadata(messageId = 302, messageType = EnumMessageType.RESPONSE, serviceId = 101)
public class GetPlayerByIdMsgResponse extends AbstractJsonGameMessage<GetPlayerByIdMsgResponse.ResponseBody> {

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }

    @Getter
    @Setter
    public static class ResponseBody {

        private long playerId;

        private String nickName;

        private Map<String, String> heros;


    }
}
