package com.nekonade.network.param.game.message.neko;

import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 202, messageType = EnumMesasageType.RESPONSE, serviceId = 101)
public class GetPlayerSelfMsgResponse extends AbstractJsonGameMessage<GetPlayerSelfMsgResponse.ResponseBody> {

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }

    @Getter
    @Setter
    public static class ResponseBody {

        private Long playerId;

        private String zoneId;

        private String nickname;

        private int level;

        private long lastLoginTime;

        private long createTime;


    }
}
