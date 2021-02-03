package com.nekonade.network.param.game.message.neko;

import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 201,messageType= EnumMesasageType.RESPONSE,serviceId=101)
public class EnterGameMsgResponse extends AbstractJsonGameMessage<EnterGameMsgResponse.ResponseBody> {

    @Getter
    @Setter
    public static class ResponseBody {

        private String nickname;

        private long playerId;
    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
