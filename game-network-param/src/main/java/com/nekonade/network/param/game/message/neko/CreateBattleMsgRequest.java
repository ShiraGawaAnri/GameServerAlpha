package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 401, messageType = EnumMessageType.REQUEST, serviceId = 101)
public class CreateBattleMsgRequest extends AbstractJsonGameMessage<CreateBattleMsgRequest.RequestBody> {


    @Override
    protected Class<CreateBattleMsgRequest.RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }

    @Getter
    @Setter
    public static class RequestBody {

        private int area;

        private int episode;

        private int chapter;

        private int stage;

        private int difficulty;

    }
}
