package com.nekonade.network.param.game.message.neko;


import com.nekonade.common.gameMessage.AbstractJsonGameMessage;
import com.nekonade.common.gameMessage.EnumMessageType;
import com.nekonade.common.gameMessage.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@GameMessageMetadata(messageId = 206, messageType = EnumMessageType.REQUEST, serviceId = 101)
public class GetMailBoxMsgRequest extends AbstractJsonGameMessage<GetMailBoxMsgRequest.RequestBody> {

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }

    @Getter
    @Setter
    public static class RequestBody {

        private List<Integer> filter;

        private Integer page;

        private Integer limit;

        private Integer sort;

    }

}
