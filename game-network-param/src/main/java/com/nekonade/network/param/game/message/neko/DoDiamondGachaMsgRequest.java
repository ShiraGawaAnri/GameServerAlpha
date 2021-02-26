package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@GameMessageMetadata(messageId = 501, messageType = EnumMessageType.REQUEST, serviceId = 101)
public class DoDiamondGachaMsgRequest extends AbstractJsonGameMessage<DoDiamondGachaMsgRequest.RequestBody> {

    @Override
    protected Class<DoDiamondGachaMsgRequest.RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }

    @Getter
    @Setter
    public static class RequestBody {

        @NonNull
        private String gachaPoolsId;

        private int type = 10;
    }
}
