package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@GameMessageMetadata(messageId = 203, messageType = EnumMesasageType.REQUEST, serviceId = 101)
public class GetInventoryMsgRequest extends AbstractJsonGameMessage<GetInventoryMsgRequest.RequestBody> {

    @Getter
    @Setter
    public static class RequestBody {
        private List<Integer> filter;
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }
}
